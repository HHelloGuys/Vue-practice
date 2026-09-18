# Oracle 비밀번호를 화면과 명령 기록에 노출하지 않고 입력받는다.
$securePassword = Read-Host 'Oracle password' -AsSecureString
$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)

try {
    # Spring Boot가 현재 프로세스의 환경변수에서만 비밀번호를 읽도록 전달한다.
    $env:ORACLE_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)

    # 저장소에 포함된 Maven Wrapper로 eGovFramework 백엔드를 실행한다.
    & "$PSScriptRoot\mvnw.cmd" spring-boot:run
}
finally {
    # 백엔드 종료 후 평문 환경변수와 변환 메모리를 정리한다.
    Remove-Item Env:ORACLE_PASSWORD -ErrorAction SilentlyContinue
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
}
