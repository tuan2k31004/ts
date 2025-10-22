#!/bin/bash

echo "Starting hTask Development Environment..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker and try again."
    exit 1
fi

# Build and start all services
echo "Building and starting all services with Docker Compose..."
docker-compose up --build

echo "hTask is now running!"
echo "Access the application at: http://localhost:3000"
echo "API Gateway: http://localhost:8080"
