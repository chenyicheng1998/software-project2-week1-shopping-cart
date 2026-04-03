# Shopping Cart Localization Project (SEP2 Week 2 + OTP2 Week 3)

## Implemented Features

- JavaFX GUI application
- Multi-item shopping cart flow:
  - Enter number of items
  - Enter item price and quantity
  - Add multiple items
  - Calculate item subtotals and overall total
- Localization support:
  - English (`en_US`)
  - Finnish (`fi_FI`)
  - Swedish (`sv_SE`)
  - Japanese (`ja_JP`)
  - Arabic (`ar_AR`) with RTL UI direction
- Localization source:
  - Reads from database table `localization_strings`
  - Falls back to `MessagesBundle_*.properties` if DB keys are missing
- Database persistence:
  - Saves shopping cart summary to `cart_records`
  - Saves item rows to `cart_items` (foreign key to `cart_records`)
- Unit tests (JUnit 5) and coverage (JaCoCo)
- CI/CD files: `Dockerfile`, `Jenkinsfile`

## Tech Stack

- Java 17
- Maven
- JavaFX 21
- MariaDB/MySQL
- JUnit 5 + JaCoCo

## Quick Start (Teacher Guide)

### 1) Prerequisites

- JDK 17
- Maven 3.9+
- MySQL or MariaDB

### 2) Clone and open project

```bash
git clone <your-repo-url>
cd shopping-cart
```

### 3) Create database schema

Use one of the following methods:

Option A (MySQL client):

```sql
SOURCE src/main/resources/db/schema.sql;
```

Option B: open `src/main/resources/db/schema.sql` and execute manually.

### 4) Configure database connection (optional)

By default, the app uses:

- `DB_URL=jdbc:mariadb://localhost:3306/shopping_cart_localization`
- `DB_USER=root`
- `DB_PASSWORD=root`

If your local credentials differ, set environment variables before running:

Windows PowerShell:

```powershell
$env:DB_URL="jdbc:mariadb://127.0.0.1:3306/shopping_cart_localization?useSsl=false&restrictedAuth=mysql_native_password"
$env:DB_USER="root"
$env:DB_PASSWORD="your_password"
```

Linux/macOS:

```bash
export DB_URL="jdbc:mariadb://127.0.0.1:3306/shopping_cart_localization?useSsl=false&restrictedAuth=mysql_native_password"
export DB_USER="root"
export DB_PASSWORD="your_password"
```

### 5) Run tests

```bash
mvn clean test
```

### 6) Run application (GUI)

```bash
mvn javafx:run
```

## How to Verify Functionality

1. Select a language from dropdown.
2. Enter number of items.
3. Enter price and quantity for each item, click `Add item`.
4. Click `Calculate total`.
5. Confirm results appear in UI and data is saved to DB.

Check database:

```sql
USE shopping_cart_localization;
SELECT * FROM cart_records ORDER BY id DESC;
SELECT * FROM cart_items ORDER BY id DESC;
```

## Reset Data

```sql
USE shopping_cart_localization;
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE cart_items;
TRUNCATE TABLE cart_records;
SET FOREIGN_KEY_CHECKS = 1;
```

## Docker

Build image:

```bash
docker build -t <dockerhub-user>/shopping-cart:latest .
```

Run image:

```bash
docker run --rm -e DB_URL=jdbc:mariadb://host.docker.internal:3306/shopping_cart_localization -e DB_USER=root -e DB_PASSWORD=your_password <dockerhub-user>/shopping-cart:latest
```

## Jenkins Pipeline

`Jenkinsfile` stages:

- Checkout
- Test (`mvn --batch-mode clean test`)
- Package (`mvn --batch-mode clean package -DskipTests`)
- Build Docker image
- Push Docker image to Docker Hub
