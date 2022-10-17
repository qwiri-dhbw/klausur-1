package io.d2a.klausur1;

import io.d2a.ahpe.AhpeFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

public class FakeTalk {

    public static void main(String[] args) {
        List<Quote> quotes = FakeTalk.loadQuotes("corona.csv");
        try {
            QuoteSelectionTerm selectionTerm = new QuoteSelectionTerm(quotes, 3, 4);

            QuoteTerm t1 = new QuoteTerm("Muenchhausen", selectionTerm);
            QuoteTerm t2 = new QuoteTerm("Pinocchio", selectionTerm);
            selectionTerm.register(t1);
            selectionTerm.register(t2);

            selectionTerm.start();
        } catch (FakeNewsException e) {
            e.printStackTrace();
        }
    }

    private static List<Quote> loadQuotes(String fileName) {
        List<Quote> quotes = new LinkedList<>();

        for (final String line : AhpeFile.readLines(new File(fileName))) {
            quotes.add(FakeTalk.parseQuote(line));
        }

        return quotes;
    }

    private static Quote parseQuote(String s) {
        if (s != null) {
            String[] parts = s.trim().split(";");
            if (parts.length == 6 || parts.length == 5) {
                return new Quote(parts[1], parts[2], parts[3], parts[4], Boolean.parseBoolean(parts[0]) ? QuoteType.HOT_SHIT : QuoteType.BULLSHIT);
            }
        }
        return null;
    }

}
