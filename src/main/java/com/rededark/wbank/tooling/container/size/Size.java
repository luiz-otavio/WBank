package com.rededark.wbank.tooling.container.size;

public enum Size {
    ONE_LINE(9),
    TWO_LINES(18),
    THREE_LINES(27),
    FOUR_LINES(36),
    FIVE_LINES(45),
    SIX_LINES(54);

    private final int amount;

    Size(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
