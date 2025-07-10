import React from 'react';
import { TrendingUp, DollarSign } from 'lucide-react';

interface HeaderProps {
  balance: number;
  totalPortfolioValue: number;
  totalGainLoss: number;
  totalGainLossPercent: number;
}

const Header: React.FC<HeaderProps> = ({ 
  balance, 
  totalPortfolioValue, 
  totalGainLoss, 
  totalGainLossPercent 
}) => {
  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value);
  };

  const formatPercent = (value: number) => {
    return `${value >= 0 ? '+' : ''}${value.toFixed(2)}%`;
  };

  return (
    <header className="bg-white shadow-lg border-b border-gray-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center py-6">
          {/* Logo */}
          <div className="flex items-center space-x-3">
            <div className="bg-gradient-to-r from-primary-500 to-primary-600 p-3 rounded-xl">
              <TrendingUp className="h-8 w-8 text-white" />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Trading Simulator</h1>
              <p className="text-sm text-gray-500">Virtual Stock Trading Platform</p>
            </div>
          </div>

          {/* Stats */}
          <div className="flex items-center space-x-8">
            {/* Cash Balance */}
            <div className="text-right">
              <p className="text-sm text-gray-500">Cash Balance</p>
              <p className="text-xl font-bold text-gray-900 flex items-center">
                <DollarSign className="h-5 w-5 mr-1" />
                {formatCurrency(balance)}
              </p>
            </div>

            {/* Portfolio Value */}
            <div className="text-right">
              <p className="text-sm text-gray-500">Portfolio Value</p>
              <p className="text-xl font-bold text-gray-900">
                {formatCurrency(totalPortfolioValue)}
              </p>
            </div>

            {/* Total Gain/Loss */}
            <div className="text-right">
              <p className="text-sm text-gray-500">Total Gain/Loss</p>
              <div className="flex items-center space-x-2">
                <p className={`text-xl font-bold ${
                  totalGainLoss >= 0 ? 'text-success-500' : 'text-danger-500'
                }`}>
                  {formatCurrency(totalGainLoss)}
                </p>
                <span className={`text-sm font-medium px-2 py-1 rounded-full ${
                  totalGainLoss >= 0 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-red-100 text-red-800'
                }`}>
                  {formatPercent(totalGainLossPercent)}
                </span>
              </div>
            </div>

            {/* Total Account Value */}
            <div className="bg-gradient-to-r from-primary-500 to-primary-600 text-white px-6 py-4 rounded-xl">
              <p className="text-sm opacity-90">Total Account Value</p>
              <p className="text-2xl font-bold">
                {formatCurrency(balance + totalPortfolioValue)}
              </p>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;