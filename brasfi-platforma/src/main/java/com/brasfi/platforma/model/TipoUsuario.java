package com.brasfi.platforma.model;

public enum TipoUsuario {
    ADMINISTRADOR("Administrador"),
    ESTUDANTE("Estudante");

    private final String displayValue;

    TipoUsuario(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}