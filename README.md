# Keycloak Spring Boot Integration

This project demonstrates how to integrate Keycloak as an authentication provider in a Spring Boot application. It configures OAuth2 security to protect specific endpoints and manage user sessions effectively.


## Requirements

- Java 8 or higher
- Spring Boot 3.x.x or higher
- Keycloak server
- maven

## Installation

1. Clone this repository:
   ```bash
   git clone https://github.com/Awambeng/keycloak-springboot-integration.git
   ```
   
2. Navigate to the project directory:
   ```bash
   cd keycloak-springboot-integration
   ```
   
3. Running Keycloak
To run Keycloak locally, you can use Docker. Execute the following command to start Keycloak in a container:
   ```bash
   docker run -p 8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:latest start-dev
   ```
    This command will start a Keycloak server accessible at http://localhost:8080 with the admin credentials set as admin/admin.


4. Create a .env file in the root directory. You can refer to [.env.example](.env.example)

## Starting the Application
1. Clean and install the project dependencies:
   ```bash
   mvn clean install
   ```
   
2. Start the application
   ```bash
    mvn spring-boot:run
   ```
    Access your application at [http:localhost:8081](http:localhost:8081)


## Testing the Application
   - Test the Public Endpoint: Access the public endpoint at http://localhost:8081/public/hello in your browser or with a tool like Postman. You should receive the response: "Hello, Public!".

   - Test the Secured Endpoint: To access the secured endpoint (http://localhost:8081/secure/hello), you need to authenticate using Keycloak. You should receive the response: "Hello, Secured User!".

## Conclusion
This project showcases how to effectively integrate Keycloak with a Spring Boot application, providing a secure solution for user authentication and authorization. Explore the implementation and customize it to fit your needs!