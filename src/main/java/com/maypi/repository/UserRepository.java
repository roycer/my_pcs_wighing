/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.repository;

import com.maypi.model.User;

/**
 *
 * @author mysac
 */
public interface UserRepository {
    
    /**
     *
     * @param id
     * @return
     */
    User getUserById(Long id);

    /**
     *
     * @param username
     * @param password
     * @return
     */
    User login(String username, String password);

    /**
     *
     * @param weight
     * @return
     */
    User saveUser(User user);
    
}
