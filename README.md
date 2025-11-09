# EWM Main Service (without requests/compilations)

## Quickstart (Docker)
```bash
docker compose up -d --build
# app: http://localhost:8080
# db : localhost:5432 (ewm/ewm)
```

## Local run
1) Start Postgres (user/pass: ewm/ewm; db: ewm).
2) `mvn spring-boot:run`

Notes:
- Dates expected as `yyyy-MM-dd HH:mm:ss` (space, no `T`).
- `confirmedRequests` and `onlyAvailable` are stubbed (no requests module yet).
