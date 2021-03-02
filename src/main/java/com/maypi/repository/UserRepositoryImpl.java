/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.repository;

import com.maypi.model.User;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author mysac
 */
public class UserRepositoryImpl implements UserRepository {

    private EntityManager em;
    
    public UserRepositoryImpl(EntityManager em){
        this.em = em;        
    }
    
    @Override
    public User getUserById(Long id) {
        return em.find(User.class, id);
    }

    @Override
    public User login(String username, String password) {
        
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username AND u.password = :password",User.class);
        query.setParameter("username", username);
        query.setParameter("password", password);

        if(query.getResultList().size()>0){
            return query.getResultStream().findFirst().orElse(null);
        }

        return null; 
        
    }

    @Override
    public User saveUser(User user) {
        if (user.getId() == null) {
            em.persist(user);
        } else {
            user = em.merge(user);
        }
        return user;
    }

    
}
