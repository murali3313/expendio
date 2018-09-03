package com.thriwin.expendio;

enum TransactionType {
    CASH("Cash Transaction"),
    DIGITAL("Digital Transaction");

    private final String value;

    TransactionType(String val) {
        this.value = val;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
