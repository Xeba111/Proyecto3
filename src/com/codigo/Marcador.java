package com.codigo;

public class Marcador {

    private int aciertos;
    private int errores;
    private int numPartidas;

    public Marcador() {
        aciertos = 0;
        errores = 0;
        numPartidas = 0;
    }

    public void setAciertos(int aciertos) {
        this.aciertos = aciertos;
    }

    public void setErrores(int errores) {
        this.errores = errores;
    }

    public void setNumPartidas(int numPartidas) {
        this.numPartidas = numPartidas;
    }

    public int getAciertos() {
        return aciertos;
    }

    public int getErrores() {
        return errores;
    }

    public int getPartidas() {
        return numPartidas;
    }

    public void addAcierto() {
        aciertos++;
    }

    public void addError() {
        errores++;
    }

    public void addPartida() {
        numPartidas++;
    }
}

