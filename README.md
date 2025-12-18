# Mafia Game

Real-time Mafia (Werewolf) game web application with WebSocket support and reconnect functionality.

## Tech Stack

### Backend

- Java 17+
- Spring Boot 3
- Spring WebSocket (STOMP)
- In-memory storage
- Lombok

### Frontend

- React 18
- TypeScript
- Vite
- Emotion (styled components)
- Redux Toolkit
- Axios
- STOMP over WebSocket

## Project Structure

```
Mafia-Online/
├── backend/          # Spring Boot backend
│   └── src/main/java/com/mafia/
└── frontend/         # React frontend
    └── src/
```

## Getting Started

### Backend

```bash
cd backend
mvn spring-boot:run
```

Server runs on `http://localhost:8080`

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on `http://localhost:3000`

## Architecture

- **REST APIs**: Only for lobby creation and joining
- **WebSockets**: All real-time gameplay (role assignment, phase changes, announcements)
- **Reconnect Support**: Players can refresh and resume their game
- **Token-based Auth**: Each player gets a UUID token stored in localStorage

## MVP Features

- [x] Create/Join lobby
- [x] WebSocket connection with token validation
- [x] Role assignment (random)
- [x] Phase transitions (Night/Day)
- [x] Reconnect support
- [ ] Night actions (Mafia kill, Doctor save, Detective investigate)
- [ ] Day voting
- [ ] Win condition detection

## TODO

- Add manual role assignment by God
- Implement voting system
- Add night action handlers
- Add chat functionality
- Persist state to database
