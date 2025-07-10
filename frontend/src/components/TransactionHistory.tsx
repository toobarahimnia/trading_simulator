import React from 'react';
import { Clock, TrendingUp, TrendingDown, ArrowUpCircle, ArrowDownCircle } from 'lucide-react';
import { Transaction } from '../services/api';

interface TransactionHistoryProps {
  transactions: Transaction[];
  isLoading: boolean;
}

const TransactionHistory: React.FC<TransactionHistoryProps> = ({ transactions, isLoading }) => {
  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value);
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  if (isLoading) {
    return (
      <div className="card">
        <div className="flex items-center space-x-3 mb-6">
          <div className="bg-gradient-to-r from-primary-500 to-primary-600 p-2 rounded-lg">
            <Clock className="h-6 w-6 text-white" />
          </div>
          <h3 className="text-xl font-bold text-gray-900">Recent Transactions</h3>
        </div>
        
        <div className="space-y-3">
          {[1, 2, 3, 4, 5].map((i) => (
            <div key={i} className="animate-pulse">
              <div className="bg-gray-200 h-16 rounded-lg"></div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  if (transactions.length === 0) {
    return (
      <div className="card">
        <div className="flex items-center space-x-3 mb-6">
          <div className="bg-gradient-to-r from-primary-500 to-primary-600 p-2 rounded-lg">
            <Clock className="h-6 w-6 text-white" />
          </div>
          <h3 className="text-xl font-bold text-gray-900">Recent Transactions</h3>
        </div>
        
        <div className="text-center py-8">
          <div className="bg-gray-100 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
            <Clock className="h-8 w-8 text-gray-400" />
          </div>
          <h4 className="text-lg font-medium text-gray-900 mb-2">No transactions yet</h4>
          <p className="text-gray-500">Your trading history will appear here</p>
        </div>
      </div>
    );
  }

  return (
    <div className="card">
      <div className="flex items-center space-x-3 mb-6">
        <div className="bg-gradient-to-r from-primary-500 to-primary-600 p-2 rounded-lg">
          <Clock className="h-6 w-6 text-white" />
        </div>
        <h3 className="text-xl font-bold text-gray-900">Recent Transactions</h3>
      </div>

      <div className="space-y-3 max-h-96 overflow-y-auto">
        {transactions.slice(0, 10).map((transaction) => (
          <div 
            key={transaction.id}
            className="flex items-center justify-between p-4 bg-gradient-to-r from-gray-50 to-white border border-gray-200 rounded-lg hover:shadow-md transition-all duration-200"
          >
            {/* Left Side - Transaction Info */}
            <div className="flex items-center space-x-4">
              {/* Icon */}
              <div className={`p-2 rounded-lg ${
                transaction.transactionType === 'BUY' 
                  ? 'bg-green-100' 
                  : 'bg-red-100'
              }`}>
                {transaction.transactionType === 'BUY' ? (
                  <ArrowUpCircle className="h-6 w-6 text-success-500" />
                ) : (
                  <ArrowDownCircle className="h-6 w-6 text-danger-500" />
                )}
              </div>

              {/* Transaction Details */}
              <div>
                <div className="flex items-center space-x-2">
                  <span className={`text-sm font-bold px-2 py-1 rounded-full ${
                    transaction.transactionType === 'BUY'
                      ? 'bg-green-100 text-green-800'
                      : 'bg-red-100 text-red-800'
                  }`}>
                    {transaction.transactionType}
                  </span>
                  <span className="font-semibold text-gray-900">
                    {transaction.stockSymbol}
                  </span>
                </div>
                <p className="text-sm text-gray-600 mt-1">
                  {transaction.quantity} shares at {formatCurrency(transaction.pricePerShare)}
                </p>
                <p className="text-xs text-gray-500">
                  {formatDate(transaction.transactionDate)}
                </p>
              </div>
            </div>

            {/* Right Side - Amount */}
            <div className="text-right">
              <p className={`text-lg font-bold ${
                transaction.transactionType === 'BUY' 
                  ? 'text-danger-500' 
                  : 'text-success-500'
              }`}>
                {transaction.transactionType === 'BUY' ? '-' : '+'}
                {formatCurrency(transaction.totalAmount)}
              </p>
              <p className="text-xs text-gray-500">
                Total Amount
              </p>
            </div>
          </div>
        ))}
      </div>

      {/* Show More Button */}
      {transactions.length > 10 && (
        <div className="mt-4 text-center">
          <button className="text-primary-600 hover:text-primary-700 font-medium text-sm">
            View All Transactions →
          </button>
        </div>
      )}
    </div>
  );
};

export default TransactionHistory;