package com.game.belote.entity;

public enum Face {
    SEDAM(0),
    OSAM(0),
    DEVET(0),
    DESET(10),
    DEČKO(2),
    DAMA(3),
    KRALJ(4),
    AS(11);

    private int rank;

    Face(int rank) {
        this.rank = rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
