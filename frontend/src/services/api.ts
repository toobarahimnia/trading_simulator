import axios from 'axios';

// API Base Configuration
const API_BASE_URL = 'http://localhost:8090/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Types
export interface User {
  id: number;
  username: string;
  email: string;
  balance: number;
  createdAt: string;
  updatedAt: string;
}

export interface Stock {
  id: number;
  symbol: string;
  companyName: string;
  currentPrice: number;
  lastUpdated: string;
}

export interface StockQuote {
  symbol: string;
  companyName: string;
  price: number;
  change: number;
  changePercent: number;
  lastUpdated: string;
}

export interface TradeRequest {
  userId: number;
  stockSymbol: string;
  transactionType: 'BUY' | 'SELL';
  quantity: number;
}

export interface Transaction {
  id: number;
  userId: number;
  stockSymbol: string;
  transactionType: string;
  quantity: number;
  pricePerShare: number;
  totalAmount: number;
  transactionDate: string;
}

export interface PortfolioSummary {
  stockSymbol: string;
  companyName: string;
  quantity: number;
  averagePrice: number;
  currentPrice: number;
  totalInvested: number;
  currentValue: number;
  gainLoss: number;
  gainLossPercent: number;
}

// API Functions
export const apiService = {
  // User APIs
  getUser: async (id: number): Promise<User> => {
    const response = await api.get(`/users/${id}`);
    return response.data;
  },

  getUserByUsername: async (username: string): Promise<User> => {
    const response = await api.get(`/users/username/${username}`);
    return response.data;
  },

  getUserBalance: async (id: number): Promise<number> => {
    const response = await api.get(`/users/${id}/balance`);
    return response.data;
  },

  // Stock APIs
  getAllStocks: async (): Promise<Stock[]> => {
    const response = await api.get('/stocks');
    return response.data;
  },

  getStock: async (symbol: string): Promise<Stock> => {
    const response = await api.get(`/stocks/${symbol}`);
    return response.data;
  },

  getStockQuote: async (symbol: string): Promise<StockQuote> => {
    const response = await api.get(`/stocks/${symbol}/quote`);
    return response.data;
  },

  getRealTimeQuote: async (symbol: string): Promise<StockQuote> => {
    const response = await api.get(`/stocks/${symbol}/realtime`);
    return response.data;
  },

  // Trading APIs
  executeTrade: async (tradeRequest: TradeRequest): Promise<Transaction> => {
    const response = await api.post('/trades', tradeRequest);
    return response.data;
  },

  validateTrade: async (tradeRequest: TradeRequest): Promise<boolean> => {
    const response = await api.post('/trades/validate', tradeRequest);
    return response.data;
  },

  // Portfolio APIs
  getUserPortfolio: async (userId: number): Promise<PortfolioSummary[]> => {
    const response = await api.get(`/portfolio/user/${userId}`);
    return response.data;
  },

  getTotalPortfolioValue: async (userId: number): Promise<number> => {
    const response = await api.get(`/portfolio/user/${userId}/value`);
    return response.data;
  },

  getTotalGainLoss: async (userId: number): Promise<number> => {
    const response = await api.get(`/portfolio/user/${userId}/gainloss`);
    return response.data;
  },

  getTotalGainLossPercent: async (userId: number): Promise<number> => {
    const response = await api.get(`/portfolio/user/${userId}/gainloss-percent`);
    return response.data;
  },

  getUserTransactions: async (userId: number): Promise<Transaction[]> => {
    const response = await api.get(`/portfolio/user/${userId}/transactions`);
    return response.data;
  },

  getAvailableShares: async (userId: number, stockSymbol: string): Promise<number> => {
    const response = await api.get(`/portfolio/user/${userId}/shares/${stockSymbol}`);
    return response.data;
  },
};

export default api;