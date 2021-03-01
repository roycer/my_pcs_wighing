/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.service.server;

import com.maypi.model.Weight;
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
public class WeightServerService {
    
    private String endpoint = "/weights";
    private String address = "10.10.22.8:8807/api/v1";
    private String code = "";
    private String token = "";

    ServerService serverService;
    
    public WeightServerService(){
        this.serverService = new ServerService();
    }
    
    public WeightServerService(String address, String endpoint, String token){
        
        this.serverService = new ServerService();
        
        if(address.length()>0 && endpoint.length()>0 && endpoint.length()>0){
            this.address = address;
            this.endpoint = endpoint;
            this.token = token;
        }
    }

    private boolean sendWeight(Weight weightobject){

         try {
            URL url = new URL("http://"+this.address+this.endpoint);
            Map<String,Object> params = new LinkedHashMap<>();
            params.put("code", this.code);
            params.put("nro", weightobject.getNro());
            params.put("weight", weightobject.getWeight());
            params.put("unit", weightobject.getUnit());
            params.put("observation", weightobject.getObservation());
            
            String response = serverService.requestPost(url, params, this.token);
            
         } catch (Exception e) {
         }
         
         return true;
     }
}
