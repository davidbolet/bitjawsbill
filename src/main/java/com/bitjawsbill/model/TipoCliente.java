package com.bitjawsbill.model;

public enum TipoCliente {
    FISICA(0),
    JURIDICA(1),
    ORGANIZACION(2);

    private final int value;

    TipoCliente(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
