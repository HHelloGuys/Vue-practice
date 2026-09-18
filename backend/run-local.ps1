# Oracle 비밀번호를 화면과 명령 기록에 노출하지 않고 입력받는다.
$securePassword = Read-Host 'Oracle password' -AsSecureString
$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
$projectRoot = Split-Path $PSScriptRoot -Parent
$frontendProcess = $null
$aiProcess = $null
$ollamaProcess = $null

try {
    # Spring Boot가 현재 프로세스의 환경변수에서만 비밀번호를 읽도록 전달한다.
    $env:ORACLE_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)

    # 개발 서버를 다시 시작할 때마다 새로운 JWT 서명키를 생성한다.
    $jwtBytes = New-Object byte[] 32
    $randomGenerator = [Security.Cryptography.RandomNumberGenerator]::Create()
    $randomGenerator.GetBytes($jwtBytes)
    $randomGenerator.Dispose()
    $env:JWT_SECRET = [Convert]::ToBase64String($jwtBytes)

    # 기존 JRE 대신 컴파일러가 포함된 Temurin JDK 8을 현재 실행에만 적용한다.
    $jdk = Get-ChildItem 'C:\Program Files\Eclipse Adoptium' -Directory -Filter 'jdk-8*' |
        Where-Object { Test-Path (Join-Path $_.FullName 'bin\javac.exe') } |
        Sort-Object Name -Descending |
        Select-Object -First 1

    if ($null -eq $jdk) {
        throw 'Temurin JDK 8을 찾을 수 없습니다.'
    }

    $env:JAVA_HOME = $jdk.FullName
    $env:Path = "$($jdk.FullName)\bin;$env:Path"

    # Ollama가 실행 중이 아니면 설치된 로컬 실행 파일로 서버를 시작한다.
    $ollamaConnection = Test-NetConnection -ComputerName localhost -Port 11434 -WarningAction SilentlyContinue
    if (-not $ollamaConnection.TcpTestSucceeded) {
        $ollamaPath = Join-Path $env:LOCALAPPDATA 'Programs\Ollama\ollama.exe'

        if (-not (Test-Path $ollamaPath)) {
            throw 'Ollama 실행 파일을 찾을 수 없습니다.'
        }

        $ollamaProcess = Start-Process `
            -FilePath $ollamaPath `
            -ArgumentList 'serve' `
            -WindowStyle Hidden `
            -PassThru
    }

    # Vue와 FastAPI를 백그라운드로 실행하고 eGovFramework는 현재 터미널에서 실행한다.
    $frontendProcess = Start-Process `
        -FilePath 'npm.cmd' `
        -ArgumentList 'run', 'frontend' `
        -WorkingDirectory $projectRoot `
        -WindowStyle Hidden `
        -PassThru

    $aiProcess = Start-Process `
        -FilePath 'npm.cmd' `
        -ArgumentList 'run', 'ai' `
        -WorkingDirectory $projectRoot `
        -WindowStyle Hidden `
        -PassThru

    Write-Host 'Vue:          http://localhost:5173'
    Write-Host 'eGovFramework: http://localhost:8080'
    Write-Host 'FastAPI:       http://localhost:8000'
    Write-Host 'Ollama:       http://localhost:11434'

    # 저장소에 포함된 Maven Wrapper로 eGovFramework 백엔드를 실행한다.
    & "$PSScriptRoot\mvnw.cmd" -f "$PSScriptRoot\pom.xml" spring-boot:run
}
finally {
    # 이 스크립트가 시작한 개발 서버만 종료한다.
    if ($null -ne $frontendProcess -and -not $frontendProcess.HasExited) {
        Stop-Process -Id $frontendProcess.Id -Force
    }

    if ($null -ne $aiProcess -and -not $aiProcess.HasExited) {
        Stop-Process -Id $aiProcess.Id -Force
    }

    if ($null -ne $ollamaProcess -and -not $ollamaProcess.HasExited) {
        Stop-Process -Id $ollamaProcess.Id -Force
    }

    # 백엔드 종료 후 평문 환경변수와 변환 메모리를 정리한다.
    Remove-Item Env:ORACLE_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:JWT_SECRET -ErrorAction SilentlyContinue
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
}
