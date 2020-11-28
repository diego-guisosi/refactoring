package calculator;

import feed.FinancialFeed;
import model.News;
import model.NewsSource;
import model.Price;
import model.Stock;

import java.time.LocalDate;
import java.util.List;

public class PriceCalculatorV1 {

    private final List<Price> prices;
    private final boolean news;
    private FinancialFeed feed;

    // Construtor que recebe uma lista de precos de acoes e um booleano que
    // indica se noticias devem ser consideradas durante o calculo de preco
    public PriceCalculatorV1(List<Price> prices, boolean news) {
        this.prices = prices;
        this.news = news;

        if (news) {
            // Inscricao no feed da CNN Money para recebimento de noticias
            this.feed = FinancialFeed.subscribe(FinancialFeed.CNNMoney);
        }
    }

    public double calculate(Stock s) {
        double result = 0.0;
        int count = 0;
        for (Price p : prices) {
            //Verifica se o preco historico esta relacionado a acao passada por parametro
            if (p.getSymbol().equals(s.getSymbol())) {
                //Desconsidera precos que nao tenham sido calculados hoje
                if (!p.getCreationDate().isBefore(LocalDate.now())) {
                    result += p.getValue();
                    count++;
                }
            }
        }

        result /= count;

        if (news) {
            List<News> news = feed.fetch(s.getSymbol());
            removeInternational(news);
            for (News n : news) {
                result += n.getPriceAdjustmentFactor();
            }
        }

        return result;
    }

    private void removeHighFactors (List<News> news) {
        news.removeIf(n -> n.getPriceAdjustmentFactor() > 0.5);
    }

    private void removeInternational (List <News> news) {
        news.removeIf(n -> n.getSource() == NewsSource.INTERNATIONAL);
    }
}
