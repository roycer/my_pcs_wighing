/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.repository;

import com.maypi.model.Weight;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author mysac
 */
public class WeightRepositoryImpl implements WeightRepository {

    private EntityManager em;
    
    public WeightRepositoryImpl(EntityManager em){
        this.em = em;
    }
    
    @Override
    public Weight getWeightById(Long id) {
        return em.find(Weight.class, id);
    }

    @Override
    public List<Weight> getWeightByCode(String code) {
        TypedQuery<Weight> query = em.createQuery("SELECT w FROM Weight w WHERE w.code = :code",Weight.class);
        query.setParameter("code", code);
        return query.getResultList();
    }

    @Override
    public List<Weight> getWeightToSync() {
        TypedQuery<Weight> query = em.createQuery("SELECT w FROM Weight w WHERE w.sync IS NULL OR w.sync = 0",Weight.class);
        return query.getResultList();
    }
    
    @Override
    public Weight saveWeight(Weight weight) {
        if (weight.getId() == null) {
            em.persist(weight);
        } else {
            weight = em.merge(weight);
        }
        return weight;
    }

    @Override
    public void deleteWeight(Weight weight) {
        if (em.contains(weight)) {
            em.remove(weight);
        } else {
            em.merge(weight);
        }
    }
    

    
}
