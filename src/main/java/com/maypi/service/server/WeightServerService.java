/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.service.server;

import com.maypi.model.Weight;
import com.maypi.repository.WeightRepositoryImpl;
import com.maypi.service.*;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author mysac
 */
public class WeightServerService extends Thread {
    
    private String endpoint = "/weights";
    private String address = "10.10.22.8:8807/api/v1";
    private String token = "";

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyDatabase");
    EntityManager em;
    ParamService param = new ParamService();
    
    ServerService serverService;
    
    public WeightServerService(){
        this.serverService = new ServerService();
        
        String temp_endpoint = this.param.getParam("endpoint");
        String temp_address = this.param.getParam("address");
        
        if(temp_endpoint != null && temp_endpoint.length() > 0){
            this.endpoint = temp_endpoint;
        }else{
            this.param.setParam("endpoint", "/weights");
            
        }
        
        if(temp_address != null && temp_address.length() > 0){
            this.address = temp_address;
        }else{
            this.param.setParam("address", "10.10.22.8:8807/api/v1");
        }
        
    }
    
    public WeightServerService(String token){
        
        this.serverService = new ServerService();
        this.token = token;

    }
    
    @Override
    public void run(){
        em = emf.createEntityManager();
        
        WeightRepositoryImpl wi = new WeightRepositoryImpl(em);
        List<Weight> weights = wi.getWeightToSync();
        
        try {
            for(Weight weight: weights){
                if(this.sendWeight(weight)){
                    weight.setSync(Boolean.TRUE);
                    em.getTransaction().begin();
                    wi.saveWeight(weight);
                    em.getTransaction().commit();
                }
            }
        }
        catch (Exception e) {
            Logger.getLogger(WeightServerService.class.getName()).log(Level.SEVERE, null, e);
        }  
        

    }
    
    private boolean sendWeight(Weight weightobject){

        try {
            URL url = new URL("http://"+this.address+this.endpoint);
            Map<String,Object> params = new LinkedHashMap<>();
            params.put("code", weightobject.getCode());
            params.put("nro", weightobject.getNro());
            params.put("weight", weightobject.getWeight());
            params.put("unit", weightobject.getUnit());
            params.put("observation", weightobject.getObservation());

            String response = serverService.requestPost(url, params, this.token);

            if(response.indexOf("success")>0 || response.indexOf("fail")>0){
                return true;
            }

        } catch (Exception e) {
             Logger.getLogger(WeightServerService.class.getName()).log(Level.SEVERE, null, e);
        }
         
        return false;
    }
}
