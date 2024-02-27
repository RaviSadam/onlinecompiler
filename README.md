# Online Compiler REST API Documentation

This document outlines the functionalities, endpoints, and usage of the Online Compiler REST API developed using Spring Boot and Java, integrated with Swagger for API documentation and Actuator for monitoring, with MySQL used for storing data. The API allows users to compile and execute code, manage submissions, and update user details.

## Authentication and Authorization

- **Create Account:** Users can create an account with the system.
- **Login:** Users can log in to their accounts and receive a JWT token which expires in 10 days.
- **Logout:** Users can log out of their accounts.

## Compiler Functionality

- **Available Status of Code:** Provides all available statuses of code.
- **AI Generated Algorithm:** Generates an algorithm for the given code.
- **Supported Languages:** Lists all supporting languages by the compiler.
- **Submission Details:** Gives details of submissions made by the logged-in user, supporting filters based on language and status, with pagination support.
- **Delete Submission:** Deletes the submission, with the requirement that the current logged-in user is the owner of the submission.
- **Download Source Code:** Allows downloading the source code file based on submission ID.
- **Submission Details:** Provides submission details such as language, submission count, success submissions, and submissions made in the past 24 hours.
- **User Submissions:** Lists all submissions made by users in the past hours, with support for filters based on username, languages, status, and pagination.

## User Management
- **Scheduled Deletion:** Schedules deletion of the current requested (logged-in) user's data at 2 AM.
- **Update User Details:** Allows updating user details such as first name, last name, gender, password, and favorite language. Username and email cannot be modified.

## Compiler Execution

- **Compilation and Execution:** Uses ProcessBuilder for compiling and executing code.
- **Optimization:** Code compilation and execution are optimized using ExecutorService and Locks in Java.
- **Input and Output Handling:** Users can provide inputs required for the program, which are available during execution, and the output is read from the ProcessBuilder and displayed to the user.
- **Expected Output Matching:** Supports matching expected output.

## Integration and Monitoring

- **Swagger Integration:** The API is integrated with Swagger for easy documentation and testing of endpoints.
- **Actuator Monitoring:** Actuator endpoints are exposed for monitoring and managing the application.

## Database Integration

- **MySQL Database:** Data is stored in a MySQL database.
- **Data Deletion:** When a user deletes their submissions, related information from the database is also deleted.
- To optimize query performance and reduce the number of database trips in a MySQL database for the Online Compiler REST API, stored procedures can be utilized
