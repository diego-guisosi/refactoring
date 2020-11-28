package repository;

import model.Stock;

public interface StockRepository {

    Stock findByTicker(String ticker);

}
