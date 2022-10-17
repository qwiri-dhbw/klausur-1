package io.d2a.klausur1;

import io.d2a.ahpe.AhpeDialog;
import io.d2a.ahpe.AhpeMisc;
import io.d2a.swag.layouts.SBorder;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class QuoteSelectionTerm extends JFrame {

    // components
    private final JLabel currentPlayerLabel = new JLabel("Pick a quote: ", SwingConstants.CENTER);
    private final JLabel pointsLabel = new JLabel("Points: 10", SwingConstants.CENTER);

    // attributes
    private final List<Quote> quotes;
    private final List<QuoteButton> buttons;

    private final List<FakeTalkClient> clients;
    private FakeTalkClient currentClient;

    // controls
    private int availablePoints;
    private boolean started = false;

    public QuoteSelectionTerm(final List<Quote> quotes, final int rows, final int cols)
            throws HeadlessException, FakeNewsException {

        if (rows * cols < quotes.size()) {
            throw new FakeNewsException("Provided quote catalog does not contain enough (hot|bull)shit!");
        }

        // select random quotes
        Collections.shuffle(quotes);
        this.quotes = quotes.subList(0, rows * cols);

        this.buttons = new ArrayList<>();
        this.clients = new ArrayList<>();

        // update local components
        this.currentPlayerLabel.setFont(this.currentPlayerLabel.getFont().deriveFont(Font.BOLD));
        this.pointsLabel.setFont(this.pointsLabel.getFont().deriveFont(Font.BOLD));

        // initialize frame
        this.setTitle("FakeTalk");

        // initialize available points ( + label )
        this.resetAvailablePoints();

        // grid
        final JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(rows, cols));

        // create buttons
        for (Quote quote : this.quotes) {
            final QuoteButton button = new QuoteButton(quote);
            this.buttons.add(button);
            grid.add(button);
        }

        // layout
        this.add(SBorder.create()
                .top(this.currentPlayerLabel)
                .center(grid)
                .bottom(this.pointsLabel)
        );
        AhpeMisc.visible(this, 10, 10);
    }

    public void register(final FakeTalkClient client) {
        this.clients.add(client);

        // set initial current client
        if (this.currentClient == null) {
            this.currentClient = client;
        }
    }

    public void start() throws FakeNewsException {
        // ignore if game already started
        if (this.started) {
            return;
        }
        // check required player size
        if (this.clients.size() < 2) {
            throw new FakeNewsException("at least 2 players required to start");
        }
        // mark game as started
        this.started = true;
        // rotate player
        this.nextPlayer();
    }

    public void answerSelected(final FakeTalkClient client, final Quote quote, final QuoteType selectedType) {
        // show "real" source
        AhpeDialog.info("Meldung", String.format("This quote is %s!%nFrom: %s",
                quote.type().getLabel(), quote.getCitation()));

        if (quote.type().equals(selectedType)) {
            // correct answer
            this.currentClient.addPoints(this.getAvailablePoints());
        } else {
            // incorrect answer
            this.currentClient.addPoints(-this.getAvailablePoints());
        }

        this.resetAvailablePoints();

        // update type for corresponding type
        this.buttons.stream()
                .filter(qb -> qb.getQuote().equals(quote))
                .findFirst()
                .orElseThrow()
                .setType(quote.type());

        // enable unknown buttons
        this.buttons.stream()
                .filter(QuoteButton::isUnknown)
                .forEach(b -> b.setEnabled(true));

        // check if there are any unknown buttons
        if (this.buttons.stream().noneMatch(QuoteButton::isUnknown)) {
            this.endGame();
            return;
        }

        // rotate to next player
        this.nextPlayer();
    }

    private void endGame() {
        this.started = false;

        AhpeDialog.info("Game finished!", "Game finished. Score: " +
                this.clients.stream()
                        .map(FakeTalkClient::toString)
                        .collect(Collectors.joining(", "))
        );
    }

    private void nextPlayer() {
        // rotate player
        if (this.currentClient == null) {
            this.currentClient = this.clients.get(0);
        } else {
            this.currentClient = this.clients.get(
                    this.clients.indexOf(this.currentClient) + 1 % this.clients.size()
            );
        }
        // update label
        this.currentPlayerLabel.setText("Pick a quote: " + this.currentClient.getPlayerName());
    }

    private int getAvailablePoints() {
        return this.availablePoints;
    }

    private void setAvailablePoints(final int newAvailablePoints) {
        this.availablePoints = newAvailablePoints;
        this.pointsLabel.setText("Punkte: " + newAvailablePoints);
    }

    private void resetAvailablePoints() {
        this.setAvailablePoints(10);
    }

}