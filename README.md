# Shopping Cart (SEP2 Assignments)

This repository contains both required assignments:

- In-class GUI Localization assignment (Week 2)
- OTP2 Database Localization extension (Week 3)

## Features

- JavaFX GUI shopping cart
- Language selector (English, Finnish, Swedish, Japanese, Arabic)
- Dynamic UI text localization
  - Reads from database (`localization_strings`)
  - Falls back to `MessagesBundle_*.properties` if DB is unavailable/missing keys
- Cart calculations
  - Item subtotal (`price * quantity`)
  - Overall total cost
- Database persistence
  - Saves cart header to `cart_records`
  - Saves each item to `cart_items` with FK to `cart_records`
- JUnit 5 unit tests + JaCoCo coverage
- Dockerfile and Jenkins pipeline for CI/CD

## Database Setup

Run SQL:

```sql
SOURCE src/main/resources/db/schema.sql;
```

Or execute the script content manually in MySQL/MariaDB.

## Configuration

The app reads DB connection from environment variables:

- `DB_URL` (default: `jdbc:mariadb://localhost:3306/shopping_cart_localization`)
- `DB_USER` (default: `root`)
- `DB_PASSWORD` (default: `root`)

## Build and Test

```bash
mvn clean test
mvn clean package
```

## Run GUI

```bash
mvn javafx:run
```

## Docker

Build image:

```bash
docker build -t <dockerhub-user>/shopping-cart:latest .
```

Run container:

```bash
docker run --rm -e DB_URL=jdbc:mariadb://host.docker.internal:3306/shopping_cart_localization -e DB_USER=root -e DB_PASSWORD=root <dockerhub-user>/shopping-cart:latest
```

## Jenkins

Pipeline stages in `Jenkinsfile`:

- Checkout
- Test (`mvn clean test`)
- Package (`mvn clean package`)
- Build Docker image
- Push Docker image to Docker Hub
