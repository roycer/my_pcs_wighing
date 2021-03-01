/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.service.response;

/**
 *
 * @author rcordova
 */
public class UserResponse implements Response {
    
    private String username;
    private String token;
    private Boolean status;
    
    public UserResponse(String username, String token) {
        this.username = username;
        this.token = token;
        this.status = true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    public void setStatus(boolean status) {
        this.status = status;
    }
    
    @Override
    public Boolean getStatus() {
        
        if(this.username.length() > 0){
            return this.status;
        }
        
        return false;
    }
}
