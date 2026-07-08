package theatricalplays;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {

    private Map<String, Play> plays;
    record StatementData(String customer, List<Performance> performances) {}

    public String print(Invoice invoice, Map<String, Play> plays) {

        return renderPlainText(new StatementData(invoice.customer, invoice.performances), plays);
    }
    
    private String renderPlainText(StatementData statementData, Map<String, Play> plays) {
        this.plays = plays;
        var result = String.format("Statement for %s%n", statementData.customer());

        for (var perf : statementData.performances()) {
            result += String.format("  %s: %s (%s seats)%n", playFor(perf).name, usd(amountFor(perf)), perf.audience);
        }

        result += String.format("Amount owed is %s%n", usd(totalAmount(statementData.performances())));
        result += String.format("You earned %s credits%n", totalVolumeCredits(statementData.performances()));
        return result;
    }

    private static String usd(int aNumber) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(aNumber / 100);
    }

    private int amountFor(Performance aPerformance) {
        int result = 0;

        switch (playFor(aPerformance).type) {
            case "tragedy":
                result = 40000;
                if (aPerformance.audience > 30) {
                    result += 1000 * (aPerformance.audience - 30);
                }
                break;
            case "comedy":
                result = 30000;
                if (aPerformance.audience > 20) {
                    result += 10000 + 500 * (aPerformance.audience - 20);
                }
                result += 300 * aPerformance.audience;
                break;
            default:
                throw new Error("unknown type: %s".formatted(playFor(aPerformance).type));
        }
        return result;
    }

    private Play playFor(Performance aPerformance) {
        return plays.get(aPerformance.playID);
    }

    private int volumeCreditsFor(Performance aPerformance) {
        int result = 0;
        result += Math.max(aPerformance.audience - 30, 0);
        if ("comedy".equals(playFor(aPerformance).type)) result += Math.floor(aPerformance.audience / 5);
        return result;
    }

    private int totalVolumeCredits(List<Performance> performances) {
        var result = 0;
        for (var perf : performances) {
            result += volumeCreditsFor(perf);
        }
        return result;
    }

    private int totalAmount(List<Performance> performances) {
        int result = 0;
        for(Performance p : performances) {
            result += amountFor(p);
        }
        return result;
    }
}
