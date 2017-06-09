/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.session;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jpa.entities.Disciplines;
import jpa.entities.Equips;
import jpa.entities.Students;
import jpa.entities.Tournaments;

/**
 *
 * @author equipo Diseño de Sistemas
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
    
    /**
     * Asocia el id del equipo a crear con el usuario (capitan) que lo crea
     * @param id_captain se obtiene de la sesion
     * @param id_equip se obtiene de la base de datos
     */
    public void createUpdateCaptain(Integer id_captain, Integer id_equip){
        Equips s = new Equips();
        s.setId(id_equip);
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaUpdate<Students> update = cb.createCriteriaUpdate(Students.class);
        Root e = update.from(Students.class);
        update.set("idEquip", s);
        update.where(getEntityManager().getCriteriaBuilder().equal(e.get("id"), id_captain));
        getEntityManager().createQuery(update).executeUpdate();
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
    
    /**
     * Realiza la consulta en la base tomando como parametro la fecha
     * ingresada por el usuario
     * @param id fecha obtenida del form
     * @return una lista de resultados
     */
    public List<Tournaments> findDate(String id) {
        javax.persistence.criteria.CriteriaQuery <Tournaments> cq = getEntityManager().getCriteriaBuilder().createQuery(Tournaments.class);
        Root<Tournaments> c = cq.from(Tournaments.class);
        cq.select(c);
        Predicate p = getEntityManager().getCriteriaBuilder().equal(c.get("inscriptionStartDate"), id);
        cq.where(p);
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    /**
     * Realiza la consulta en la base de datos tomando como parametro la 
     * disicplina
     * @param name nombre de la disciplina a buscar obtenida del form
     * @return lista de torneos que pertenecen a la disciplina
     */
    public List<Tournaments> findDiscipline(String name) {
        javax.persistence.criteria.CriteriaQuery <Disciplines> cq = getEntityManager().getCriteriaBuilder().createQuery(Disciplines.class);
        Root<Disciplines> c = cq.from(Disciplines.class);
        cq.select(c);
        Predicate p = getEntityManager().getCriteriaBuilder().equal(c.get("name"), name);
        cq.where(p);
        Disciplines d = getEntityManager().createQuery(cq).getResultList().get(0);
        
        javax.persistence.criteria.CriteriaQuery <Tournaments> ct = getEntityManager().getCriteriaBuilder().createQuery(Tournaments.class);
        Root<Tournaments> t = ct.from(Tournaments.class);
        ct.select(t);
        Predicate pt = getEntityManager().getCriteriaBuilder().equal(t.get("idDisciplina").get("id"), d.getId());
        System.out.println("MIRA: "+t.get("idDisciplina").get("id")+"  "+d.getId());
        ct.where(pt);
        return getEntityManager().createQuery(ct).getResultList();
    }
    
    /**
     * Realiza la consulta a la base de datos tomando como parametro
     * el nombre del torneo
     * @param name nombre del torneo obtenido del form 
     * @return lista de torneos con el nombre a buscar
     */
    public List<Tournaments> findName(String name) {
        javax.persistence.criteria.CriteriaQuery <Tournaments> cq = getEntityManager().getCriteriaBuilder().createQuery(Tournaments.class);
        Root<Tournaments> c = cq.from(Tournaments.class);
        cq.select(c);
        Predicate p = getEntityManager().getCriteriaBuilder().equal(c.get("name"), name);
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
