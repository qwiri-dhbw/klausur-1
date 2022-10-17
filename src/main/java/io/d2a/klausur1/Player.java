package io.d2a.klausur1;

public class Player {

    private final String name;
    private int points;

    public Player(String name) {
        this.name = name;
        this.points = 0;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", this.name, this.points);
    }
}
