package com.game.belote.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Card implements Comparable<Card> {
    private Suit suit;
    private Face face;
    private boolean visible;
    private int value;
    private int rank;
    private int sequence;

    public Card() {
    }

    public Card(Suit suit, Face face) {
        this(suit, face, false);
    }

    public Card(Suit suit, Face face, boolean visible) {
        this.suit = suit;
        this.face = face;
        this.visible = visible;
        this.rank = generateRank();
        this.value = generateValue();
        this.sequence = generateSequence();
    }

    private int generateSequence() {
        switch (face) {
            case SEDAM: return 1;
            case OSAM: return 2;
            case DEVET: return 3;
            case DESET: return 4;
            case DEČKO: return 5;
            case DAMA: return 6;
            case KRALJ: return 7;
            case AS: return 8;
            default: return 0;
        }
    }

    private int generateRank() {
        switch (face) {
            case SEDAM: return 1;
            case OSAM: return 2;
            case DEVET: return 3;
            case DEČKO: return 4;
            case DAMA: return 5;
            case KRALJ: return 6;
            case DESET: return 7;
            case AS: return 8;
            default: return 0;
        }
    }

    private int generateValue() {
        switch (face) {
            case SEDAM, OSAM, DEVET: return 0;
            case DEČKO: return 2;
            case DAMA: return 3;
            case KRALJ: return 4;
            case DESET: return 10;
            case AS: return 11;
            default: return 0;
        }
    }

    @Override
    public String toString() {
        return "%s %s(r:%s)(v:%s)(s:%s)".formatted(suit.name(), face.name(), getRank(), getValue(), getSequence());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        if (suit != card.suit) return false;
        return face == card.face;
    }

    @Override
    public int compareTo(Card c) {
        return Integer.compare(getRank(), c.getRank());
    }
}
