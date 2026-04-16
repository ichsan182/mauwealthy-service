# mauwealthy-service

Spring Boot Kotlin backend for persisting `db.json`-style user profile data into PostgreSQL.

## Run

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/mauwealthy_db"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="13qeadzc"
./gradlew.bat bootRun
```

Base URL: `http://localhost:8081`

## Main Endpoints (Postman)

- `POST /api/users` create full user profile (body uses Angular `db.json` shape).
- `GET /api/users` get all user profiles.
- `GET /api/users/{id}` get one user profile.
- `PUT /api/users/{id}` replace one full user profile.
- `DELETE /api/users/{id}` delete user and nested data.
- `GET /api/users/{id}/debts` get debt items for user.
- `POST /api/users/{id}/debts` add debt item for user.
- `GET /api/users/{id}/journal/chats?date=yyyy-MM-dd` get daily chat messages.
- `POST /api/users/{id}/journal/chats?date=yyyy-MM-dd` add one chat message.

## Notes

- JPA uses `ddl-auto=update`, so tables are created automatically on startup.
- For production, replace plain password storage with hashing and migrate schema with Flyway.
