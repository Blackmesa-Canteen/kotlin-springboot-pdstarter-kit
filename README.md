# ![RealWorld Example App](logo.png)

> ### Kotlin + Spring Boot + JPA + SLF4J + PostgreSQL + Gradle  codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.


### [Demo](https://demo.realworld.io/)&nbsp;&nbsp;&nbsp;&nbsp;[RealWorld](https://github.com/gothinkster/realworld)


This codebase was created to demonstrate a fully fledged backend application built with **Kotlin + Spring Boot + JPA + SLF4J + PostgreSQL + Gradle** including CRUD operations, authentication, routing, pagination, and more.

We've gone to great lengths to adhere to the **Kotlin + Spring Boot + JPA + SLF4J + PostgreSQL + Gradle** community styleguides & best practices.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

The business logic is modified based on [realworld-springboot-kotlin](https://github.com/raeperd/realworld-springboot-kotlin/tree/master). The Author is [sabercom](https://github.com/sabercon), I replaced the ORM framework into Spring Data JPA.


# How it works
- [Spring Boot](https://spring.io/projects/spring-boot) for Web API implementations
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa) for the persistence layer
- [Flyway](https://flywaydb.org/) for database migrations
- [PostgreSQL](https://www.postgresql.org/) for the actual database
- [JWT](https://jwt.io/) for authentication
- [Kotest](https://kotest.io/) for tests
- [Testcontainers](https://www.testcontainers.org/) for integration tests
- [Detekt](https://detekt.dev/) for static code analysis
- [Kover](https://github.com/Kotlin/kotlinx-kover) for test coverage


# Getting started

- Make sure you have Java 21, and the Docker engine is running on your machine
- Run ./gradlew bootRun to start the application and the database
- You can now access the application at http://localhost:8080/api
- For testing:
```shell
APIURL=http://localhost:8080/api ./api/run-api-tests.sh
```

