/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.repository;

import com.maypi.model.Param;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author mysac
 */
public class ParamRepositoryImpl implements ParamRepository {

    private EntityManager em;
    
    public ParamRepositoryImpl(EntityManager em){
        this.em = em;        
    }

    @Override
    public Param getParamById(Long id) {
        return em.find(Param.class, id);
    }

    @Override
    public Param getParamByField(String field) {
        
        TypedQuery<Param> query = em.createQuery("SELECT p FROM Param p WHERE p.field = :field",Param.class);
        query.setParameter("field", field);

        if(query.getResultList().size()>0){
            return query.getSingleResult();
        }

        return null; 
    }

    @Override
    public Param saveParam(Param param) {
        if (param.getId() == null) {
            em.persist(param);
        } else {
            param = em.merge(param);
        }
        return param;
    }

}
