# Trading Simulator

A modern full-stack web application for virtual stock trading with real-time price updates and portfolio tracking.

## Features 

- **Virtual Trading**: Buy and sell stocks with $50,000 starting balance
- **Real-time Charts**: Interactive stock price charts with live updates
- **Portfolio Tracking**: View your holdings with real-time gains/losses
- **Popular Stocks**: Trade AAPL, GOOGL, MSFT, TSLA, and AMZN
- **Responsive Design**: Works perfectly on desktop, tablet, and mobile

## Tech Stack

- **Frontend**: React + TypeScript + Tailwind CSS
- **Backend**: Spring Boot + Java 17
- **Database**: PostgreSQL
- **Charts**: Recharts
- **Containerization**: Docker + Docker Compose

## Quick Start

### Prerequisites
- Docker Desktop
- Git

### 1. Clone the Repository
```bash
git clone git@github.com:toobarahimnia/trading_simulator.git
cd trading-simulator
```

### 2. Set Up Environment
Create a `.env` file in the root directory:
```bash
ALPHA_VANTAGE_API_KEY=your_api_key_here
```
Get your free API key at [Alpha Vantage](https://www.alphavantage.co/support/#api-key)

### 3. Start with Docker (Recommended)
```bash
# Start all services
docker-compose up -d

# Wait a few minutes for all services to start
# Access the app at http://localhost:3000
```

### 4. Manual Development Setup
```bash
# Start database
docker-compose up -d postgres

# Start backend (in one terminal)
cd backend
./mvnw spring-boot:run

# Start frontend (in another terminal)
cd frontend
npm install
npm start
```

## Access Points

- **Trading App**: http://localhost:3000
- **API Documentation**: http://localhost:8090/api
- **Database Admin**: http://localhost:8080 (pgAdmin)
  - Email: `admin@trading.com`
  - Password: `admin123`

## How to Use

1. **View Stocks**: Browse real-time prices for 5 major stocks
2. **Place Orders**: Click on any stock and use the trading panel
3. **Buy Stocks**: Enter quantity and click BUY (requires sufficient balance)
4. **Sell Stocks**: Enter quantity and click SELL (requires owned shares)
5. **Track Portfolio**: Monitor your holdings and performance in real-time

## Demo Account

- **Username**: demo_user
- **Starting Balance**: $50,000
- **Available Stocks**: AAPL, GOOGL, MSFT, TSLA, AMZN

## Development

### Project Structure
```
trading-simulator/
├── backend/          # Spring Boot API
├── frontend/         # React application
├── database/         # PostgreSQL scripts
└── docker-compose.yml
```

## API Endpoints

### Stocks
- `GET /api/stocks` - List all stocks
- `GET /api/stocks/{symbol}/quote` - Get stock quote

### Trading
- `POST /api/trades` - Execute buy/sell order
- `POST /api/trades/validate` - Validate trade

### Portfolio
- `GET /api/portfolio/user/{id}` - Get user portfolio
- `GET /api/users/{id}/balance` - Get user balance


## Disclaimer

This is a **virtual trading simulator** for educational purposes only. No real money is involved. Stock prices are simulated and do not reflect actual market data.



