# Replit Agent Instructions

## Overview

This is a Telegram Web App for "Чебуречная Вкус Востока" (Cheburek House "Taste of the East"), a food ordering application. The project is a client-side web application designed to run within the Telegram messaging platform, allowing users to browse and order food items directly through Telegram's WebApp interface.

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

### Potential Future Dependencies
Based on the application type, the following may be added:
- **Backend API**: For menu management, order processing, and business logic
- **Database**: For storing menu items, orders, user preferences (likely PostgreSQL with Drizzle ORM if following standard patterns)
- **Payment Gateway**: If using non-Telegram payment methods
- **Analytics**: For tracking user behavior and order patterns