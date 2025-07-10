import React, { useState, useEffect } from 'react';
import Header from './components/Header';
import StockChart from './components/StockChart';
import TradingPanel from './components/TradingPanel';
import Portfolio from './components/Portfolio';
import TransactionHistory from './components/TransactionHistory';
import { 
  apiService, 
  User, 
  StockQuote, 
  PortfolioSummary, 
  Transaction 
} from './services/api';

const AVAILABLE_STOCKS = ['AAPL', 'GOOGL', 'MSFT', 'TSLA', 'AMZN'];

function App() {
  // State Management
  const [selectedStock, setSelectedStock] = useState<string>('AAPL');
  const [stockQuote, setStockQuote] = useState<StockQuote | null>(null);
  const [user, setUser] = useState<User | null>(null);
  const [portfolio, setPortfolio] = useState<PortfolioSummary[]>([]);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [totalPortfolioValue, setTotalPortfolioValue] = useState<number>(0);
  const [totalGainLoss, setTotalGainLoss] = useState<number>(0);
  const [totalGainLossPercent, setTotalGainLossPercent] = useState<number>(0);
  
  // Loading states
  const [isLoadingUser, setIsLoadingUser] = useState(true);
  const [isLoadingPortfolio, setIsLoadingPortfolio] = useState(true);
  const [isLoadingTransactions, setIsLoadingTransactions] = useState(true);

  // Load initial data
  useEffect(() => {
    loadUserData();
    loadPortfolioData();
    loadTransactionData();
  }, []);

  // Load stock quote when selected stock changes
  useEffect(() => {
    loadStockQuote();
  }, [selectedStock]);

  // Refresh stock quotes every 30 seconds
  useEffect(() => {
    const interval = setInterval(loadStockQuote, 30000);
    return () => clearInterval(interval);
  }, [selectedStock]);

  const loadUserData = async () => {
    try {
      setIsLoadingUser(true);
      const userData = await apiService.getUser(1); // Using demo user ID 1
      setUser(userData);
    } catch (error) {
      console.error('Failed to load user data:', error);
    } finally {
      setIsLoadingUser(false);
    }
  };

  const loadStockQuote = async () => {
    try {
      const quote = await apiService.getStockQuote(selectedStock);
      setStockQuote(quote);
    } catch (error) {
      console.error('Failed to load stock quote:', error);
    }
  };

  const loadPortfolioData = async () => {
    try {
      setIsLoadingPortfolio(true);
      const [portfolioData, totalValue, gainLoss, gainLossPercent] = await Promise.all([
        apiService.getUserPortfolio(1),
        apiService.getTotalPortfolioValue(1),
        apiService.getTotalGainLoss(1),
        apiService.getTotalGainLossPercent(1)
      ]);
      
      setPortfolio(portfolioData);
      setTotalPortfolioValue(totalValue);
      setTotalGainLoss(gainLoss);
      setTotalGainLossPercent(gainLossPercent);
    } catch (error) {
      console.error('Failed to load portfolio data:', error);
      setPortfolio([]);
      setTotalPortfolioValue(0);
      setTotalGainLoss(0);
      setTotalGainLossPercent(0);
    } finally {
      setIsLoadingPortfolio(false);
    }
  };

  const loadTransactionData = async () => {
    try {
      setIsLoadingTransactions(true);
      const transactionData = await apiService.getUserTransactions(1);
      setTransactions(transactionData);
    } catch (error) {
      console.error('Failed to load transaction data:', error);
      setTransactions([]);
    } finally {
      setIsLoadingTransactions(false);
    }
  };

  const handleTradeExecuted = async () => {
    // Refresh all data after a trade is executed
    await Promise.all([
      loadUserData(),
      loadPortfolioData(),
      loadTransactionData(),
      loadStockQuote()
    ]);
  };

  const handleStockSelect = (stock: string) => {
    setSelectedStock(stock);
  };

  if (isLoadingUser) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-primary-500 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading Trading Simulator...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50">
      {/* Header */}
      <Header
        balance={user?.balance || 0}
        totalPortfolioValue={totalPortfolioValue}
        totalGainLoss={totalGainLoss}
        totalGainLossPercent={totalGainLossPercent}
      />

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Top Section - Chart and Trading Panel */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-8">
          {/* Stock Chart */}
          <div className="lg:col-span-2">
            <StockChart 
              stockQuote={stockQuote}
              selectedStock={selectedStock}
            />
          </div>

          {/* Trading Panel */}
          <div className="lg:col-span-1">
            <TradingPanel
              availableStocks={AVAILABLE_STOCKS}
              selectedStock={selectedStock}
              stockQuote={stockQuote}
              userBalance={user?.balance || 0}
              onTradeExecuted={handleTradeExecuted}
              onStockSelect={handleStockSelect}
            />
          </div>
        </div>

        {/* Bottom Section - Portfolio and Transactions */}
        <div className="grid grid-cols-1 xl:grid-cols-2 gap-8">
          {/* Portfolio */}
          <div>
            <Portfolio 
              portfolio={portfolio}
              isLoading={isLoadingPortfolio}
            />
          </div>

          {/* Transaction History */}
          <div>
            <TransactionHistory 
              transactions={transactions}
              isLoading={isLoadingTransactions}
            />
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-white border-t border-gray-200 mt-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="text-center text-gray-500 text-sm">
            <p>© 2024 Trading Simulator - Educational Purpose Only</p>
            <p className="mt-1">This is a virtual trading platform for learning. No real money is involved.</p>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default App;