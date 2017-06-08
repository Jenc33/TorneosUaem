/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.session;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jpa.entities.Tournaments;

/**
 *
 * @author ingjo
 */
public abstract class AbstractFacade<T> {

    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<Tournaments> findDate(String id) {
        javax.persistence.criteria.CriteriaQuery <Tournaments> cq = getEntityManager().getCriteriaBuilder().createQuery(Tournaments.class);
        Root<Tournaments> c = cq.from(Tournaments.class);
        cq.select(c);
        Predicate p = getEntityManager().getCriteriaBuilder().equal(c.get("inscriptionStartDate"), id);
        cq.where(p);
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    public List<Tournaments> findDiscipline(String id) {
        javax.persistence.criteria.CriteriaQuery <Tournaments> cq = getEntityManager().getCriteriaBuilder().createQuery(Tournaments.class);
        Root<Tournaments> c = cq.from(Tournaments.class);
        cq.select(c);
        Predicate p = getEntityManager().getCriteriaBuilder().equal(c.get("inscriptionStartDate"), id);
        cq.where(p);
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
}
