# High-Level Architecture

## Overview

The Enterprise Asset Management System follows a modern three-tier architecture.

```
+------------------------------------------------------+
|                  Vue.js + PrimeVue                   |
|                (Presentation Layer)                  |
+------------------------------------------------------+
                    |
                    | REST APIs
                    |
+------------------------------------------------------+
|               Spring Boot Application                |
|               (Business Logic Layer)                 |
+------------------------------------------------------+
                    |
                    |
+------------------------------------------------------+
|                  PostgreSQL Database                 |
|                 (Data Persistence Layer)             |
+------------------------------------------------------+
```

Future Integrations

- OpenAI API
- Email Service
- File Storage
- Notification Service
- Audit Logging
