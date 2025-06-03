package com.brasfi.platforma.model;

public enum EixoTematico {
    LIDERANCA("Liderança"),
    FINANCAS("Finanças"),
    EMPREENDEDORISMO("Empreendedorismo"),
    SAUDE("Saúde");

    private final String displayValue; // Campo para o valor de exibição

    // Construtor
    EixoTematico(String displayValue) {
        this.displayValue = displayValue;
    }

    // Getter para o valor de exibição
    public String getDisplayValue() {
        return displayValue;
    }
}