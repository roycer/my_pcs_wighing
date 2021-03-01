/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.repository;

import com.maypi.model.Param;

/**
 *
 * @author mysac
 */
public interface ParamRepository {
    
    /**
     *
     * @param id
     * @return
     */
    Param getParamById(Long id);

    /**
     *
     * @param field
     * @return
     */
    Param getParamByField(String field);

    /**
     *
     * @param param
     * @return
     */
    Param saveParam(Param param);
    
}
