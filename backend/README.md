# eGovFramework Backend

## Prerequisites

- Java 8
- Oracle 21c XE (`localhost:1521/XEPDB1`)

## Local environment

The local runner prompts for the Oracle password. It is not written to a file.

```powershell
.\run-local.ps1
```

Optional overrides:

```powershell
$env:ORACLE_URL = 'jdbc:oracle:thin:@//localhost:1521/XEPDB1'
$env:ORACLE_USERNAME = 'system'
```

## Run

Verify the Oracle connection:

```text
GET http://localhost:8080/api/health/database
```
