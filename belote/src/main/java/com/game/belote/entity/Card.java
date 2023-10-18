package com.game.belote.entity;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Card {
    private Suit color;
    private Face face;

    public Card(Suit color, Face face) {
        this.color = color;
        this.face = face;
    }

    public void setFaceRank(int rank) {
        this.face.setRank(rank);
    }
}
