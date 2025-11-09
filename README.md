# URL Shortener

A minimal fullstack URL shortener with a Spring Boot backend and a Vite + React frontend.

<img src="assets/app-preview.png" alt="URL Shortener Screenshot" width="600" />

## Table of Contents

- [About](#about)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
  - [Prerequisites (for local development)](#prerequisites-for-local-development)
  - [Build and run locally](#build-and-run-locally)
    - [Prerequisites](#prerequisites)
    - [Repository](#repository)
    - [Backend](#backend)
    - [Frontend](#frontend)
  - [Docker (recommended)](#docker-recommended)
- [Usage](#usage)
- [Endpoints](#endpoints)
- [Notes and Assumptions](#notes-and-assumptions)
- [Quick command cheatsheet](#quick-command-cheatsheet)
- [Future Enhancements & Technical Trade-offs](#future-enhancements--technical-trade-offs)
  - [Tech Debt](#tech-debt)
  - [Future Enhancements](#future-enhancements)

## About

A minimal fullstack URL shortener with a Spring Boot backend and a Vite + React frontend.
This README explains how to build and run the project locally, shows example usage for the frontend and API,
and lists notes and assumptions used during development.

## Features

- Shorten long URLs to short aliases
- Custom alias support
- Redirect short URLs to original long URLs
- View and manage created short URLs via frontend UI
- REST API for programmatic access
- Basic validation and error handling
- Dockerized for easy deployment
- Simple file-based H2 database for development
- Swagger UI for API documentation and testing
- CORS support for frontend-backend communication

## Tech Stack

- **Backend:** Java, Spring Boot
- **Frontend:** React, TypeScript, Vite
- **Database:** H2 (file-based, for development)
- **Infrastructure:** Docker, Docker Compose

## Getting Started

### Prerequisites (for local development)

- Java 21+
- Node.js 20+ and npm
- Docker

### Build and run locally

This section explains running the project on your machine for development without Docker. It covers backend (Spring Boot) and frontend (React + Vite) steps, expected ports, environment variables, and quick troubleshooting.

#### Prerequisites
- Git
- Java 21+
- Node.js 20+ and npm

#### Repository
Assumes repo root contains `backend/` and `frontend/`.

#### Backend
1. Clone and open repo
```bash
  git clone https://github.com/acunil/url-shortener.git
  cd url-shortener/backend
```

2. Build and run
```bash
  ./mvnw clean install
  ./mvnw spring-boot:run
```

3. Access backend at http://localhost:8080
4. API docs (Swagger UI) at http://localhost:8080/swagger-ui.html

#### Frontend
1. Open new terminal, navigate to frontend
```bash
cd url-shortener/frontend
```
2. Install dependencies
```bash
npm install
```
3. Start development server
```bash
npm run dev
````
4. Access frontend at http://localhost:5173

### Docker (recommended)

1. Build and run with docker compose from repo root

```bash
docker compose build
docker compose up -d
```

2. Containers:
- backend: port 8080
- frontend: port 3000

3. Access frontend at http://localhost:3000 and backend at http://localhost:8080

## Usage

- Load the UI at the frontend URL (see ports above).
- Enter a long URL in the form with an optional alias and click Shorten URL.
- The new short link appears in the table. Click the copy icon to copy the short URL to clipboard.
- Click the short URL to test redirection to the original long URL.
- Delete aliases using the trash icon.

## Endpoints
- `POST /shorten` - Create a new short URL
    ```json
    {
      "fullUrl": "https://www.example.com/some/long/url",
      "customAlias": "myalias"
    }
    ```
- `GET /urls` - List all short URLs
- `DELETE /{alias}` - Delete a short URL by alias
- `GET /{alias}` - Redirect to original URL

    ```json
    {
      "alias": "myalias",
      "fullUrl": "https://www.example.com/some/long/url",
      "shortUrl": "http://localhost:8080/myalias"
    }
    ```


## Notes and Assumptions

- The repository uses a simple H2 file-based database for development and demonstration only. For production, migrate to a production-grade database.

- Two modes of frontend delivery:
  - Vite dev server for fast local development (http://localhost:5173).
  - Static build served by nginx in Docker (http://localhost:3000). The build embeds `VITE_API_BASE_URL` at build time.

- During debugging the project used a relaxed local-dev security posture (CSRF disabled) to remove friction. Re-enable CSRF and revert any dev-only security relaxations before production deployment.

- The dockerized frontend does not proxy API calls through nginx; it expects the browser to call the backend directly (CORS must allow the frontend origin) or the backend must be reachable on host network ports.

- If you change ports in compose or run frontend/dev on a different host/port, update backend CORS allowed origins and frontend `VITE_API_BASE_URL` accordingly.

## Quick command cheatsheet

Start everything (build + up):

```bash
docker compose build
docker compose up -d
```

Rebuild backend only:

```bash
docker compose build backend
docker compose up -d backend
```

Run frontend locally (dev):

```bash
cd frontend
npm install
npm run dev
```

Run backend locally:

```bash
cd backend
./mvnw spring-boot:run
```
Inspect backend container /data/h2:

```bash
docker exec -it $(docker ps --filter name=urlshortener-backend -q) sh -c "ls -la /data/h2 || true"
```

---
## Future Enhancements & Technical Trade-offs

This project was developed as part of an interview challenge, and as such, I’ve made conscious trade-offs to prioritize 
delivering a complete, functional product within a limited timeframe. While the core functionality is solid, 
there are several areas where I would invest further time and care in a production setting.

### Tech Debt
- **Security**: CSRF protection is currently disabled for simplicity in local development. In production, I would re-enable CSRF and ensure proper token handling across frontend and backend.
- **Code Quality**: While I’ve strived for clean, maintainable code, some areas show remnants of active development in aspects which required greater efforts of troubleshooting and investigation. This would be cleaned up and refactored in a production codebase.
- **Alias Generation**: The current random alias generation is basic but functional. A more robust algorithm would be needed to minimize collisions and ensure URL uniqueness at scale.
- **Dockerized Ports**: NGINX in the dockerized frontend serves static files but does not proxy API requests. In production, I would configure NGINX to proxy API calls to the backend to simplify CORS and deployment.
- **Testing**: While backend unit tests are comprehensive, the frontend has no automated test suite. A production system would require thorough end-to-end tests and component tests for the frontend using eg Vitest or Cypress.
- **Code Modularity**: Some components and services could be further modularized for better separation of concerns and reusability.

### Future Enhancements
- **Analytics**: Track click counts and usage stats per short URL.
- **Expiration**: Allow optional expiry dates for short URLs.
- **Authentication**: Add user accounts and private link management.
- **Search & filtering**: Improve the frontend table with search, pagination, and sorting.
- **QR code generation**: Add a button to generate a QR code for each short URL.
- **Rate limiting**: Prevent abuse of the shortening endpoint.
- **Admin dashboard**: View usage metrics and manage links.
- **Database**: Migrate to a persistent database with PostgreSQL and schema migrations using Liquibase.
- **Observability**: Add structured logs, health checks, and metrics endpoints.
- **Styling**: Refine spacing, responsiveness, and theming of frontend.
- **CI/CD**: Set up automated builds, tests, and deployments using GitHub Actions or similar.

This README and the codebase aim to balance clarity, functionality, and pragmatism. I’ve focused on delivering a maintainable, testable foundation that could be extended into a production-ready service with further investment.

