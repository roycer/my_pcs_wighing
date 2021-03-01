/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.service.response;

import java.io.Serializable;

/**
 *
 * @author rcordova
 */
public class WeightResponse implements Response, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int nro;
    private String weight;
    private String unit;
    private String observation;
    private String code;
    private boolean status;

    public WeightResponse(int nro, String weight, String unit, String observation) {
        this.nro = nro;
        this.weight = weight;
        this.unit = unit;
        this.observation = observation;
        this.code = "";
        this.status = true;
    }
    
    public WeightResponse(int nro, String weight, String unit, String observation, String code) {
        this.nro = nro;
        this.weight = weight;
        this.unit = unit;
        this.observation = observation;
        this.code = code;
        this.status = true;
    }

    public WeightResponse(int nro, WeightResponse weight, String unit, String observation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    
    @Override
    public Boolean getStatus() {
        
        if(this.weight.length() > 0){
            return this.status;
        }
        
        return false;
    }
}
