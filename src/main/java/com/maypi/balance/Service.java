/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.balance;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author rcordova
 */
public class Service  {
    
    public void Service() {}
    
    public String response(URL url, Map<String,Object> params, String token) throws Exception {
        
        StringBuilder postData = new StringBuilder();
        for(Map.Entry<String,Object> param: params.entrySet()){
            if(postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append("=");
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setRequestProperty("Authorization", "bearer " + token);
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        
        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
        
        StringBuilder sb = new StringBuilder();
        
        for(int c; (c = in.read()) >= 0;){
            sb.append((char)c);
        }
        
        return sb.toString();
    }
    
    
    
    
}
