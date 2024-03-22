# Ecommerce Microservices APIs

## Prerequisites
- Docker installed: [Docker Installation Guide](https://docs.docker.com/get-docker/)
- Docker Compose installed: [Docker Compose Installation Guide](https://docs.docker.com/compose/install/)
#### *Optional
- Python 3.11+ is recommended but not mandatory.

## How to Run the Application

1. Start by creating a Docker network:
    ```bash
   docker network create ecommerce
    ```

2. Run the application using the provided Python script (designed for Linux-based systems):
    ```bash
    python deploy.py
    ```
   To shut down all containers:
   ```bash
   python down.py
    ```
   
3. If you prefer not to use the script, navigate to each directory containing the docker-compose.yml file and execute:
    ```bash
   docker compose up -d
    ```
   This will initialize the microservices needed for the Ecommerce application.

## Swagger

To access the swagger docs access the endpoints:

1. User API
   ```bash
   http://localhost:8080/api/v1/swagger-ui/index.html
   ```

2. Product API
   ```bash
   http://localhost:8081/api/v1/swagger-ui/index.html
   ```

3. Shopping API
   ```bash
   http://localhost:8082/api/v1/swagger-ui/index.html
   ```