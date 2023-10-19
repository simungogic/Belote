package com.game.belote.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Card {
    private Suit color;
    private Face face;
    private boolean visibility;

    public Card(Suit color, Face face) {
        this.color = color;
        this.face = face;
    }

    public void setFaceRank(int rank) {
        this.face.setRank(rank);
    }
}
