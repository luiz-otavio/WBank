package com.rededark.wbank.account.operation.type;

public enum OperationType {
    COLLECT("Receber"),
    UPDATE("Alteração"),
    INCREMENT("Aumento"),
    DECREMENT("Prejuizo"),
    PAYOFF("Pagamento"),
    UPDATE_LIMIT("Atualização de limite"),
    INCREMENT_LIMIT("Aumento de limite"),
    DECREMENT_LIMIT("Prejuizo de limite");

    private final String prefix;

    OperationType (String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
