package io.d2a.klausur1;

public interface FakeTalkClient {

    String getPlayerName();

    void setQuote(final Quote quote);

    void addPoints(final int points);

    int getPoints();

}
