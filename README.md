# LoanTech Microservices Project

## Project Overview
LoanTech is a distributed microservices-based system designed to manage loans, users, and transactions efficiently. It leverages service discovery, centralized routing, and individual service modules for scalability and maintainability.

## Setup Instructions

### Prerequisites
- **Java 17** or higher
- **Maven** 3.8 or higher
- **Docker** (optional, for containerized deployment)
- **PostgreSQL** or other supported database
- An IDE like IntelliJ IDEA or VS Code for development

### Steps to Run Locally

1. **Clone the Repository**
   ```bash
   git clone <repository_url>
   cd loantech
   ```

2. **Build the Project**
   Navigate to the root directory and build the Maven project:
   ```bash
   mvn clean install
   ```

3. **Start the Eureka Server**
   Navigate to the `eureka-server` directory and run:
   ```bash
   mvn spring-boot:run
   ```

4. **Start Individual Services**
   Navigate to each service directory (`gateway-service`, `loan-service`, `user-service`, `transaction-service`) and run:
   ```bash
   mvn spring-boot:run
   ```

5. **Access the Gateway**
   The API Gateway will be available at:
   ```
   http://localhost:8083
   ```

6. **Swagger Documentation**
   Each service exposes its own Swagger documentation at:
   - Loan Service: `http://localhost:<loan-service-port>/swagger-ui.html`
   - User Service: `http://localhost:<user-service-port>/swagger-ui.html`
   - Transaction Service: `http://localhost:<transaction-service-port>/swagger-ui.html`

7. **Service Discovery**
   Eureka dashboard is accessible at:
   ```
   http://localhost:<eureka-server-port>
   ```

### Using Docker (Optional)
To run the system using Docker, ensure Docker is installed and then build and run the Docker containers as follows:
1. Build images for each service.
2. Use a `docker-compose.yml` file to start all services together.

## Design Decisions and Trade-offs

### Microservices Architecture
- **Decision**: Split functionalities into individual services (User, Loan, Transaction) with an API Gateway for centralized routing.
- **Trade-offs**:
  - **Pros**: Scalability, modularity, and fault isolation.
  - **Cons**: Increased complexity in deployment and inter-service communication.

### Service Discovery with Eureka
- **Decision**: Used Eureka for dynamic discovery of services.
- **Trade-offs**:
  - **Pros**: Simplifies service registration and routing.
  - **Cons**: Adds a single point of failure (Eureka Server).

### Gateway for Routing
- **Decision**: Centralized API Gateway (`gateway-service`) to handle all client requests.
- **Trade-offs**:
  - **Pros**: Simplifies client communication, enables cross-cutting concerns like authentication.
  - **Cons**: Adds potential latency.

### Swagger Integration
- **Decision**: Included Swagger for API documentation in each service.
- **Trade-offs**:
  - **Pros**: Enhances developer experience.
  - **Cons**: Increases configuration overhead.

### Database Choice
- **Decision**: Designed to use PostgreSQL as the database backend.
- **Trade-offs**:
  - **Pros**: Reliable and supports complex queries.
  - **Cons**: Requires additional setup for clustering.

### Security
- **Decision**: OAuth2 and JWT for securing APIs.
- **Trade-offs**:
  - **Pros**: Modern and scalable authentication mechanism.
  - **Cons**: Adds configuration complexity.

## Contribution Guidelines
1. Fork the repository.
2. Create a feature branch.
3. Commit changes and push to the fork.
4. Create a pull request with detailed explanations.



## Support
For questions or issues, open an issue on the repository or contact the maintainers.

