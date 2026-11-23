/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import controllers.exceptions.NonexistentEntityException;
import controllers.exceptions.PreexistingEntityException;
import controllers.exceptions.RollbackFailureException;
import entities.LigneVente;
import entities.LigneVentePK;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import entities.Produit;
import entities.Vente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.UserTransaction;
import java.util.List;

/**
 *
 * @author adk
 */
public class LigneVenteJpaController implements Serializable {

    public LigneVenteJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(LigneVente ligneVente) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (ligneVente.getLigneVentePK() == null) {
            ligneVente.setLigneVentePK(new LigneVentePK());
        }
        ligneVente.getLigneVentePK().setIdV(ligneVente.getVente().getIdV());
        ligneVente.getLigneVentePK().setNLot(ligneVente.getProduit().getNLot());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Produit produit = ligneVente.getProduit();
            if (produit != null) {
                produit = em.getReference(produit.getClass(), produit.getNLot());
                ligneVente.setProduit(produit);
            }
            Vente vente = ligneVente.getVente();
            if (vente != null) {
                vente = em.getReference(vente.getClass(), vente.getIdV());
                ligneVente.setVente(vente);
            }
            em.persist(ligneVente);
            if (produit != null) {
                produit.getLigneVenteList().add(ligneVente);
                produit = em.merge(produit);
            }
            if (vente != null) {
                vente.getLigneVenteList().add(ligneVente);
                vente = em.merge(vente);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findLigneVente(ligneVente.getLigneVentePK()) != null) {
                throw new PreexistingEntityException("LigneVente " + ligneVente + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(LigneVente ligneVente) throws NonexistentEntityException, RollbackFailureException, Exception {
        ligneVente.getLigneVentePK().setIdV(ligneVente.getVente().getIdV());
        ligneVente.getLigneVentePK().setNLot(ligneVente.getProduit().getNLot());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            LigneVente persistentLigneVente = em.find(LigneVente.class, ligneVente.getLigneVentePK());
            Produit produitOld = persistentLigneVente.getProduit();
            Produit produitNew = ligneVente.getProduit();
            Vente venteOld = persistentLigneVente.getVente();
            Vente venteNew = ligneVente.getVente();
            if (produitNew != null) {
                produitNew = em.getReference(produitNew.getClass(), produitNew.getNLot());
                ligneVente.setProduit(produitNew);
            }
            if (venteNew != null) {
                venteNew = em.getReference(venteNew.getClass(), venteNew.getIdV());
                ligneVente.setVente(venteNew);
            }
            ligneVente = em.merge(ligneVente);
            if (produitOld != null && !produitOld.equals(produitNew)) {
                produitOld.getLigneVenteList().remove(ligneVente);
                produitOld = em.merge(produitOld);
            }
            if (produitNew != null && !produitNew.equals(produitOld)) {
                produitNew.getLigneVenteList().add(ligneVente);
                produitNew = em.merge(produitNew);
            }
            if (venteOld != null && !venteOld.equals(venteNew)) {
                venteOld.getLigneVenteList().remove(ligneVente);
                venteOld = em.merge(venteOld);
            }
            if (venteNew != null && !venteNew.equals(venteOld)) {
                venteNew.getLigneVenteList().add(ligneVente);
                venteNew = em.merge(venteNew);
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
                LigneVentePK id = ligneVente.getLigneVentePK();
                if (findLigneVente(id) == null) {
                    throw new NonexistentEntityException("The ligneVente with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(LigneVentePK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            LigneVente ligneVente;
            try {
                ligneVente = em.getReference(LigneVente.class, id);
                ligneVente.getLigneVentePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ligneVente with id " + id + " no longer exists.", enfe);
            }
            Produit produit = ligneVente.getProduit();
            if (produit != null) {
                produit.getLigneVenteList().remove(ligneVente);
                produit = em.merge(produit);
            }
            Vente vente = ligneVente.getVente();
            if (vente != null) {
                vente.getLigneVenteList().remove(ligneVente);
                vente = em.merge(vente);
            }
            em.remove(ligneVente);
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

    public List<LigneVente> findLigneVenteEntities() {
        return findLigneVenteEntities(true, -1, -1);
    }

    public List<LigneVente> findLigneVenteEntities(int maxResults, int firstResult) {
        return findLigneVenteEntities(false, maxResults, firstResult);
    }

    private List<LigneVente> findLigneVenteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(LigneVente.class));
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

    public LigneVente findLigneVente(LigneVentePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(LigneVente.class, id);
        } finally {
            em.close();
        }
    }

    public int getLigneVenteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<LigneVente> rt = cq.from(LigneVente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
