package com.game.belote.entity;

import lombok.ToString;

@ToString
public enum Face {
    SEDAM(1, 0),
    OSAM(2, 0),
    DEVET(3, 0),
    DEČKO(4, 2),
    DAMA(5, 3),
    KRALJ(6, 4),
    DESET(7, 10),
    AS(8, 11);

    private int value;
    private int rank;

    Face(int rank, int value) {
        this.rank = rank;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int getRank() {
        return rank;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
