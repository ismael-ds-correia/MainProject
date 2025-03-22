package com.qmasters.fila_flex.util;

public enum PriorityCondition {
    PWD("PWD"),
    ELDERLY("ELDERLY"),
    PREGNANT_OR_LACTANT("PREGNANT_OR_LACTANT");

    private final String condition;

    PriorityCondition(String condition) {
        this.condition = condition;
    }
    
    public String getCondition() {
        return this.condition;
    }
}
