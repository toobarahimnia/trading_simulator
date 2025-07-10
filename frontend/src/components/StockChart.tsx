import React, { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { StockQuote } from '../services/api';

interface StockChartProps {
  stockQuote: StockQuote | null;
  selectedStock: string;
}

interface ChartDataPoint {
  time: string;
  price: number;
  timestamp: number;
}

const StockChart: React.FC<StockChartProps> = ({ stockQuote, selectedStock }) => {
  const [chartData, setChartData] = useState<ChartDataPoint[]>([]);

  useEffect(() => {
    // Initialize chart with some historical data simulation
    if (stockQuote) {
      const basePrice = stockQuote.price;
      const newData: ChartDataPoint[] = [];
      
      // Generate 20 data points for the last 20 minutes
      for (let i = 19; i >= 0; i--) {
        const timestamp = Date.now() - (i * 60000); // 1 minute intervals
        const variation = (Math.random() - 0.5) * 0.02; // ±1% variation
        const price = basePrice * (1 + variation);
        
        newData.push({
          time: new Date(timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          price: Number(price.toFixed(2)),
          timestamp
        });
      }
      
      setChartData(newData);
    }
  }, [selectedStock, stockQuote]);

  useEffect(() => {
    // Update chart with new data every 5 seconds
    const interval = setInterval(() => {
      if (stockQuote) {
        const currentTime = Date.now();
        const variation = (Math.random() - 0.5) * 0.01; // ±0.5% variation
        const newPrice = stockQuote.price * (1 + variation);
        
        const newDataPoint: ChartDataPoint = {
          time: new Date(currentTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          price: Number(newPrice.toFixed(2)),
          timestamp: currentTime
        };

        setChartData(prevData => {
          const updatedData = [...prevData, newDataPoint];
          // Keep only last 20 data points
          return updatedData.slice(-20);
        });
      }
    }, 5000);

    return () => clearInterval(interval);
  }, [stockQuote]);

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value);
  };

  const formatPercent = (value: number) => {
    return `${value >= 0 ? '+' : ''}${value.toFixed(2)}%`;
  };

  if (!stockQuote) {
    return (
      <div className="card">
        <div className="flex items-center justify-center h-80">
          <div className="text-center">
            <div className="animate-pulse-slow bg-gray-200 h-8 w-32 rounded mb-4 mx-auto"></div>
            <p className="text-gray-500">Select a stock to view chart</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="card">
      {/* Stock Header */}
      <div className="flex justify-between items-start mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">{stockQuote.symbol}</h2>
          <p className="text-gray-600">{stockQuote.companyName}</p>
        </div>
        <div className="text-right">
          <p className="text-3xl font-bold text-gray-900">
            {formatCurrency(stockQuote.price)}
          </p>
          <div className="flex items-center space-x-2 mt-1">
            <span className={`text-lg font-semibold ${
              stockQuote.change >= 0 ? 'text-success-500' : 'text-danger-500'
            }`}>
              {stockQuote.change >= 0 ? '+' : ''}{formatCurrency(stockQuote.change)}
            </span>
            <span className={`text-sm font-medium px-2 py-1 rounded-full ${
              stockQuote.change >= 0 
                ? 'bg-green-100 text-green-800' 
                : 'bg-red-100 text-red-800'
            }`}>
              {formatPercent(stockQuote.changePercent)}
            </span>
          </div>
        </div>
      </div>

      {/* Chart */}
      <div className="h-80">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={chartData}>
            <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
            <XAxis 
              dataKey="time" 
              stroke="#6b7280"
              fontSize={12}
            />
            <YAxis 
              stroke="#6b7280"
              fontSize={12}
              tickFormatter={(value) => `$${value}`}
            />
            <Tooltip 
              labelFormatter={(label) => `Time: ${label}`}
              formatter={(value: number) => [formatCurrency(value), 'Price']}
              contentStyle={{
                backgroundColor: '#fff',
                border: '1px solid #e5e7eb',
                borderRadius: '8px',
                boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)'
              }}
            />
            <Line 
              type="monotone" 
              dataKey="price" 
              stroke={stockQuote.change >= 0 ? '#27ae60' : '#e74c3c'}
              strokeWidth={3}
              dot={false}
              activeDot={{ r: 6, fill: stockQuote.change >= 0 ? '#27ae60' : '#e74c3c' }}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>

      {/* Chart Info */}
      <div className="mt-4 text-sm text-gray-500 text-center">
        Real-time simulation • Updates every 5 seconds
      </div>
    </div>
  );
};

export default StockChart;