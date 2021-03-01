/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.service;

import com.maypi.model.Param;
import com.maypi.repository.ParamRepositoryImpl;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author mysac
 */
public class ParamService {
    
    public ParamService(){}
    
    public String getParam(String field){
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyDatabase");
        EntityManager em = emf.createEntityManager();
        
        ParamRepositoryImpl pi = new ParamRepositoryImpl(em);
        Param param = pi.getParamByField(field);
        
        if(param!=null){
            String param_value = param.getValue();
            return param_value;
        }
        
        return "";
    }
    
    public Boolean setParam(String field, String value){
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyDatabase");
        EntityManager em = emf.createEntityManager();

        ParamRepositoryImpl pi = new ParamRepositoryImpl(em);
        Param param = pi.getParamByField(field);
        
         em.getTransaction().begin();
        if(param!=null){
            param = pi.saveParam(param);
        }
        else{
            param = pi.saveParam(new Param(field, value));
        }

        em.getTransaction().commit();
        
        if(param!=null){
            return true;
        }

        return false;
    }
}
