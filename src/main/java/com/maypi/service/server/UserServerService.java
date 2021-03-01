/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.service.server;

import com.maypi.service.*;
import com.maypi.service.server.ServerService;
import com.maypi.view.JFrameLogin;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.json.JSONObject;

/**
 *
 * @author mysac
 */
public class UserServerService {
    
    String endpoint = "/login";
    String address = "10.10.22.8:8807/api/v1";
    String code = "";
    ServerService serverService;
    
    public UserServerService(){
        this.serverService = new ServerService();
    }
    
    public UserServerService(String address, String endpoint){
        
        this.serverService = new ServerService();
        
        if(address.length()>0 && endpoint.length()>0){
            this.address = address;
            this.endpoint = endpoint;
        }
    }

    public Boolean login(String username, String password){
               
        try {
            
            URL url = new URL("http://"+this.address+this.endpoint);
            Map<String,Object> params = new LinkedHashMap<>();
            params.put("username", username);
            params.put("password", password);

            String response = serverService.requestPost(url, params, "");
            
            JSONObject jSONObject = new JSONObject(response.toString());
            
            String token = jSONObject.getString("token");
            
        } catch (Exception e) {
       
            Logger.getLogger(JFrameLogin.class.getName()).log(Level.SEVERE, null, e);

        }
        
        return true;
    }
    
}
