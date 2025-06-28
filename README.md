# Gmail Plugin 

A web application that allows users to access their Gmail emails with attachments and download them directly into their Personal storage.

## Features

- Secure Google OAuth2 authentication
- View emails with attachments from your Gmail account
- Browse through paginated email list
- View individual email attachments
- Download attachments selectively
- Responsive and user-friendly interface

## Tech Stack

### Frontend
- React.js
- JavaScript (ES6+)
- HTML5/CSS3

### Backend
- Spring Boot 2.7.18
- Java 8
- Google API Client
- Spring Security
- OAuth2 Client

## Prerequisites

- Node.js (v14 or higher)
- npm (comes with Node.js)
- Java Development Kit (JDK) 8
- Maven 3.6 or higher
- Google Cloud Platform account with Gmail API enabled

## Setup Instructions

### Backend Setup

1. Clone the repository:
   ```bash
   git clone [repository-url]
   cd gmail-capsyl-plugin
   ```

2. Configure Google OAuth2 credentials:
   - Go to Google Cloud Console (https://console.cloud.google.com)
   - Create a new project or select an existing one
   - Enable Gmail API
   - Create OAuth 2.0 Client ID
   - Add authorized redirect URI: `http://localhost:3000`
   - Copy Client ID and Client Secret

3. Configure application.properties:
   - Navigate to `backend/src/main/resources/application.properties`
   - Add your Google OAuth2 credentials:
     ```properties
     google.client.id=YOUR_CLIENT_ID
     google.client.secret=YOUR_CLIENT_SECRET
     google.scopes=https://www.googleapis.com/auth/gmail.readonly
     ```

4. Build and run the backend:
   ```bash
   cd backend
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

### Frontend Setup

1. Install dependencies:
   ```bash
   cd frontend
   npm install
   ```

2. Start the development server:
   ```bash
   npm start
   ```

3. The application will be available at http://localhost:3000

## Usage

1. Open http://localhost:3000 in your browser
2. Click "Login with Google" to authenticate
3. After successful login, you'll see a list of your emails with attachments
4. Click on an email to view its attachments
5. Click on any attachment to download it

## Project Structure

```
gmail-capsyl-plugin/
├── backend/                 # Spring Boot backend application
│   ├── src/main/java/      # Java source code
│   ├── src/main/resources/ # Configuration files
│   └── pom.xml             # Maven configuration
├── frontend/               # React frontend application
│   ├── public/            # Static assets
│   └── src/               # React source code
└── README.md              # Project documentation
```

## Security

The application uses OAuth2 for secure authentication and authorization. All API communications are protected by Spring Security and CORS policies.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
