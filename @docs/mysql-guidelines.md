# MySQL Database Design Guidelines

This document provides guidelines for MySQL database design based on industry best practices.

## 1 Database Security

### 1.1 Password Storage
- Use irreversible encryption algorithms (SHA-256, bcrypt) for password storage
- Always combine with random salt values
- Establish unified encryption middleware

### 1.2 Sensitive Data
- Encrypt or desensitize sensitive user information (phone numbers, ID cards)
- Establish unified sensitive information query services
- All operations involving sensitive data must record detailed audit logs

### 1.3 Access Control
- Use IP whitelisting to restrict access sources
- Different business systems must use independent database accounts
- Follow least privilege principles for database permissions

## 2 Basic Design Specifications

### 2.1 Database Naming Conventions
- Use lowercase with underscores (snake_case) for database names
- Database names should be descriptive and related to the business module
- Maximum 64 characters

```sql
-- Good
CREATE DATABASE order_management;
CREATE DATABASE user_auth_system;

-- Bad
CREATE DATABASE OrderManagement;
CREATE DATABASE db1;
```

### 2.2 Table Naming Conventions
- Use lowercase with underscores (snake_case)
- Use singular form for table names
- Prefix with business module when needed

```sql
-- Good
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL
);

CREATE TABLE order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL
);

-- Bad
CREATE TABLE Users;
CREATE TABLE orderItems;
```

## 3 Table Design

### 3.1 Storage Engine
- Use InnoDB for transaction support and foreign key constraints
- Set default charset to utf8mb4 for full Unicode support

```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 3.2 Primary Keys
- Always define a primary key for each table
- Use auto-increment BIGINT for simplicity
- Use UUID for distributed systems or when exposing IDs publicly

```sql
-- Auto-increment (recommended for most cases)
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ...
);

-- UUID (for distributed systems)
CREATE TABLE user (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    ...
);
```

### 3.3 Foreign Keys
- Use foreign keys to enforce referential integrity
- Create indexes on foreign key columns
- Use appropriate ON DELETE/UPDATE actions

```sql
CREATE TABLE order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
) ENGINE=InnoDB;
```

### 3.4 Timestamps
- Use DATETIME or TIMESTAMP for timestamp fields
- Include `created_at` and `updated_at` fields in tables
- Use CURRENT_TIMESTAMP as default for created_at

```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;
```

## 4 Field Design

### 4.1 Data Type Selection

#### 4.1.1 Numbers
- Use TINYINT for flags (0-255)
- Use INT for standard integers
- Use BIGINT for large numbers
- Use DECIMAL for currency (never use FLOAT/DOUBLE)

```sql
-- Flags
is_active TINYINT(1) DEFAULT 0

-- Currency
amount DECIMAL(10, 2) NOT NULL

-- Standard IDs
user_id BIGINT UNSIGNED NOT NULL
```

#### 4.1.2 Strings
- Use VARCHAR for variable-length strings
- Set appropriate length limits
- Use CHAR for fixed-length strings (e.g., country codes)

```sql
username VARCHAR(50) NOT NULL
phone_number VARCHAR(20)
country_code CHAR(2) DEFAULT 'US'
```

#### 4.1.3 Dates and Times
- Use DATE for dates without time
- Use DATETIME for dates with time
- Use TIMESTAMP for timestamps (auto-updating)

```sql
birth_date DATE
created_at DATETIME
last_login TIMESTAMP
```

### 4.2 Field Constraints
- Use NOT NULL for required fields
- Use DEFAULT for optional fields with sensible defaults
- Use UNIQUE for fields requiring uniqueness
- Use CHECK constraints for business rules (MySQL 8.0+)

```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    age INT,
    CONSTRAINT chk_age CHECK (age >= 0 AND age <= 150)
) ENGINE=InnoDB;
```

### 4.3 Avoid Magic Numbers
- Use ENUM for columns with limited string options
- Document any numeric codes in comments

```sql
status ENUM('pending', 'processing', 'completed', 'cancelled') DEFAULT 'pending'
```

## 5 Normalization

### 5.1 First Normal Form (1NF)
- Each column contains atomic (indivisible) values
- No repeating groups or arrays within a single field

```sql
-- Bad: repeating group
CREATE TABLE order_bad (
    id INT PRIMARY KEY,
    product1 VARCHAR(100),
    product2 VARCHAR(100),
    product3 VARCHAR(100)
);

-- Good: atomic values
CREATE TABLE order_item (
    id INT PRIMARY KEY,
    order_id INT,
    product_name VARCHAR(100)
);
```

### 5.2 Second Normal Form (2NF)
- All non-key columns depend on the entire primary key
- No partial key dependencies

### 5.3 Third Normal Form (3NF)
- No transitive dependencies
- Non-key columns should depend only on the primary key

### 5.4 When to Denormalize
- Read-heavy applications (analytics dashboards)
- Frequently accessed data that requires complex joins
- Performance-critical queries after profiling

## 6 Indexing

### 6.1 When to Add Indexes
- Always index: Primary keys, Foreign keys
- Index columns in WHERE clauses
- Index columns in ORDER BY
- Index columns in JOIN conditions

### 6.2 Index Best Practices
- Use composite indexes for multi-column queries
- Place high-selectivity columns first in composite indexes
- Avoid over-indexing (each index slows down writes)

```sql
-- Single column index
CREATE INDEX idx_user_email ON user(email);

-- Composite index
CREATE INDEX idx_order_user_status ON order(user_id, status);

-- Covering index (includes all columns needed for query)
CREATE INDEX idx_order_user_covering ON order(user_id, status, created_at);
```

### 6.3 Index Naming
- Use prefix `idx_` for regular indexes
- Use prefix `uk_` for unique indexes
- Use prefix `fk_` for foreign key indexes (or let MySQL create automatically)

```sql
idx_tablename_columnname
uk_tablename_columnname
```

## 7 Query Optimization

### 7.1 General Rules
- Avoid SELECT *; specify only needed columns
- Use EXPLAIN to analyze queries
- Avoid functions on indexed columns in WHERE clauses
- Use LIMIT for pagination

### 7.2 Pagination
```sql
-- Bad for large offsets
SELECT * FROM orders ORDER BY id LIMIT 1000000, 10;

-- Good: use keyset pagination
SELECT * FROM orders WHERE id > 1000000 ORDER BY id LIMIT 10;
```

### 7.3 JOIN Best Practices
- Ensure foreign keys have indexes
- Prefer INNER JOIN over LEFT JOIN when possible
- Avoid joining on expressions or functions

## 8 Backup and Recovery

### 8.1 Backup Strategy
- Follow 3-2-1 backup rule: 3 copies, 2 media types, 1 off-site
- Schedule regular automated backups
- Test restore procedures regularly

### 8.2 Point-in-Time Recovery
- Enable binary logging
- Store binary logs in separate location from data

## 9 Schema Documentation

### 9.1 Required Documentation
- Table purpose and business context
- Column meanings and valid values
- Index purposes and usage patterns
- Relationships and dependencies

### 9.2 Tools
- Use ER diagrams for visual representation
- Tools: MySQL Workbench, dbdiagram.io, DBeaver
