package com.hugos.BanKING2.enums;

public enum Role{

    GUEST(0),
    USER(1),
    ADMIN(2),
    CEO(3);

    private final int levelOfClearance;

    Role(int levelOfClearance) {
        this.levelOfClearance = levelOfClearance;
    }

    public int getLevelOfClearance() {
        return levelOfClearance;
    }
}