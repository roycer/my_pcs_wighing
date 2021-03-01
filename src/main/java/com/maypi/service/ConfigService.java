/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.service;

import com.fazecast.jSerialComm.SerialPort;
import com.maypi.service.response.ConfigResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mysac
 */
public class ConfigService {
    
    private String path_temp = System.getProperty("java.io.tmpdir");
    private String tempConfig = "rc_config.bin";
    private ConfigResponse configResponse;
    
    public ConfigService(){
        
        this.configResponse = new ConfigResponse();
        this.configResponse.setPort("");
        this.configResponse.setBaudRate(9600);
        this.configResponse.setNumDataBits(7);
        this.configResponse.setNumStopBits(1);
        this.configResponse.setParity(SerialPort.ODD_PARITY);
        this.configResponse.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER);
        
    }
    
    public ConfigResponse setConfig(String port){
  
        this.configResponse.setPort(port);

        return this.configResponse;
        
    }
    
    public ConfigResponse setConfig(ConfigResponse configResponse){
  
        this.configResponse = configResponse;

        return this.configResponse;
        
    }
    
      
    public ConfigResponse getConfig(){

        return configResponse;
    }
    
    public Boolean setConfigInFileBin(ConfigResponse configResponse){
        
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(this.path_temp+this.tempConfig));
            objectOutputStream.writeObject(configResponse);
            objectOutputStream.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(ConfigService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(objectOutputStream != null){
                    objectOutputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ConfigService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return false;
    }
    
    public ConfigResponse getConfigFromFileBin(){
        
        ConfigResponse configResponse = null;
        
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(this.path_temp+this.tempConfig));
            configResponse = (ConfigResponse) objectInputStream.readObject();
                    
            return configResponse;
        } catch (IOException ex) {
            Logger.getLogger(ConfigService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConfigService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return configResponse;
        
    }
    
}
