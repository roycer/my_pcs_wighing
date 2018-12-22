/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.balance;

import java.io.Serializable;

/**
 *
 * @author Roycer
 */
public class Weight implements Serializable {
    
    private static final long serialVersionUID = 1L;
    int nro;
    String weight;
    String unit;
    String observation;

    public Weight() {
    }

    public Weight(int nro, String weight, String unit, String observation) {
        this.nro = nro;
        this.weight = weight;
        this.unit = unit;
        this.observation = observation;
    }

    public int getNro() {
        return nro;
    }

    public void setNro(int nro) {
        this.nro = nro;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
    
}
