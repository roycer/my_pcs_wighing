package com.maypi.util;

import com.fazecast.jSerialComm.SerialPort;
import com.maypi.service.response.ConfigResponse;
import com.maypi.view.JFrameBalance;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Roycer
 */
public class Reading extends Thread {
    
    private SerialPort serialport;
    private JFrame frame;
    private Boolean flagReading;
    private ConfigResponse configResponse;
    private InputStream inputstream;        
    private Scanner scanner;
    
    public Reading(ConfigResponse configResponse, JFrame frame) {
        
        this.flagReading = true;
        this.frame = frame;
         
        if(configResponse.getStatus()){
            this.configResponse = configResponse;
        }
        
    }
    
    @Override
    public void run(){
        
        this.serialport.setBaudRate(this.configResponse.getBaudRate());
        this.serialport.setNumDataBits(this.configResponse.getNumDataBits());
        this.serialport.setNumStopBits(this.configResponse.getNumStopBits());
        this.serialport.setParity​(this.configResponse.getParity());
        this.serialport.setComPortTimeouts(this.configResponse.getComPortTimeouts(), 0, 0);

        this.serialport.openPort();

        try {
            this.inputstream = serialport.getInputStream();
            this.scanner = new Scanner(this.inputstream); 
            this.scanner.nextLine();

            while(this.scanner.hasNextLine()) {          
                if(!this.flagReading) break;
                String data = this.scanner.nextLine();
                this.response(data);
            }

            if(this.flagReading) this.closePort();

        }
        catch (Exception e) {
            Logger.getLogger(Reading.class.getName()).log(Level.SEVERE, null, e);
        }  

    }

    private void response(String data){
        
        switch(frame.getClass().getName()){
            case "com.maypi.balance.JFrameBalance": 
                ((JFrameBalance)frame).onDataBalance(data);
                break;          
        }
    }
    
    public void closePort() throws IOException{
        this.flagReading = false;
        this.serialport.closePort();
        this.scanner.close();
        this.inputstream.close();
    }
    
    public ConfigResponse getConfigResponse() {
        return configResponse;
    }

    public void setConfigResponse(ConfigResponse configResponse) {
        this.configResponse = configResponse;
    }
  
}
