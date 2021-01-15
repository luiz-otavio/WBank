package com.rededark.wbank.booster.type;

public enum BoosterType {
    SUM("Somar"),
    MULTIPLY("Multiplicar"),
    PERCENTAGE("Porcentagem");

    private final String name;

    BoosterType (String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static BoosterType from(String name) {
        for (BoosterType boosterType : values()) {
            if(boosterType.getName().equalsIgnoreCase(name)) return boosterType;
        }; return null;
    }
}
