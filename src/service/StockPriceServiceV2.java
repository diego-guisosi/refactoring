package service;

import calculator.PriceCalculatorV2;
import model.Price;
import model.Stock;
import repository.StockRepository;

import java.util.List;

public class StockPriceServiceV2 {

    private final PriceCalculatorV2 averagePriceAdjustedByNewsCalculator;
    private final PriceCalculatorV2 averagePriceWithoutAdjustmentCalculator;
    private final StockRepository stockRepository;

    public StockPriceServiceV2(List<Price> prices, StockRepository stockRepository) {
        this.averagePriceAdjustedByNewsCalculator = PriceCalculatorV2.createAveragePriceAdjustedByNewsCalculator(prices);
        this.averagePriceWithoutAdjustmentCalculator = PriceCalculatorV2.createAveragePriceWithoutAdjustmentCalculator(prices);
        this.stockRepository = stockRepository;
    }

    public double calculateAveragePriceAdjustedByNews(String ticker) {
        Stock stock = stockRepository.findByTicker(ticker);
        return averagePriceAdjustedByNewsCalculator.calculate(stock);
    }

    public double calculateAverageWithoutAdjustment(String ticker) {
        Stock stock = stockRepository.findByTicker(ticker);
        return averagePriceWithoutAdjustmentCalculator.calculate(stock);
    }

}
