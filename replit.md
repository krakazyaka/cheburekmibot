# Replit Agent Instructions

## Overview

This is a Telegram Web App for "Чебуречная Вкус Востока" (Cheburek House "Taste of the East"), a food ordering application. The project consists of:
- **Backend**: Spring Boot REST API (Java 19, port 8080) with JWT authentication
- **Frontend**: React + Vite + TypeScript + TanStack Query (port 5000)
- **Database**: PostgreSQL with Liquibase migrations and test data
- **Tests**: Integration tests with H2 in-memory database
- **Integration**: Full-stack JWT-based authentication with CORS support

## User Preferences

Preferred communication style: Simple, everyday language.

## System Architecture

### Frontend Architecture
**Technology Stack**: Pure HTML, CSS, and JavaScript (no framework detected)

The application uses a vanilla JavaScript approach with:
- Single-page application architecture (index.html as main entry point)
- Telegram Web App SDK integration for native Telegram features
- Responsive design optimized for mobile devices (max-width: 400px container)

**Design Decisions**:
- **Rationale**: Lightweight, minimal dependencies ensure fast loading times within Telegram's WebApp environment
- **Pros**: No build process needed, easy deployment, minimal overhead
- **Cons**: May require more manual DOM manipulation as complexity grows

### Telegram WebApp Integration
**SDK Integration**: Official Telegram Web App JavaScript SDK

The application leverages:
- `telegram-web-app.js` loaded from Telegram's CDN
- Native Telegram UI/UX patterns through the WebApp API
- Access to Telegram user data and payment systems

**Design Decisions**:
- **Problem**: Need seamless integration with Telegram messenger
- **Solution**: Use official Telegram WebApp SDK for authentication, payments, and UI consistency
- **Pros**: Built-in user authentication, native payment processing, familiar UX for Telegram users
- **Cons**: Application limited to Telegram ecosystem only

### Styling Architecture
**Approach**: Inline CSS with custom styling

