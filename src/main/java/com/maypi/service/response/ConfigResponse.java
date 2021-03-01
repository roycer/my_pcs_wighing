/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.service.response;

/**
 *
 * @author rcordova
 */
public class ConfigResponse implements Response {
    
    private String port;
    private int baudRate;
    private int numStopBits;
    private int numDataBits;
    private int parity;
    private int comPortTimeouts;
    private boolean status;
    
     public ConfigResponse() {
         this.port = "";
         this.status = true;
     }

     public String getPort() {
         return port;
     }

     public void setPort(String port) {
         this.port = port;
     }

     public int getBaudRate() {
         return baudRate;
     }

     public void setBaudRate(int baudRate) {
         this.baudRate = baudRate;
     }

     public int getNumStopBits() {
         return numStopBits;
     }

     public void setNumStopBits(int numStopBits) {
         this.numStopBits = numStopBits;
     }

     public int getParity() {
         return parity;
     }

     public void setParity(int parity) {
         this.parity = parity;
     }

     public int getComPortTimeouts() {
         return comPortTimeouts;
     }

     public void setComPortTimeouts(int comPortTimeouts) {
         this.comPortTimeouts = comPortTimeouts;
     }

    public int getNumDataBits() {
        return numDataBits;
    }

    public void setNumDataBits(int numDataBits) {
        this.numDataBits = numDataBits;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    
    @Override
    public Boolean getStatus() {
        
        if(this.port.length() > 0){
            return this.status;
        }
        
        return false;
    }

}
