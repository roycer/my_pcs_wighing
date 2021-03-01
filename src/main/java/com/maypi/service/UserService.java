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
        
    public UserService(){
    }
    
    public UserResponse login(String username, String password){
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyDatabase");
        EntityManager em = emf.createEntityManager();
        
        UserRepositoryImpl ui = new UserRepositoryImpl(em);
        User user = ui.login(username, password);
        
        if(user == null){
            em.getTransaction().begin();
            user = new User("operador", "123456");
            ui.saveUser(user);
            em.getTransaction().commit();
        }
      
        if(user!=null){
            UserResponse userResponse = new UserResponse(user.getUsername(), user.getToken());
            return userResponse;
        }
        
        return null;
    }
}
