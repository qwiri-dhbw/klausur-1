package io.d2a.klausur1;

import javax.swing.*;

public class QuoteButton extends JButton {

    private final Quote quote;

    private QuoteType type;

    public QuoteButton(Quote quote) {
        this.quote = quote;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setType(QuoteType type) {
        this.type = type;

        this.setBackground(type.getColor());
        this.setIcon(type.getIcon());
    }

    public boolean isUnknown() {
        return this.type == QuoteType.UNKNOWN;
    }

}
