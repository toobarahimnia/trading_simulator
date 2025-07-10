import React from 'react';
import { TrendingUp, TrendingDown, DollarSign, Briefcase } from 'lucide-react';
import { PortfolioSummary } from '../services/api';

interface PortfolioProps {
  portfolio: PortfolioSummary[];
  isLoading: boolean;
}

const Portfolio: React.FC<PortfolioProps> = ({ portfolio, isLoading }) => {
  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value);
  };

  const formatPercent = (value: number) => {
    return `${value >= 0 ? '+' : ''}${value.toFixed(2)}%`;
  };

  if (isLoading) {
    return (
      <div className="card">
        <div className="flex items-center space-x-3 mb-6">
          <div className="bg-gradient-to-r from-primary-500 to-primary-600 p-2 rounded-lg">
            <Briefcase className="h-6 w-6 text-white" />
          </div>
          <h3 className="text-xl font-bold text-gray-900">Your Portfolio</h3>
        </div>
        
        <div className="space-y-4">
          {[1, 2, 3].map((i) => (
            <div key={i} className="animate-pulse">
              <div className="bg-gray-200 h-20 rounded-lg"></div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  if (portfolio.length === 0) {
    return (
      <div className="card">
        <div className="flex items-center space-x-3 mb-6">
          <div className="bg-gradient-to-r from-primary-500 to-primary-600 p-2 rounded-lg">
            <Briefcase className="h-6 w-6 text-white" />
          </div>
          <h3 className="text-xl font-bold text-gray-900">Your Portfolio</h3>
        </div>
        
        <div className="text-center py-12">
          <div className="bg-gray-100 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
            <Briefcase className="h-8 w-8 text-gray-400" />
          </div>
          <h4 className="text-lg font-medium text-gray-900 mb-2">No investments yet</h4>
          <p className="text-gray-500">Start trading to build your portfolio!</p>
        </div>
      </div>
    );
  }

  return (
    <div className="card">
      <div className="flex items-center space-x-3 mb-6">
        <div className="bg-gradient-to-r from-primary-500 to-primary-600 p-2 rounded-lg">
          <Briefcase className="h-6 w-6 text-white" />
        </div>
        <h3 className="text-xl font-bold text-gray-900">Your Portfolio</h3>
      </div>

      <div className="space-y-4">
        {portfolio.map((position) => (
          <div 
            key={position.stockSymbol}
            className="bg-gradient-to-r from-gray-50 to-white border border-gray-200 rounded-xl p-5 hover:shadow-md transition-all duration-200"
          >
            {/* Header */}
            <div className="flex justify-between items-start mb-3">
              <div>
                <h4 className="text-lg font-bold text-gray-900">{position.stockSymbol}</h4>
                <p className="text-sm text-gray-600">{position.companyName}</p>
              </div>
              <div className="text-right">
                <p className="text-xl font-bold text-gray-900">
                  {formatCurrency(position.currentValue)}
                </p>
                <p className="text-sm text-gray-500">Current Value</p>
              </div>
            </div>

            {/* Stats Grid */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-3">
              <div>
                <p className="text-xs text-gray-500 uppercase tracking-wide">Shares</p>
                <p className="text-lg font-semibold text-gray-900">{position.quantity}</p>
              </div>
              <div>
                <p className="text-xs text-gray-500 uppercase tracking-wide">Avg Price</p>
                <p className="text-lg font-semibold text-gray-900">
                  {formatCurrency(position.averagePrice)}
                </p>
              </div>
              <div>
                <p className="text-xs text-gray-500 uppercase tracking-wide">Current Price</p>
                <p className="text-lg font-semibold text-gray-900">
                  {formatCurrency(position.currentPrice)}
                </p>
              </div>
              <div>
                <p className="text-xs text-gray-500 uppercase tracking-wide">Invested</p>
                <p className="text-lg font-semibold text-gray-900">
                  {formatCurrency(position.totalInvested)}
                </p>
              </div>
            </div>

            {/* Gain/Loss Section */}
            <div className="flex justify-between items-center pt-3 border-t border-gray-100">
              <div className="flex items-center space-x-2">
                {position.gainLoss >= 0 ? (
                  <TrendingUp className="h-5 w-5 text-success-500" />
                ) : (
                  <TrendingDown className="h-5 w-5 text-danger-500" />
                )}
                <span className="text-sm font-medium text-gray-600">
                  {position.gainLoss >= 0 ? 'Gain' : 'Loss'}
                </span>
              </div>
              <div className="text-right">
                <div className="flex items-center space-x-3">
                  <span className={`text-lg font-bold ${
                    position.gainLoss >= 0 ? 'text-success-500' : 'text-danger-500'
                  }`}>
                    {formatCurrency(position.gainLoss)}
                  </span>
                  <span className={`text-sm font-medium px-2 py-1 rounded-full ${
                    position.gainLoss >= 0 
                      ? 'bg-green-100 text-green-800' 
                      : 'bg-red-100 text-red-800'
                  }`}>
                    {formatPercent(position.gainLossPercent)}
                  </span>
                </div>
              </div>
            </div>

            {/* Progress Bar */}
            <div className="mt-3">
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div 
                  className={`h-2 rounded-full transition-all duration-500 ${
                    position.gainLoss >= 0 ? 'bg-success-500' : 'bg-danger-500'
                  }`}
                  style={{ 
                    width: `${Math.min(Math.abs(position.gainLossPercent), 100)}%` 
                  }}
                ></div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Portfolio Summary */}
      <div className="mt-6 pt-6 border-t border-gray-200">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="text-center p-4 bg-blue-50 rounded-lg">
            <DollarSign className="h-8 w-8 text-blue-500 mx-auto mb-2" />
            <p className="text-sm text-blue-600 font-medium">Total Invested</p>
            <p className="text-xl font-bold text-blue-900">
              {formatCurrency(portfolio.reduce((sum, p) => sum + p.totalInvested, 0))}
            </p>
          </div>
          <div className="text-center p-4 bg-purple-50 rounded-lg">
            <Briefcase className="h-8 w-8 text-purple-500 mx-auto mb-2" />
            <p className="text-sm text-purple-600 font-medium">Current Value</p>
            <p className="text-xl font-bold text-purple-900">
              {formatCurrency(portfolio.reduce((sum, p) => sum + p.currentValue, 0))}
            </p>
          </div>
          <div className="text-center p-4 bg-green-50 rounded-lg">
            <TrendingUp className="h-8 w-8 text-green-500 mx-auto mb-2" />
            <p className="text-sm text-green-600 font-medium">Total Gain/Loss</p>
            <p className={`text-xl font-bold ${
              portfolio.reduce((sum, p) => sum + p.gainLoss, 0) >= 0 
                ? 'text-green-900' 
                : 'text-red-900'
            }`}>
              {formatCurrency(portfolio.reduce((sum, p) => sum + p.gainLoss, 0))}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Portfolio;