/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.service;

import com.maypi.model.Weight;
import com.maypi.repository.WeightRepositoryImpl;
import com.maypi.service.response.WeightResponse;
import com.maypi.view.JFrameBalance;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author mysac
 */
public class WeightService {
    
    private String path_temp = System.getProperty("java.io.tmpdir");
    public String tempRegs = "rc_regs.bin";
    public String tempConfig = "rc_config.bin";
    
    public WeightService(){
        
    }
    
    public Boolean saveWeight(WeightResponse weightResponse){
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyDatabase");
        EntityManager em = emf.createEntityManager();

        WeightRepositoryImpl wi = new WeightRepositoryImpl(em);
        
        Weight weight = new Weight(weightResponse.getNro(), weightResponse.getWeight(), weightResponse.getUnit(), weightResponse.getObservation());
        
        em.getTransaction().begin();
        
        if(weight!=null){
            weight = wi.saveWeight(weight);
        }
       
        em.getTransaction().commit();
        
        if(weight!=null){
            return true;
        }

        return false;
        
        
    }
    
    public ArrayList<WeightResponse> getWeightsFromFileBin(){
        
        ArrayList<WeightResponse> weights = new ArrayList<WeightResponse>();
        
        try {

            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(this.path_temp+tempRegs));
            weights = (ArrayList<WeightResponse>)objectInputStream.readObject();
            objectInputStream.close();
            
        } catch (IOException ex) {
            Logger.getLogger(JFrameBalance.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            
            Logger.getLogger(WeightService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return weights;
    }
    
    public void saveWeightsInFileBin(ArrayList<WeightResponse> weights){
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(this.path_temp+tempRegs));
            objectOutputStream.writeObject(weights);
            objectOutputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(JFrameBalance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
