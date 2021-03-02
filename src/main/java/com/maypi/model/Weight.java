/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Roycer
 */
@Entity
@Table(name="weights")
public class Weight implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Column
    private int nro;
    
    @Column
    private String weight;
    
    @Column
    private String unit;
    
    @Column
    private String observation;
    
    @Column
    private String code;

    @Column
    private Date created_at;
    
    @Column
    private Boolean sync;
  
    public Weight() {
    }

    public Weight(int nro, String weight, String unit, String observation) {

        this.nro = nro;
        this.weight = weight;
        this.unit = unit;
        this.observation = observation;
        this.code = "";
        this.created_at = new Date();
    }
    
    public Weight(int nro, String weight, String unit, String observation, String code) {
        this.nro = nro;
        this.weight = weight;
        this.unit = unit;
        this.observation = observation;
        this.code = code;
        this.created_at = new Date();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public int getNro() {
        return this.nro;
    }

    public void setNro(int nro) {
        this.nro = nro;
    }

    public String getWeight() {
        return this.weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getObservation() {
        return this.observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
    
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    public Boolean getSync() {
        return sync;
    }

    public void setSync(Boolean sync) {
        this.sync = sync;
    }
}
