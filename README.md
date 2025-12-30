# Warehouse Retail System – Backend

The Warehouse Retail System is a backend service for a warehouse distribution company that manages multiple retail stores. It provides internal admin users with tools to manage stores, products, inventory, customer orders, and product reviews. The system uses a relational database (MySQL) for core transactional data and a NoSQL database (MongoDB) for unstructured customer reviews.

This project was built as a full backend implementation using Java, Spring Boot, and Hibernate, with an emphasis on clean architecture, clear domain modeling, and production-style practices.

---

## 1. Core features

- **Store management:**
  - Create, update, list, and deactivate retail stores.
  - Store metadata such as location, status, and identifiers.

- **Product catalog management:**
  - Manage the entire product catalog with categories, pricing, and activation status.
  - Designed to support large catalogs across multiple stores.

- **Inventory management:**
  - Track inventory per store and product.
  - View stock levels by store.
  - Update stock levels and enforce constraints (e.g., cannot reduce below zero).

- **Order placement:**
  - Place customer orders through internal tools (phone or in-store).
  - Calculate totals, validate inventory, and update stock atomically.
  - Associate orders with stores, customers, and line items.

- **Customer management:**
  - Basic customer profiles for order tracking and analytics.

- **Review system (NoSQL / MongoDB):**
  - Store product reviews as unstructured/semistructured data in MongoDB.
  - Fetch reviews per product.

- **Reporting via stored procedures (MySQL):**
  - Monthly sales per store.
  - Aggregate company sales by month and year.
  - Top-selling products by category.

- **Error handling:**
  - Global exception handling with standardized error responses for the frontend.

---

## 2. Architecture overview

At a high level, the system is a layered Spring Boot application:

- **Controller layer:** Exposes REST APIs to the frontend, handles HTTP requests and responses.
- **Service layer:** Contains business logic, validations, and orchestration across repositories.
- **Repository layer:** Communicates with databases using Spring Data JPA (MySQL) and Spring Data MongoDB.
- **Data layer:** Contains JPA entities for structured SQL data and MongoDB documents for unstructured review data.

The design follows common backend best practices:
- Clear separation of concerns between layers.
- Domain-focused models (Store, Product, Inventory, Order, Customer, Review).
- Transactional integrity around order placement and inventory updates.

---

## 3. Tech stack

- **Language:** Java (Spring Boot)
- **Frameworks:** Spring Boot, Spring Web, Spring Data JPA, Spring Data MongoDB, Hibernate
- **Databases:**
  - MySQL (relational, transactional data)
  - MongoDB (NoSQL, reviews)
- **Build tool:** Maven or Gradle (project uses one; see `pom.xml` or `build.gradle`)
- **Documentation:** README, inline JavaDoc and comments where logic is non-trivial
- **Testing:** JUnit and Spring Boot test support (for services and controllers)

---

## 4. Domain model (business entities)

### 4.1 SQL entities (MySQL via JPA/Hibernate)

- **Store:**
  - Represents a physical or virtual retail location.
  - Contains information such as name, code, address, and active status.

- **Product:**
  - Represents items in the catalog.
  - Includes SKU, name, category, description, price, and active status.

- **Inventory:**
  - Connects a `Store` and a `Product` with a quantity.
  - Used to track how much of a product is available in each store.

- **Customer:**
  - Represents a customer placing orders through the internal system.
  - Includes basic contact information.

- **OrderDetails (Order header):**
  - The main order record containing store, customer, date, status, and total amount.
  - Connected to multiple `OrderItem` entries.

- **OrderItem:**
  - Represents line items for each `OrderDetails`.
  - Includes product, quantity, unit price, and line total.

### 4.2 NoSQL documents (MongoDB)

- **Review:**
  - Stores customer reviews for products.
  - Contains product reference, rating, comment, and timestamps.
  - Uses MongoDB for flexibility in review structure and evolution over time.

---

## 5. Project structure

A typical project structure (simplified) looks like this:

```text
src/main/java/com/example/warehouseretailsystem
├── controller
│   ├── StoreController.java
│   ├── ProductController.java
│   ├── InventoryController.java
│   ├── OrderController.java
│   ├── CustomerController.java
│   └── ReviewController.java
├── service
│   ├── StoreService.java
│   ├── ProductService.java
│   ├── InventoryService.java
│   ├── OrderService.java
│   ├── CustomerService.java
│   └── ReviewService.java
├── repository
│   ├── StoreRepository.java
│   ├── ProductRepository.java
│   ├── InventoryRepository.java
│   ├── OrderDetailsRepository.java
│   ├── OrderItemRepository.java
│   ├── CustomerRepository.java
│   └── ReviewRepository.java
├── model
│   ├── sql
│   │   ├── Store.java
│   │   ├── Product.java
│   │   ├── Inventory.java
│   │   ├── Customer.java
│   │   ├── OrderDetails.java
│   │   └── OrderItem.java
│   └── nosql
│       └── Review.java
├── dto
│   ├── OrderRequestDto.java
│   ├── OrderItemRequest.java
│   └── other DTOs as needed
├── exception
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── InsufficientStockException.java
│   └── other custom exceptions
└── config
    ├── DatabaseConfig.java (if needed)
    └── MongoConfig.java (if needed)
