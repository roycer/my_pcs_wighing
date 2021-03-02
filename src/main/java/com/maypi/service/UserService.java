/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.service;

import com.maypi.model.User;
import com.maypi.repository.UserRepositoryImpl;
import com.maypi.service.response.UserResponse;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 *
 * @author mysac
 */
public class UserService {
        
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyDatabase");
    EntityManager em;
    
    public UserService(){
    }
    
    public UserResponse login(String username, String password){
        
        em = emf.createEntityManager();
        
        UserRepositoryImpl ui = new UserRepositoryImpl(em);
        User user = ui.login(username, password);
        
        if(user == null) return null;
        
        UserResponse userResponse = new UserResponse(user.getUsername(), user.getToken());
        return userResponse;
        
    }
}
