package theatricalplays;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {

    private Map<String, Play> plays;

    public String print(Invoice invoice, Map<String, Play> plays) {
        this.plays = plays;
        var totalAmount = 0;
        var result = String.format("Statement for %s%n", invoice.customer);

        for (var perf : invoice.performances) {
            // print line for this order
            result += String.format("  %s: %s (%s seats)%n", playFor(perf).name, usd(amountFor(perf)), perf.audience);
            totalAmount += amountFor(perf);
        }

        result += String.format("Amount owed is %s%n", usd(totalAmount));
        result += String.format("You earned %s credits%n", totalVolumeCredits(invoice.performances));
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
        var volumeCredits = 0;
        for (var perf : performances) {
            volumeCredits += volumeCreditsFor(perf);
        }
        return volumeCredits;
    }
}
