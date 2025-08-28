-- Trading Simulator Database Schema

-- Users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    balance DECIMAL(12, 2) DEFAULT 50000.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stocks table (for tracking available stocks)
CREATE TABLE stocks (
    id SERIAL PRIMARY KEY,
    symbol VARCHAR(10) UNIQUE NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    current_price DECIMAL(10, 2),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- Portfolio table (current holdings)
CREATE TABLE portfolio (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    stock_symbol VARCHAR(10) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    average_price DECIMAL(10, 2) NOT NULL,
    total_invested DECIMAL(12, 2) NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, stock_symbol)
);
     
-- Insert sample data
INSERT INTO users (username, email, balance) VALUES 
('demo_user', 'demo@trading.com', 50000.00);

INSERT INTO stocks (symbol, company_name, current_price) VALUES
('AAPL', 'Apple Inc.', 150.25),
('GOOGL', 'Alphabet Inc.', 2750.00),
('MSFT', 'Microsoft Corporation', 280.00),
('TSLA', 'Tesla Inc.', 220.50),
('AMZN', 'Amazon.com Inc.', 3200.00);

-- Create indexes for better performance - speed up lookups
CREATE INDEX idx_portfolio_user_id ON portfolio(user_id);
CREATE INDEX idx_stocks_symbol ON stocks(symbol);