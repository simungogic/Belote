package com.game.belote.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Card implements Comparable<Card> {
    private Suit suit;
    private Face face;
    private boolean visible;

    public Card(Suit suit, Face face) {
        this.suit = suit;
        this.face = face;
    }

    public void setFaceValue(int rank) {
        this.face.setValue(rank);
    }

    public void setFaceRank(int rank) {
        this.face.setRank(rank);
    }

    @Override
    public String toString() {
        return "%s %s".formatted(suit.name(), face.name());
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
        return Integer.compare(this.getFace().getRank(), c.getFace().getRank());
    }
}
