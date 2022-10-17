package io.d2a.klausur1;

public record Quote(String text, String person, String role, String source, QuoteType type) {

    public String getCitation() {
        return String.format("%s (%s, %s)", this.person, this.role, this.source);
    }

}
