# 이 프로젝트의 로컬 개발 서버가 사용하는 포트만 확인한다.
$developmentPorts = 5173, 8000, 8080
$allowedProcessNames = 'java', 'node', 'python', 'pythonw'
$processIds = @()

foreach ($port in $developmentPorts) {
    $listeners = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    $processIds += $listeners | Select-Object -ExpandProperty OwningProcess
}

$processIds = $processIds | Where-Object { $_ -gt 0 } | Sort-Object -Unique

if ($processIds.Count -eq 0) {
    Write-Host '종료할 Vue, FastAPI, eGovFramework 서버가 없습니다.'
    exit 0
}

foreach ($processId in $processIds) {
    $process = Get-Process -Id $processId -ErrorAction SilentlyContinue

    if ($null -eq $process) {
        continue
    }

    # 다른 프로그램이 같은 포트를 쓰는 경우에는 임의로 종료하지 않는다.
    if ($allowedProcessNames -notcontains $process.ProcessName) {
        Write-Warning "포트를 사용하는 프로세스를 종료하지 않았습니다: $($process.ProcessName) (PID $processId)"
        continue
    }

    Write-Host "종료: $($process.ProcessName) (PID $processId)"
    Stop-Process -Id $processId -Force
}

Write-Host '로컬 개발 서버 종료를 완료했습니다.'
