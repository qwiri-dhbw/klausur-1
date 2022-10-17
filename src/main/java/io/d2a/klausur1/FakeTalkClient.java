package io.d2a.klausur1;

public interface FakeTalkClient {

    String getPlayerName();

    void setQuote(final Quote quote);

    void addPoints(final int points);

    /**
     * Hinweis: Diese Methode wird aktuell nicht benötigt,
     * Da der Punktestand der einzelnen Spieler durch die
     * {@link QuoteTerm#toString()}-Mehode erhalten wird.
     *
     * Alternativ beim Gewinnen:
     * <pre>
     *     final StringBuilder builder = new StringBuilder();
     *     for (final FakeTalkClient client : this.clients) {
     *          if (builder.length() > 0) {
     *              builder.append(", ");
     *          }
     *          builder.append(client.getPlayerName())
     *              .append(" (")
     *              .append(client.getPoints())
     *              .append(")");
     *     }
     * </pre>
     *
     * @return
     */
    int getPoints();

}
