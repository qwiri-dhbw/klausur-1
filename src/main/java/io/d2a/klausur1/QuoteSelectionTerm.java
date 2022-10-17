package io.d2a.klausur1;

import io.d2a.ahpe.AhpeDialog;
import io.d2a.ahpe.AhpeFile;
import io.d2a.ahpe.AhpeMisc;
import io.d2a.ahpe.AhpeThread;
import io.d2a.swag.layouts.SBorder;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QuoteSelectionTerm extends JFrame {

    /**
     * top label: shows the current client
     */
    private final JLabel currentPlayerLabel = new JLabel("Pick a quote: ", SwingConstants.CENTER);

    /**
     * bottom label: shows the current available point count
     */
    private final JLabel availablePointsLabel = new JLabel("Points: 10", SwingConstants.CENTER);

    /**
     * contains the points available for a player to claim
     */
    private final AtomicInteger availablePoints = new AtomicInteger();

    /**
     * contains all buttons in the grid
     */
    private final List<QuoteButton> buttons = new ArrayList<>();

    /**
     * contains all available clients which registered using the
     * {@link QuoteSelectionTerm#register(FakeTalkClient)} method.
     */
    private final List<FakeTalkClient> clients = new ArrayList<>();

    /**
     * reference to the current player
     */
    private FakeTalkClient currentClient;

    /**
     * indicates whether the round started
     */
    private boolean started = false;

    /**
     * indicates if the current client has time to guess
     */
    private boolean guessing = false;

    public QuoteSelectionTerm(final List<Quote> quotes, final int rows, final int cols)
            throws HeadlessException, FakeNewsException {

        // check if there are enough quote to fill the grid
        if (rows * cols > quotes.size()) {
            throw new FakeNewsException("Provided quote catalog does not contain enough (hot|bull)shit!");
        }

        // select random {rows*cols} quotes
        Collections.shuffle(quotes);

        // update local components
        this.currentPlayerLabel.setFont(this.currentPlayerLabel.getFont().deriveFont(Font.BOLD));
        this.availablePointsLabel.setFont(this.availablePointsLabel.getFont().deriveFont(Font.BOLD));

        // initialize frame
        this.setTitle("FakeTalk");

        // create grid component with quotes
        final JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(rows, cols));

        // create buttons
        for (Quote quote : quotes.subList(0, rows * cols)) {
            final QuoteButton button = new QuoteButton(quote);
            button.setType(QuoteType.UNKNOWN);
            button.addActionListener(e -> this.handleQuoteClick(button));

            this.buttons.add(button);
            grid.add(button);
        }

        // layout
        this.add(SBorder.create()
                .top(this.currentPlayerLabel)
                .center(grid)
                .bottom(this.availablePointsLabel)
        );
        AhpeMisc.visible(this, 10, 10);

        // decrement points every 2 second
        AhpeThread.every(2, TimeUnit.SECONDS, () -> {
            if (!this.guessing) {
                return;
            }
            if (this.getAvailablePoints() > 1) {
                this.setAvailablePoints(this.getAvailablePoints() - 1);
            }
        });
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
        this.guessing = false;

        // output dialog and write to file
        final String message = String.format("This quote is %s!%nFrom: %s", quote.type().getLabel(), quote.getCitation());

        // Hinweis: zum Testen habe ich den Typ des Dialogs daran angepasst,
        // ob die Antwort korrekt war.
        // Falls nicht gewuenscht,einfach `enhancedDialog` auf `false` setzen :)
        final boolean enhancedDialog = true;

        if (!enhancedDialog) {
            AhpeDialog.info("Meldung", message);
        } else if (quote.type().equals(selectedType)) {
            JOptionPane.showMessageDialog(
                    null, message,
                    "Richtig!",
                    JOptionPane.INFORMATION_MESSAGE,
                    quote.type().getIcon()
            );
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    message,
                    "Falsch!",
                    JOptionPane.WARNING_MESSAGE,
                    quote.type().getIcon()
            );
        }

        if (quote.type().equals(selectedType)) {
            // correct answer
            this.currentClient.addPoints(this.getAvailablePoints());
        } else {
            // incorrect answer
            this.currentClient.addPoints(-this.getAvailablePoints());
        }

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

        final String message = "Game finished. Score: " +
                this.clients.stream()
                        .map(FakeTalkClient::toString)
                        .collect(Collectors.joining(", "));

        // show game end dialog
        AhpeDialog.info("Game finished!", message);

        // write game end message to file
        try {
            AhpeFile.appendLine(new File("fake-score.txt"), message);
        } catch (final Exception ignored) {
        }
    }

    private void nextPlayer() {
        // rotate player
        if (this.currentClient == null) {
            this.currentClient = this.clients.get(0);
        } else {
            this.currentClient = this.clients.get(
                    (this.clients.indexOf(this.currentClient) + 1) % this.clients.size()
            );
        }
        // update label
        this.currentPlayerLabel.setText("Pick a quote: " + this.currentClient.getPlayerName());
    }

    private int getAvailablePoints() {
        return this.availablePoints.get();
    }

    private void setAvailablePoints(final int newAvailablePoints) {
        if (newAvailablePoints != this.availablePoints.get()) {
            this.availablePoints.set(newAvailablePoints);
        }
        this.availablePointsLabel.setText("Punkte: " + newAvailablePoints);
    }

    private void resetAvailablePoints() {
        this.setAvailablePoints(10);
    }

    private void handleQuoteClick(final QuoteButton button) {
        this.guessing = true;

        // reset available points
        this.resetAvailablePoints();

        // disable all buttons
        this.buttons.forEach(b -> b.setEnabled(false));
        // set quote for client
        this.currentClient.setQuote(button.getQuote());
    }

}