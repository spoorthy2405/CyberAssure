# 4. Technology Stack and Core Engines

The CyberAssure system is designed as a modern, full-stack cybersecurity insurance management application utilizing a decoupled client-server architecture. It integrates several specialized engines and libraries to handle distinct functional requirements efficiently.

## 4.1. Server-Side Technologies (Backend)

| Layer / Category | Technology | Purpose and Details |
| :--- | :--- | :--- |
| **Framework** | Spring Boot 4.0.3 | Robust, enterprise-grade Java framework providing auto-configuration and a rich ecosystem for building RESTful microservices. |
| **Persistence (ORM)** | Spring Data JPA (Hibernate) | Simplifies data access and object-relational mapping, ensuring efficient database interactions and transaction management. |
| **Security (Auth)** | Spring Security & JWT | Implements stateless, secure API communication using JSON Web Tokens (JJWT) with role-based access control (RBAC). |
| **Database** | H2 Database | High-performance, in-memory relational database for rapid development and testing with SQL compatibility. |
| **Risk Engine** | Custom Risk Assessment Logic | A specialized rule-based engine that calculates risk scores and levels (LOW/MEDIUM/HIGH) based on user security posture. |
| **API Documentation** | Swagger (Springdoc OpenAPI 2.5) | Interactive endpoint visualization, testing, and contract definitions for seamless frontend-backend integration. |
| **Testing** | JUnit 5 & Mockito | Comprehensive unit and integration testing suite ensuring business logic reliability and API contract adherence. |

## 4.2. Client-Side Technologies (Frontend)

| Layer / Category | Technology | Purpose and Details |
| :--- | :--- | :--- |
| **Framework** | Angular 21 (TypeScript) | Modern, reactive Single Page Application (SPA) utilizing Standalone Components and the latest Signal-based state management. |
| **State Management** | Angular Signals & RxJS | Fine-grained reactivity via Signals for UI state, combined with RxJS for complex asynchronous data stream handling. |
| **Styling** | Tailwind CSS 3.4 | Utility-first CSS framework enabling highly customized, modern UI designs with rapid development cycles. |
| **Data Visualization** | Chart.js 4.5 | Dynamic, interactive data visualization for insurance analytics and dashboard statistics. |
| **Iconography** | Emoji & SVG Assets | Lightweight, modern visual language used throughout the interface for intuitive user interaction. |
| **Form Handling** | Angular Reactive Forms | Comprehensive client-side form validation and complex data binding for policy applications and incident reports. |
| **Testing** | Vitest 4.0 | Next-generation testing framework providing rapid execution and high-fidelity unit testing for Angular components. |
