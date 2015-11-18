package com.jonathanbravo.taxicosto.configuracion;

/**
 * Created by Johnny on 17/02/2015.
 */
public class Tarifa {

    private Float arranque_diurno = 0.35f;
    private Float arranque_nocturno = 0.40f;

    private Float costo_km_diurno = 0.26f;
    private Float costo_km_nocturno = 0.30f;

    private Float costo_minuto_diurno = 0.06f;

    private Float carerra_minima_diurno = 1.0f;
    private Float carerra_minima_nocturno = 1.10f;

    private String horario_nocturno = "22:00-05:00";

    public Float getArranque_diurno() {
        return arranque_diurno;
    }

    public Float getArranque_nocturno() {
        return arranque_nocturno;
    }

    public Float getCosto_km_diurno() {
        return costo_km_diurno;
    }

    public Float getCosto_km_nocturno() {
        return costo_km_nocturno;
    }

    public Float getCosto_minuto_diurno() {
        return costo_minuto_diurno;
    }

    public Float getCarerra_minima_diurno() {
        return carerra_minima_diurno;
    }

    public Float getCarerra_minima_nocturno() {
        return carerra_minima_nocturno;
    }

    public String getHorario_nocturno() {
        return horario_nocturno;
    }
}
