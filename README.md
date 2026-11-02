## 📌 Project Description

<img width="1199" height="1312" alt="Credit Bureau Virtualization Architecture" src="https://github.com/user-attachments/assets/49c8369c-5a20-4288-ac6e-e0f0e2e83528" />


### What is Credit Bureau Virtualization?

Credit Bureau Virtualization is a banking-oriented service virtualization project developed using **Java, Spring Boot, and WireMock**.

In real-world banking applications, services such as loan processing often depend on external systems like Credit Bureaus to retrieve customer credit scores.

However, during application development and testing, depending on an actual third-party service can create challenges:
- External services may be unavailable.
- API responses may be delayed.
- Testing server failures can be difficult.
- Access to real third-party environments may be restricted.
- Repeated integration testing may depend on external system availability.

To solve these challenges, this project implements a **Virtual Credit Bureau** using WireMock that behaves like an external Credit Bureau REST API.

The main Spring Boot application communicates with this virtual service just as it would communicate with a real external service.

### 🎯 Project Objective

The primary objective is to demonstrate how service virtualization can help banking applications independently develop, integrate, and test external service dependencies.

The project demonstrates:
- Simulating external REST APIs without connecting to actual third-party systems.
- Creating predefined success and failure responses.
- Simulating HTTP 404 and HTTP 500 errors.
- Simulating network delays and request timeouts.
- Handling external service failures gracefully.
- Testing application behaviour under different dependency conditions.

### 🏗️ Architecture Explanation

The application follows a layered architecture with clearly separated responsibilities.

**1. Controller Layer**

The controller exposes REST endpoints and receives customer requests. It accepts the customer ID and delegates the request to the service layer.

**2. Service Layer**

This layer contains the core business logic. It obtains the customer's credit score through the integration layer and evaluates loan eligibility based on a demo credit score threshold of 750.

**3. Client / Integration Layer**

The `CreditBureauClient` communicates with the external Credit Bureau using Spring `RestClient`.

It is responsible for:
- Sending HTTP requests.
- Configuring connection and read timeouts.
- Deserializing JSON responses.
- Handling HTTP errors and connectivity failures.

**4. Model Layer**

This layer contains the data models used to transfer information between components.

- `CreditScoreResponse`: Represents the response received from the virtual Credit Bureau.
- `LoanEligibilityResponse`: Represents the final loan eligibility result returned to the client.

**5. Virtual Credit Bureau Layer**

WireMock acts as the external Credit Bureau and simulates different API behaviours.

It provides predefined responses for successful requests, unknown customers, server errors, and delayed responses.

### 🔄 End-to-End Request Flow

1. A client sends a loan eligibility request to the Spring Boot application.
2. The Controller receives the customer ID.
3. The Service Layer invokes the Credit Bureau Client.
4. The Client sends an HTTP request to the WireMock server.
5. WireMock matches the request against configured stubs and returns a simulated response.
6. The Client processes the response and handles any external service errors.
7. The Service Layer evaluates the credit score.
8. The Controller returns the final loan eligibility response to the client.

### ⚙️ Service Virtualization Scenarios

| Scenario | Virtual Credit Bureau Behaviour | Application Behaviour |
|---|---|---|
| Successful response | Returns credit score 780 | Customer eligible |
| Low credit score | Returns credit score 650 | Customer not eligible |
| Unknown customer | Returns HTTP 404 | Returns HTTP 404 |
| Server failure | Returns HTTP 500 | Returns HTTP 502 |
| Timeout | Delays response by 5 seconds | Returns HTTP 503 after client timeout |

### 💡 Why WireMock?

WireMock allows developers to simulate external HTTP services without requiring access to the actual service.

Using WireMock, we can control:
- HTTP status codes.
- Response headers.
- JSON response bodies.
- Response delays.
- Different API behaviours.

This makes it possible to test both successful and failure scenarios in a controlled environment.

### 🚀 Key Takeaway

This project demonstrates how service virtualization can reduce dependency on external systems, improve development efficiency, and enable reliable testing of banking application integrations.

Instead of waiting for an actual Credit Bureau environment, developers can use a virtual service to simulate realistic responses and validate application behaviour independently.

**Note:** This is an educational project. The credit score threshold and customer data are illustrative and do not represent actual banking policies or credit bureau records.
