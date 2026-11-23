/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import controllers.exceptions.NonexistentEntityException;
import controllers.exceptions.PreexistingEntityException;
import controllers.exceptions.RollbackFailureException;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import entities.Echange;
import entities.LigneEchange;
import entities.LigneEchangePK;
import entities.Produit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.UserTransaction;
import java.util.List;

/**
 *
 * @author adk
 */
public class LigneEchangeJpaController implements Serializable {

    public LigneEchangeJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(LigneEchange ligneEchange) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (ligneEchange.getLigneEchangePK() == null) {
            ligneEchange.setLigneEchangePK(new LigneEchangePK());
        }
        ligneEchange.getLigneEchangePK().setNLot(ligneEchange.getProduit().getNLot());
        ligneEchange.getLigneEchangePK().setIdEchange(ligneEchange.getEchange().getIdEchange());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Echange echange = ligneEchange.getEchange();
            if (echange != null) {
                echange = em.getReference(echange.getClass(), echange.getIdEchange());
                ligneEchange.setEchange(echange);
            }
            Produit produit = ligneEchange.getProduit();
            if (produit != null) {
                produit = em.getReference(produit.getClass(), produit.getNLot());
                ligneEchange.setProduit(produit);
            }
            em.persist(ligneEchange);
            if (echange != null) {
                echange.getLigneEchangeList().add(ligneEchange);
                echange = em.merge(echange);
            }
            if (produit != null) {
                produit.getLigneEchangeList().add(ligneEchange);
                produit = em.merge(produit);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findLigneEchange(ligneEchange.getLigneEchangePK()) != null) {
                throw new PreexistingEntityException("LigneEchange " + ligneEchange + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(LigneEchange ligneEchange) throws NonexistentEntityException, RollbackFailureException, Exception {
        ligneEchange.getLigneEchangePK().setNLot(ligneEchange.getProduit().getNLot());
        ligneEchange.getLigneEchangePK().setIdEchange(ligneEchange.getEchange().getIdEchange());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            LigneEchange persistentLigneEchange = em.find(LigneEchange.class, ligneEchange.getLigneEchangePK());
            Echange echangeOld = persistentLigneEchange.getEchange();
            Echange echangeNew = ligneEchange.getEchange();
            Produit produitOld = persistentLigneEchange.getProduit();
            Produit produitNew = ligneEchange.getProduit();
            if (echangeNew != null) {
                echangeNew = em.getReference(echangeNew.getClass(), echangeNew.getIdEchange());
                ligneEchange.setEchange(echangeNew);
            }
            if (produitNew != null) {
                produitNew = em.getReference(produitNew.getClass(), produitNew.getNLot());
                ligneEchange.setProduit(produitNew);
            }
            ligneEchange = em.merge(ligneEchange);
            if (echangeOld != null && !echangeOld.equals(echangeNew)) {
                echangeOld.getLigneEchangeList().remove(ligneEchange);
                echangeOld = em.merge(echangeOld);
            }
            if (echangeNew != null && !echangeNew.equals(echangeOld)) {
                echangeNew.getLigneEchangeList().add(ligneEchange);
                echangeNew = em.merge(echangeNew);
            }
            if (produitOld != null && !produitOld.equals(produitNew)) {
                produitOld.getLigneEchangeList().remove(ligneEchange);
                produitOld = em.merge(produitOld);
            }
            if (produitNew != null && !produitNew.equals(produitOld)) {
                produitNew.getLigneEchangeList().add(ligneEchange);
                produitNew = em.merge(produitNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                LigneEchangePK id = ligneEchange.getLigneEchangePK();
                if (findLigneEchange(id) == null) {
                    throw new NonexistentEntityException("The ligneEchange with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(LigneEchangePK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            LigneEchange ligneEchange;
            try {
                ligneEchange = em.getReference(LigneEchange.class, id);
                ligneEchange.getLigneEchangePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ligneEchange with id " + id + " no longer exists.", enfe);
            }
            Echange echange = ligneEchange.getEchange();
            if (echange != null) {
                echange.getLigneEchangeList().remove(ligneEchange);
                echange = em.merge(echange);
            }
            Produit produit = ligneEchange.getProduit();
            if (produit != null) {
                produit.getLigneEchangeList().remove(ligneEchange);
                produit = em.merge(produit);
            }
            em.remove(ligneEchange);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<LigneEchange> findLigneEchangeEntities() {
        return findLigneEchangeEntities(true, -1, -1);
    }

    public List<LigneEchange> findLigneEchangeEntities(int maxResults, int firstResult) {
        return findLigneEchangeEntities(false, maxResults, firstResult);
    }

    private List<LigneEchange> findLigneEchangeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(LigneEchange.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public LigneEchange findLigneEchange(LigneEchangePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(LigneEchange.class, id);
        } finally {
            em.close();
        }
    }

    public int getLigneEchangeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<LigneEchange> rt = cq.from(LigneEchange.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
