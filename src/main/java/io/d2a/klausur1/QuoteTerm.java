package io.d2a.klausur1;

import io.d2a.ahpe.AhpeMisc;
import io.d2a.swag.components.SButton;
import io.d2a.swag.layouts.SBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class QuoteTerm extends JFrame implements FakeTalkClient {

    /**
     * contains the players name
     */
    private final String name;

    /**
     * contains a reference to {@link QuoteSelectionTerm}
     */
    private final QuoteSelectionTerm term;

    /**
     * contains the current quote
     */
    private Quote quote;

    /**
     * contains the players' points
     */
    private int points;

    /**
     * quote display component.
     */
    private final QuoteDisplay display;

    /**
     * points label
     */
    private final JLabel pointsLabel = new JLabel("0 Points", SwingConstants.CENTER);

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

    public QuoteTerm(final String name, final QuoteSelectionTerm term) throws HeadlessException {
        this.name = name;
        this.term = term;

        this.display = new QuoteDisplay();

        // update title to match player name
        this.setTitle(name);

        // make points bold
        this.pointsLabel.setFont(this.pointsLabel.getFont().deriveFont(Font.BOLD));

        // layout
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
