# Registration System

## Description

This is a console-based student registration system, developed in Java. The application allows for the management of student data, including the creation, reading, updating, and deletion of records (CRUD). The system has two types of users: **Administrator** and **Student**, each with different access levels and functionalities.

### Features

* **Administrator:**
    * Insert, update, list, and delete students.
    * Create and list classes.
    * Enroll students in classes.

* **Student:**
    * View their own registration data.
    * List the classes in which they are enrolled.

## Technologies Used

* **Language:** Java (JDK 23)
* **Database:** PostgreSQL
* **Build:** Maven
* **Dependencies:**
    * `org.postgresql:postgresql:42.7.3`

## Adopted Practices

* Object-Oriented Programming (OOP)
* DAO (Data Access Object)
* Connection Factory
* Exception Handling
* Package Structure

## How to Run

1.  **Prerequisites:**
    * JDK 23 or higher.
    * Maven.
    * A running PostgreSQL database server.

2.  **Database Configuration:**
    * Create a database in PostgreSQL.
    * Update the connection credentials (`URL`, `USER`, `PASSWORD`) in the `src/main/java/util/ConnectionFactory.java` file.

3.  **Execution:**
    * Clone the repository.
    * Navigate to the project's root directory.
    * Run the application from the `Main.java` class.

Upon startup, the application will offer the option to create the `alunos` and `usuarios` tables and insert a default administrator user (`login: admin1`, `password: 1234`).
