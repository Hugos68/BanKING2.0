package com.hugos.BanKING.enums;

public enum Role{
    USER(1),
    ADMIN(2);

    private final int levelOfClearance;

    Role(int levelOfClearance) {
        this.levelOfClearance = levelOfClearance;
    }

    public int getLevelOfClearance() {
        return levelOfClearance;
    }
}
