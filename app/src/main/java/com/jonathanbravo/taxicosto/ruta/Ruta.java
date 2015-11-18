package com.jonathanbravo.taxicosto.ruta;

public class Ruta {
    private double desde_latitud;
    private double desde_longitud;

    private double hasta_latitud;
    private double hasta_longitud;

    public double getDesde_latitud() {
        return desde_latitud;
    }

    public double getDesde_longitud() {
        return desde_longitud;
    }

    public double getHasta_latitud() {
        return hasta_latitud;
    }

    public double getHasta_longitud() {
        return hasta_longitud;
    }

    public void setDesde_latitud(double desde_latitud) {
        this.desde_latitud = desde_latitud;
    }

    public void setDesde_longitud(double desde_longitud) {
        this.desde_longitud = desde_longitud;
    }

    public void setHasta_latitud(double hasta_latitud) {
        this.hasta_latitud = hasta_latitud;
    }

    public void setHasta_longitud(double hasta_longitud) {
        this.hasta_longitud = hasta_longitud;
    }
}
