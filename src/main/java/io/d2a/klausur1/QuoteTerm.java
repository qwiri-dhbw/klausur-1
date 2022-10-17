package io.d2a.klausur1;

import io.d2a.ahpe.AhpeMisc;
import io.d2a.swag.components.SButton;
import io.d2a.swag.layouts.SBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class QuoteTerm extends JFrame implements FakeTalkClient {

    private final String name;
    private final QuoteSelectionTerm term;

    private Quote quote;
    private int points;

    private final QuoteDisplay display;

    private final JButton shitButton = new SButton()
            .click(handle(QuoteType.BULLSHIT))
            .icon(FakeTalkIcons.ICON_BULLSHIT)
            .disable()
            .build();

    private final JButton hotButton = new SButton()
            .click(handle(QuoteType.HOT_SHIT))
            .icon(FakeTalkIcons.ICON_HOT_SHIT)
            .disable()
            .build();

    private final JLabel pointsLabel = new JLabel("0 Points");

    public QuoteTerm(final String name, final QuoteSelectionTerm term) throws HeadlessException {
        this.name = name;
        this.term = term;

        this.display = new QuoteDisplay();

        this.setTitle(name);
        this.add(SBorder.create()
                .top(this.display)
                .center(SBorder.create()
                        .left(this.hotButton)
                        .right(this.shitButton)
                )
                .bottom(this.pointsLabel));
        AhpeMisc.visible(this);
    }

    @Override
    public String getPlayerName() {
        return this.name;
    }

    @Override
    public void setQuote(Quote quote) {
        this.quote = quote;

        this.display.setText(quote.text());

        // enable buttons
        this.shitButton.setEnabled(true);
        this.hotButton.setEnabled(true);
    }

    @Override
    public void addPoints(int points) {
        this.points += points;
        this.pointsLabel.setText(this.points + " Points");
    }

    @Override
    public int getPoints() {
        return this.points;
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", this.name, this.points);
    }

    private ActionListener handle(final QuoteType type) {
        return event -> {
            // disable buttons
            this.shitButton.setEnabled(false);
            this.hotButton.setEnabled(false);
            // clear quote
            this.display.setText("");
            // send answer to term
            this.term.answerSelected(this, this.quote, type);
        };
    }


}
