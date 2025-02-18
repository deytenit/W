# W: The Minimalistic Forum Application

[![Docker Image Available](https://img.shields.io/badge/Docker-Image-blue?style=flat-square&logo=docker)](https://ghcr.io/deytenit/w-server)
[![Archived](https://img.shields.io/badge/Status-Archived-red?style=flat-square)](https://github.com/deytenit/w)
[![License: MIT](https://img.shields.io/badge/License-MIT-green?style=flat-square)](LICENSE)

> [!Note]
> This repository has been archived, since all related to it courses has ended.

Technically, **W** is a Java Spring.Boot application that provides REST API to manage users, posts, discussions, and votes in a Maria Database.
The application exposes all its domain entities via dedicated controllers, trying to be structurally clean as much as possible.

## History

- **Initially** developed as an assignment during the Web Development course at ITMO University.
- **Later** highly modified to serve as the backend for an iOS application created during the iOS Development course at VK Education.
- **At the end** this repository was created to preserve the work done during those courses, and represents a snapshot of the project at the time of latest course completion.

## Features

- **Modular Architecture:** Clear separation of concerns with dedicated controllers for each entity.
- **Database Integration:** Uses JPA/Hibernate for ORM with automatic schema updates.
- **Configuration:** Custom properties for JWT secrets, SHA salts, and media storage directories.
- **Docker Ready:** Easily deployable with Docker Compose, including MariaDB and phpMyAdmin support.

## Domain of Operation

The application operates around four core entities:

1. **User**
   - Represents forum users.
   - Stores basic information such as name, login, and is admin privileges available or not.
   - Maintains a collection of posts/discussions/votes authored by the user.

2. **Post**
   - Acts as the primary discussion thread with a title and text body.
   - Supports media attachments.
   - Keeps track of associated discussions, votes, view counts and creation timestamp.
   - Linked to the user who created it.

3. **Discussion**
   - Serves as a reply to posts and other discussions.
   - Supports nested discussions through an optional parent discussion link.
   - Contains the discussion text, associated votes, and timestamps.
   - Linked both to the user posting the comment and the corresponding post.

4. **Vote**
   - Records upvotes or downvotes for posts and discussions.
   - Associates each vote with a user and either a post or a discussion.

## API Documentation

The API is fully documented using the OpenAPI specification. You can explore the endpoints and their usage details via the [Swagger Generated Documentation](./openapi-spec.json).

## Configuration

The application configuration is handled in the `application.properties` file (see [application.properties](./src/main/resources/application.properties)). Key configuration parameters include:

- **Database Connection:**
  - Uses environment variables (`MYSQL_URL`, `MYSQL_USER`, `MYSQL_PASSWORD`) to configure the datasource.

- **Server Settings:**
  - `server.port=8090`
    Sets the default port for the application.
  - `server.servlet.contextPath`
    Allows customization of the API base path (for the reverse proxy connoisseurs).

- **Custom Application Properties:**
  - `config.shaSalt`
    A salt used for SHA hashing of a users' passwords, provided via an environment variable.
  - `config.jwtSecret`
    The secret key for JWT authentication.
  - `config.mediaDir`
    Directory path for storing media files.

> [!Important]
> To adjust these settings, supply the required environment variables.
> Do not explicitly change the values in file unless you know the risks.

## Docker Deployment

The application is containerized and can be deployed using the provided `docker-compose-dev.yml` file as template. The setup includes:

### Running with Docker Compose

1. **Prepare your environment:**
   Create an `.env` file with the necessary environment variables (database credentials, JWT secrets, etc.).

2. **Build and start the containers:**

   ```bash
   docker compose -f docker-compose-dev.yml up --build
   ```

3. **Access the services:**
   - **API:** `http://localhost`
   - **phpMyAdmin:** `http://localhost:8080`

## License

This project is licensed under the [MIT License](LICENSE).

---

_The project's name **W** is as a humorous nod to the name of the "X" social network._
