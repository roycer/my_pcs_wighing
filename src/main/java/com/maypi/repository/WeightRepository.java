/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.repository;

import com.maypi.model.Weight;
import java.util.List;

/**
 *
 * @author mysac
 */
public interface WeightRepository {
    
    Weight getWeightById(Long id);
    List<Weight> getWeightByCode(String code);
    Weight saveWeight(Weight weight);
    void deleteWeight(Weight weight);
    
}
