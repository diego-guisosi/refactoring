package calculator;

import feed.FinancialFeed;
import model.News;
import model.NewsSource;
import model.Price;
import model.Stock;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PriceCalculatorV2 {

    private final List<Price> stockPriceDataSource;
    private final boolean adjustByNews;
    private FinancialFeed feed;

    private PriceCalculatorV2(List<Price> stockPriceDataSource) {
        this.stockPriceDataSource = stockPriceDataSource;
        this.adjustByNews = false;
    }

    private PriceCalculatorV2(List<Price> stockPriceDataSource, boolean adjustByNews) {
        this.stockPriceDataSource = stockPriceDataSource;
        this.adjustByNews = adjustByNews;
        this.feed = FinancialFeed.subscribe(FinancialFeed.CNNMoney);
    }
    
    public static PriceCalculatorV2 createAveragePriceWithoutAdjustmentCalculator(List<Price> prices) {
        return new PriceCalculatorV2(prices);
    }

    public static PriceCalculatorV2 createAveragePriceAdjustedByNewsCalculator(List<Price> prices) {
        return new PriceCalculatorV2(prices, true);
    }

    public double calculate(Stock stock) {
        double averagePrice = 0.0;

        List<Price> currentStockPrices = findCurrentPricesOf(stock);
        double sumOfCurrentStockPrices = sumOf(currentStockPrices);
        averagePrice = sumOfCurrentStockPrices / currentStockPrices.size();

        if (adjustByNews) {
            List<News> stockNationalNews = fetchNationalNewsOf(stock);
            averagePrice = adjustPriceByNews(averagePrice, stockNationalNews);
        }

        return averagePrice;
    }

    private List<Price> findCurrentPricesOf(Stock stock) {
        return stockPriceDataSource.stream()
                .filter(this::isNotHistoricalPrice)
                .filter(price -> matchStockAgainstPrice(stock, price))
                .collect(Collectors.toList());
    }

    private boolean isNotHistoricalPrice(Price price) {
        return !isHistoricalPrice(price);
    }

    private boolean isHistoricalPrice(Price price) {
        return price.getCreationDate().isBefore(LocalDate.now());
    }

    private boolean matchStockAgainstPrice(Stock stock, Price price) {
        return price.getSymbol().equals(stock.getSymbol());
    }

    private double sumOf(List<Price> stockCurrentPrices) {
        return stockCurrentPrices
                .stream()
                .mapToDouble(Price::getValue)
                .sum();
    }

    private List<News> fetchNationalNewsOf(Stock stock) {
        return feed.fetch(stock.getSymbol())
                .stream()
                .filter(this::isNationalNews)
                .collect(Collectors.toList());
    }

    private boolean isNationalNews(News news) {
        return news.getSource() == NewsSource.NATIONAL;
    }

    private double adjustPriceByNews(double averagePrice, List<News> news) {
        double sumOfPriceAdjustmentFactor = sumPriceAdjustmentFactorOf(news);
        return averagePrice + sumOfPriceAdjustmentFactor;
    }

    private double sumPriceAdjustmentFactorOf(List<News> news) {
        return news.stream()
                .mapToDouble(News::getPriceAdjustmentFactor)
                .sum();
    }

}