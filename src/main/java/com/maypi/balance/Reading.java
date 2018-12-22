package com.maypi.balance;

import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;
import java.util.Scanner;
import javax.swing.JFrame;

/**
 *
 * @author Roycer
 */
public class Reading extends Thread{
    
    String port;
    SerialPort serialport;
    JFrame frame;
    Boolean closereading;
    
    public Reading(String port, JFrame frame) {
        System.out.println(port);
        this.closereading = false;
        this.port = port;
        this.frame = frame;
        this.serialport = SerialPort.getCommPort(port);  
    }
    
    @Override
    public void run(){    
        try {
            
            this.serialport.setBaudRate(9600);
            this.serialport.setNumDataBits(7);
            this.serialport.setNumStopBits(1);
            this.serialport.setParity​(SerialPort.ODD_PARITY);
            this.serialport.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
            this.serialport.openPort();
            System.out.println("open port");
            try {
                InputStream inputstream = serialport.getInputStream();
                Scanner scanner = new Scanner(inputstream); 
                scanner.nextLine();
                System.out.println("data");
                while(scanner.hasNextLine()) {    
                    if(closereading) break;
                    
                    String data = scanner.nextLine();
                    System.out.println(data);
                    response(data);
                    
                    
                }
                System.out.println("fin while");
                scanner.close();
                inputstream.close();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }  
            this.closePort();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }

    public void response(String data){
        
        switch(frame.getClass().getName()){
            case "com.maypi.balance.balance": 
                ((balance)frame).response(data);
                break;          
        }
    }
    
    public void closePort(){
        this.closereading = true;
        this.serialport.closePort();
    }
    
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
  
}