Current implementation includes:
- CSS reset for cross-browser consistency
- Linear gradient background for visual appeal
- Mobile-first responsive design
- Custom color scheme (primary: #e67e22 orange, secondary: #7f8c8d gray)

**Design Decisions**:
- **Rationale**: Inline styles reduce HTTP requests and simplify deployment
- **Alternative**: External stylesheet could improve maintainability for larger projects
- **Pros**: Single-file distribution, no additional asset management
- **Cons**: Harder to maintain as styles grow, no CSS preprocessing benefits

### Application Structure
**Component Organization**: Category-based layout

The UI is structured with:
- Header section with branding (logo and tagline)
- Category sections for menu organization
- Container-based layout for content management

## External Dependencies

### Third-Party Services
1. **Telegram Web App SDK**
   - Source: `https://telegram.org/js/telegram-web-app.js`
   - Purpose: Telegram platform integration, user authentication, payment processing
   - Integration: Client-side JavaScript SDK

## Backend Architecture

### Technology Stack
- **Framework**: Spring Boot 3.4.11
- **Language**: Java 19
- **Build Tool**: Gradle 8.14.3
- **Database**: PostgreSQL
- **ORM**: Hibernate/JPA
- **Migrations**: Liquibase (SQL-based)
- **Mapping**: MapStruct for DTO conversions
- **Testing**: JUnit 5, Spring Boot Test, H2 Database

### Architecture Pattern
**Strict Repository-Service-Controller (RSC) Pattern**

The backend follows a strict three-layer architecture:

1. **Repository Layer**: JPA repositories for database access
   - UserRepository, OrderRepository, MenuItemRepository, AddonRepository, LoyaltyCardRepository, LoyaltyCodeRepository
   
2. **Service Layer**: Business logic and transaction management
   - OrderService: Order creation, retrieval, cheburek counting, server-side validation
   - AdminService: Order status management, loyalty points awarding
   - AddonService: Addon CRUD operations
   
3. **Controller Layer**: REST API endpoints
   - OrderController: User-facing order endpoints
   - AdminController: Admin endpoints for order and user management
   - AddonController: Addon management endpoints

### Recent Changes (November 24, 2025)

**Full-Stack Integration Completed**:
- React frontend integrated with Spring Boot backend via JWT authentication
- Vite development server configured with proxy to backend (port 8080)
- JWT tokens stored in localStorage with automatic Authorization header injection
- CORS enabled for localhost:5000 and *.replit.dev domains
- Method-level security annotations on OrderController (removed class-level to allow admin access)

**New Backend Endpoints**:
- POST /api/users/find-or-create - Telegram user authentication, returns JWT token
- GET /api/loyalty/{userId} - Get user loyalty card information
- PATCH /api/orders/{orderId}/status - Admin endpoint for order status updates
- POST /api/admin/offline-purchase - Award loyalty points for offline purchases

**Frontend Configuration**:
- Vite config with API proxy (/api, /auth, /admin → localhost:8080)
- JWT interceptor in queryClient.ts (automatic token management)
- package.json with React, TanStack Query, Wouter, Tailwind dependencies
- TypeScript configuration with path aliases (@/, @shared/, @assets/)

**Test Data Created**:
- 10 menu items across all categories (CHEBUR, DRINKS, SNACKS, DESSERTS)
- 4 addons linked to cheburek items
- 11 menu_item_addons relationships

**Previous Integration Infrastructure**:

**Integration Tests Infrastructure**:
- Added H2 in-memory database for testing (no Docker required)
- BaseIntegrationTest with Spring Boot Test + MockMvc + ActiveProfiles("test")
- Test configuration in `src/test/resources/application-test.properties`
- Liquibase disabled for tests, Hibernate auto-DDL enabled

**AuthController Tests (4/4 passing)**:
- Valid Telegram initData authentication
- Invalid HMAC hash rejection
- Expired auth_date rejection (>300 seconds)
- New user creation on first login

**OrderController Tests (3/4 passing)**:
- Get my orders successfully
- Ownership isolation between users
- Authentication required for all endpoints
- Note: Create order test requires OrderItem ID schema fix

**Order Entity Enhancements**:
- Added @PrePersist onCreate() to auto-set createdAt and status=CREATED
- Ensures data integrity without manual intervention

**OrderDto and OrderService**:
- Added subtotal and tax fields to OrderDto
- OrderService.validateAndRecalculateOrder() now sets subtotal (calculated), tax (zero), total (subtotal + tax)
- Tax computation can be extended when business rules are defined

**JWT-Based Authentication System**:
- Implemented complete authentication system using JWT tokens
- Telegram initData validation with HMAC-SHA256 signature verification
- Auth date freshness check (5-minute window) to prevent replay attacks
- Role-based access control with USER and ADMIN roles
- Spring Security integration with custom JWT filter
- Endpoint protection with @PreAuthorize annotations
- Ownership validation: users can only access their own data

**Server-Side Order Validation**:
- MenuItem existence and availability check
- Quantity must be greater than zero
- Prices fetched from database catalog (client prices ignored)
- XL option validated (2x price if available)
- Addon existence, availability, and menu item binding validated
- Total recalculated server-side to prevent tampering

**Security Architecture**:
- **AuthController**: POST /auth/login endpoint for Telegram authentication
- **JwtService**: JWT token generation and validation using JJWT 0.12.5
- **TelegramAuthService**: Telegram initData validation with replay protection
- **JwtAuthenticationFilter**: Intercepts requests and validates JWT tokens
- **SecurityConfig**: Configures Spring Security with stateless session management

**Access Control**:
- Public endpoints: /auth/**, /api/menu/**, /api/addons/** (GET only)
- User endpoints: /api/orders/**, /api/user/** (requires ROLE_USER + ownership validation)
- Admin endpoints: /admin/** (requires ROLE_ADMIN)
- Addon mutations: POST/PUT/DELETE /api/addons/** (requires ROLE_ADMIN)

**AdminController Implementation**:
- Added admin functionality for order and loyalty management
- Implemented order status workflow: CREATED → IN_PROGRESS → READY → DONE
- Automatic loyalty points awarding when orders are completed
- Manual loyalty points management by user code

**Order Status Flow**:
- New orders start with status CREATED
- Admin can move orders through statuses sequentially
- Upon completion (DONE status), loyalty points are automatically awarded equal to the number of items in the order
- Robust null-safety and user validation before status changes

**Database Migrations**:
- Restructured from XML to SQL files (9 base tables + 1 status update migration)
- Each table has its own migration file in `db/changelog/changes/`
- Migration 010 updates existing order statuses to new enum values

### API Endpoints

**Admin Endpoints**:
- `GET /admin/orders` - Get all orders sorted by creation date (newest first)
- `POST /admin/orders/{orderId}/move` - Move order to next status in workflow
- `POST /admin/users/{userCode}/loyalty-points` - Add loyalty points to user by code

**Order Endpoints**:
- `POST /api/orders` - Create new order (with server-side validation, requires ROLE_USER)
- `GET /api/orders/my-orders` - Get authenticated user's orders (requires ROLE_USER)
- `PATCH /api/orders/{orderId}/status` - Update order status (requires ROLE_ADMIN)

**User Endpoints**:
- `POST /api/users/find-or-create` - Find or create user by Telegram ID, returns JWT token

**Loyalty Endpoints**:
- `GET /api/loyalty/{userId}` - Get loyalty card for user

**Addon Endpoints**:
- `GET /api/addons` - Get all addons
- `GET /api/addons/available` - Get available addons
- `GET /api/addons/{id}` - Get addon by ID
- `POST /api/addons` - Create addon (admin only)
- `PUT /api/addons/{id}` - Update addon (admin only)
- `DELETE /api/addons/{id}` - Delete addon (admin only)

### Database Schema

**Core Tables**:
- `users` - User accounts with telegram_id and user_code (id is VARCHAR/UUID)
- `menu_items` - Food items with categories (CHEBUR, DRINKS, SNACKS, DESSERTS)
- `orders` - Orders with status tracking (user_id is VARCHAR to match users.id)
- `order_items` - Items in each order
- `loyalty_cards` - User loyalty progress
- `loyalty_codes` - One-time loyalty codes
- `addons` - Available addons (linked to CHEBUR items only)
- `menu_item_addons` - Many-to-Many relationship
- `order_item_addons` - Addons selected in orders

**Design Decisions**:
- Addons only available for CHEBUR category items
- Order items store denormalized data (name, price) for historical accuracy
- Loyalty points awarded based on total item quantity in completed orders
- User lookup supports both telegram_id and user_code
- Transactional safety with pre-validation before status changes
- Server-side price calculation prevents client-side tampering

### Configuration
- Database connection via environment variables (DATABASE_URL, PGUSER, PGPASSWORD)
- JWT configuration: JWT_SECRET (256-bit key), expiration 7 days
- Telegram Bot Token: TELEGRAM_BOT_TOKEN (for initData validation)
- Server runs on localhost:8080
- Liquibase automatically applies migrations on startup

### Testing
- Integration tests use H2 in-memory database (PostgreSQL compatibility mode)
- Test profile: `src/test/resources/application-test.properties`
- Run tests: `./gradlew test`
- Current status: 7/8 integration tests passing (AuthController 4/4, OrderController 3/4)
