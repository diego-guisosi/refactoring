package service;

import calculator.PriceCalculatorV1;
import model.Price;
import model.Stock;
import repository.StockRepository;

import java.util.List;

public class StockPriceServiceV1 {

    private final PriceCalculatorV1 priceWithNewsCalculator;
    private final PriceCalculatorV1 priceDefaultCalculator;
    private final StockRepository stockRepository;

    public StockPriceServiceV1(List<Price> prices, StockRepository stockRepository) {
        this.priceWithNewsCalculator = new PriceCalculatorV1(prices, true);
        this.priceDefaultCalculator = new PriceCalculatorV1(prices, false);
        this.stockRepository = stockRepository;
    }

    public double calculateAveragePriceAdjustedByNews(String ticker) {
        Stock stock = stockRepository.findByTicker(ticker);
        return priceWithNewsCalculator.calculate(stock);
    }

    public double calculateAverageWithoutAdjustment(String ticker) {
        Stock stock = stockRepository.findByTicker(ticker);
        return priceDefaultCalculator.calculate(stock);
    }

}
