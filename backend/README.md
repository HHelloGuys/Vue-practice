# eGovFramework Backend

## Prerequisites

- Java 8
- Oracle 21c XE (`localhost:1521/XEPDB1`)
- Ollama with `qwen3:4b-instruct`

## Local environment

The local runner prompts for the Oracle password. It is not written to a file.
The same command starts Vue, eGovFramework, and FastAPI together.

```powershell
.\run-local.ps1
```

Open the Vue screen:

```text
http://localhost:5173
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

## Tests

Backend unit tests use mocks and do not connect to Oracle or FastAPI.

```powershell
cd backend
.\mvnw.cmd test
```

AI unit tests mock Ollama and do not load the local model.

```powershell
cd ..
.\ai\.venv\Scripts\python.exe -m unittest discover -s ai/tests -v
```
