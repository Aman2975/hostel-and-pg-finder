# Hostel and PG Finder App

A complete Android application for finding and booking hostels and PGs, with a MySQL backend API.

## 🏗️ Architecture

```
Android App (Kotlin + Jetpack Compose) ←→ REST API (Node.js + Express) ←→ MySQL Database
```

## 📱 Features

### Android App
- **Student Authentication** - Login and registration
- **Hostel Allotment** - Request hostel accommodation
- **PG Finder** - Search and filter PGs by area
- **Real-time Data** - Live updates from MySQL database
- **Modern UI** - Material 3 design with purple theme

### Backend API
- **RESTful API** - Clean and well-documented endpoints
- **MySQL Database** - Relational data storage
- **JWT Authentication** - Secure user sessions
- **Data Validation** - Input validation and error handling
- **CORS Support** - Cross-origin requests enabled

## 🚀 Quick Start

### Prerequisites
- **Node.js** (v16 or higher)
- **MySQL** (v8.0 or higher)
- **Android Studio** (Latest version)
- **Java 11** or higher

### Backend Setup

1. **Install Dependencies**
   ```bash
   cd backend
   npm install
   ```

2. **Configure Database**
   - Create MySQL database
   - Update `backend/config.env` with your MySQL credentials:
   ```env
   DB_HOST=localhost
   DB_USER=root
   DB_PASSWORD=your_mysql_password
   DB_NAME=hostel_pg_finder
   ```

3. **Start Backend Server**
   ```bash
   npm start
   # or for development
   npm run dev
   ```

   Server will run on `http://localhost:3000`

### Android Setup

1. **Open Project**
   - Open `hostelallotementandpgfinder` folder in Android Studio

2. **Update API URL** (if needed)
   - For emulator: `http://10.0.2.2:3000/api/`
   - For real device: `http://YOUR_IP_ADDRESS:3000/api/`
   - Update in `app/src/main/java/.../network/ApiClient.kt`

3. **Build and Run**
   - Sync project with Gradle files
   - Run on emulator or device

## 📊 Database Schema

### Tables
- **users** - Student information and authentication
- **hostels** - Hostel listings and details
- **pgs** - PG listings and details
- **bookings** - Student booking requests
- **favorites** - Student saved properties
- **reviews** - Property reviews and ratings

### Sample Data
The database includes sample hostels and PGs for Patiala/PUP area:
- Bhadurgarh, Phase 1, Phase 2, Near PUP, Patiala City

## 🔗 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new student
- `POST /api/auth/login` - Student login
- `GET /api/auth/profile` - Get user profile
- `PUT /api/auth/profile` - Update user profile

### Hostels
- `GET /api/hostels` - Get all hostels (with filters)
- `GET /api/hostels/:id` - Get hostel by ID
- `GET /api/hostels/area/:area` - Get hostels by area
- `GET /api/hostels/search/:term` - Search hostels

### PGs
- `GET /api/pgs` - Get all PGs (with filters)
- `GET /api/pgs/:id` - Get PG by ID
- `GET /api/pgs/area/:area` - Get PGs by area
- `GET /api/pgs/search/:term` - Search PGs

## 🎨 UI Features

### Color Scheme
- **Primary**: Deep Purple (#9C27B0)
- **Secondary**: Purple Grey (#512DA8)
- **Tertiary**: Pink (#C2185B)
- **Backgrounds**: Light purple and pink gradients

### Screens
1. **Login Screen** - Student authentication
2. **Signup Screen** - New student registration
3. **Choice Screen** - Hostel vs PG selection
4. **Hostel Allotment** - Request form
5. **PG Finder** - Search and filter PGs

## 🛠️ Development

### Backend Development
```bash
cd backend
npm run dev  # Start with nodemon for auto-restart
```

### Android Development
- Use Android Studio
- Enable USB debugging for device testing
- Use emulator for quick testing

### Database Management
```sql
-- Connect to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE hostel_pg_finder;

-- Import schema
source backend/database/schema.sql;
```

## 📱 Testing

### Backend API Testing
```bash
# Health check
curl http://localhost:3000/health

# Get hostels
curl http://localhost:3000/api/hostels

# Get PGs
curl http://localhost:3000/api/pgs
```

### Android Testing
1. **Login**: Use demo credentials (2024001 / student123)
2. **Register**: Create new student account
3. **Search**: Test hostel and PG search functionality
4. **Filters**: Test area and price filters

## 🔧 Configuration

### Backend Configuration
- **Port**: 3000 (configurable in config.env)
- **Database**: MySQL with connection pooling
- **CORS**: Enabled for Android app
- **Rate Limiting**: 100 requests per 15 minutes

### Android Configuration
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 14)
- **Architecture**: Modern Android with Jetpack Compose

## 🚀 Deployment

### Backend Deployment
1. **VPS/Cloud Server**
   - Install Node.js and MySQL
   - Clone repository
   - Configure environment variables
   - Start with PM2: `pm2 start server.js`

2. **Database**
   - Create production database
   - Import schema
   - Configure backups

### Android Deployment
1. **Generate Signed APK**
   - Build → Generate Signed Bundle/APK
   - Create keystore
   - Sign and build

2. **Google Play Store**
   - Create developer account
   - Upload APK/AAB
   - Configure store listing

## 📝 API Documentation

### Request/Response Format
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "count": 10
}
```

### Authentication
Include JWT token in Authorization header:
```
Authorization: Bearer YOUR_JWT_TOKEN
```

## 🤝 Contributing

1. Fork the repository
2. Create feature branch
3. Make changes
4. Test thoroughly
5. Submit pull request

## 📄 License

MIT License - see LICENSE file for details

## 🆘 Support

For issues and questions:
1. Check existing issues
2. Create new issue with details
3. Include logs and error messages

## 🔄 Updates

### Recent Changes
- ✅ MySQL backend integration
- ✅ REST API implementation
- ✅ Android networking setup
- ✅ Purple theme implementation
- ✅ Student-focused design

### Planned Features
- 🔄 Real-time notifications
- 🔄 Image upload for properties
- 🔄 Advanced search filters
- 🔄 Booking management
- 🔄 Payment integration

