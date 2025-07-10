import React, { useState, useEffect } from 'react';
import { ShoppingCart, TrendingDown, AlertCircle, CheckCircle } from 'lucide-react';
import { StockQuote, TradeRequest, apiService } from '../services/api';

interface TradingPanelProps {
  availableStocks: string[];
  selectedStock: string;
  stockQuote: StockQuote | null;
  userBalance: number;
  onTradeExecuted: () => void;
  onStockSelect: (stock: string) => void;
}

const TradingPanel: React.FC<TradingPanelProps> = ({
  availableStocks,
  selectedStock,
  stockQuote,
  userBalance,
  onTradeExecuted,
  onStockSelect
}) => {
  const [quantity, setQuantity] = useState<number>(1);
  const [orderType, setOrderType] = useState<'MARKET'>('MARKET');
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error' | null; text: string }>({ type: null, text: '' });
  const [availableShares, setAvailableShares] = useState<number>(0);

  useEffect(() => {
    // Fetch available shares for the selected stock
    const fetchAvailableShares = async () => {
      try {
        const shares = await apiService.getAvailableShares(1, selectedStock); // Using user ID 1
        setAvailableShares(shares);
      } catch (error) {
        setAvailableShares(0);
      }
    };

    if (selectedStock) {
      fetchAvailableShares();
    }
  }, [selectedStock, onTradeExecuted]);

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value);
  };

  const calculateEstimatedCost = () => {
    if (!stockQuote || !quantity) return 0;
    return stockQuote.price * quantity;
  };

  const canBuy = () => {
    const estimatedCost = calculateEstimatedCost();
    return userBalance >= estimatedCost && quantity > 0;
  };

  const canSell = () => {
    return availableShares >= quantity && quantity > 0;
  };

  const executeTrade = async (transactionType: 'BUY' | 'SELL') => {
    if (!stockQuote) return;

    setIsLoading(true);
    setMessage({ type: null, text: '' });

    try {
      const tradeRequest: TradeRequest = {
        userId: 1, // Using demo user
        stockSymbol: selectedStock,
        transactionType,
        quantity
      };

      await apiService.executeTrade(tradeRequest);
      
      setMessage({ 
        type: 'success', 
        text: `Successfully ${transactionType.toLowerCase()}ed ${quantity} shares of ${selectedStock}!` 
      });
      
      // Reset form
      setQuantity(1);
      
      // Notify parent component
      onTradeExecuted();
      
    } catch (error: any) {
      setMessage({ 
        type: 'error', 
        text: error.response?.data || 'Trade execution failed. Please try again.' 
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="card">
      <div className="flex items-center space-x-3 mb-6">
        <div className="bg-gradient-to-r from-primary-500 to-primary-600 p-2 rounded-lg">
          <ShoppingCart className="h-6 w-6 text-white" />
        </div>
        <h3 className="text-xl font-bold text-gray-900">Place Order</h3>
      </div>

      {/* Stock Selection */}
      <div className="mb-6">
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Stock Symbol
        </label>
        <div className="grid grid-cols-2 gap-2">
          {availableStocks.map((stock) => (
            <button
              key={stock}
              onClick={() => onStockSelect(stock)}
              className={`p-3 rounded-lg border-2 transition-all duration-200 ${
                selectedStock === stock
                  ? 'border-primary-500 bg-primary-50 text-primary-700'
                  : 'border-gray-200 hover:border-gray-300'
              }`}
            >
              <span className="font-semibold">{stock}</span>
            </button>
          ))}
        </div>
      </div>

      {/* Quantity Input */}
      <div className="mb-6">
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Quantity
        </label>
        <input
          type="number"
          min="1"
          value={quantity}
          onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value) || 1))}
          className="input-field"
          placeholder="Enter quantity"
        />
      </div>

      {/* Order Type */}
      <div className="mb-6">
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Order Type
        </label>
        <select
          value={orderType}
          onChange={(e) => setOrderType(e.target.value as 'MARKET')}
          className="input-field"
        >
          <option value="MARKET">Market Order</option>
        </select>
      </div>

      {/* Position Info */}
      {availableShares > 0 && (
        <div className="mb-6 p-3 bg-blue-50 rounded-lg">
          <p className="text-sm text-blue-800">
            <strong>Current Position:</strong> {availableShares} shares
          </p>
        </div>
      )}

      {/* Estimated Cost */}
      {stockQuote && (
        <div className="mb-6 p-4 bg-gray-50 rounded-lg">
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-600">Estimated Cost:</span>
            <span className="text-lg font-bold text-gray-900">
              {formatCurrency(calculateEstimatedCost())}
            </span>
          </div>
          <div className="flex justify-between items-center mt-1">
            <span className="text-xs text-gray-500">Price per share:</span>
            <span className="text-sm text-gray-700">
              {formatCurrency(stockQuote.price)}
            </span>
          </div>
        </div>
      )}

      {/* Trade Buttons */}
      <div className="grid grid-cols-2 gap-4 mb-4">
        <button
          onClick={() => executeTrade('BUY')}
          disabled={!canBuy() || isLoading}
          className={`btn-success flex items-center justify-center space-x-2 ${
            !canBuy() || isLoading ? 'opacity-50 cursor-not-allowed' : ''
          }`}
        >
          <ShoppingCart className="h-5 w-5" />
          <span>{isLoading ? 'Buying...' : 'BUY'}</span>
        </button>

        <button
          onClick={() => executeTrade('SELL')}
          disabled={!canSell() || isLoading}
          className={`btn-danger flex items-center justify-center space-x-2 ${
            !canSell() || isLoading ? 'opacity-50 cursor-not-allowed' : ''
          }`}
        >
          <TrendingDown className="h-5 w-5" />
          <span>{isLoading ? 'Selling...' : 'SELL'}</span>
        </button>
      </div>

      {/* Messages */}
      {message.type && (
        <div className={`p-3 rounded-lg flex items-center space-x-2 ${
          message.type === 'success' 
            ? 'bg-green-50 text-green-800 border border-green-200' 
            : 'bg-red-50 text-red-800 border border-red-200'
        }`}>
          {message.type === 'success' ? (
            <CheckCircle className="h-5 w-5" />
          ) : (
            <AlertCircle className="h-5 w-5" />
          )}
          <span className="text-sm">{message.text}</span>
        </div>
      )}

      {/* Trading Rules */}
      <div className="mt-6 text-xs text-gray-500 space-y-1">
        <p>• Market orders execute immediately at current price</p>
        <p>• Minimum order: 1 share</p>
          </div> 
        </div>
      );
    };
    
    export default TradingPanel;
