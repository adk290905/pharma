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
import entities.BonDeCommande;
import entities.ConsultationBonCommande;
import entities.ConsultationBonCommandePK;
import entities.Utilisateur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.UserTransaction;
import java.util.List;

/**
 *
 * @author adk
 */
public class ConsultationBonCommandeJpaController implements Serializable {

    public ConsultationBonCommandeJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ConsultationBonCommande consultationBonCommande) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (consultationBonCommande.getConsultationBonCommandePK() == null) {
            consultationBonCommande.setConsultationBonCommandePK(new ConsultationBonCommandePK());
        }
        consultationBonCommande.getConsultationBonCommandePK().setIdUtilisateur(consultationBonCommande.getUtilisateur().getIdUtilisateur());
        consultationBonCommande.getConsultationBonCommandePK().setIdBon(consultationBonCommande.getBonDeCommande().getIdBon());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            BonDeCommande bonDeCommande = consultationBonCommande.getBonDeCommande();
            if (bonDeCommande != null) {
                bonDeCommande = em.getReference(bonDeCommande.getClass(), bonDeCommande.getIdBon());
                consultationBonCommande.setBonDeCommande(bonDeCommande);
            }
            Utilisateur utilisateur = consultationBonCommande.getUtilisateur();
            if (utilisateur != null) {
                utilisateur = em.getReference(utilisateur.getClass(), utilisateur.getIdUtilisateur());
                consultationBonCommande.setUtilisateur(utilisateur);
            }
            em.persist(consultationBonCommande);
            if (bonDeCommande != null) {
                bonDeCommande.getConsultationBonCommandeList().add(consultationBonCommande);
                bonDeCommande = em.merge(bonDeCommande);
            }
            if (utilisateur != null) {
                utilisateur.getConsultationBonCommandeList().add(consultationBonCommande);
                utilisateur = em.merge(utilisateur);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findConsultationBonCommande(consultationBonCommande.getConsultationBonCommandePK()) != null) {
                throw new PreexistingEntityException("ConsultationBonCommande " + consultationBonCommande + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ConsultationBonCommande consultationBonCommande) throws NonexistentEntityException, RollbackFailureException, Exception {
        consultationBonCommande.getConsultationBonCommandePK().setIdUtilisateur(consultationBonCommande.getUtilisateur().getIdUtilisateur());
        consultationBonCommande.getConsultationBonCommandePK().setIdBon(consultationBonCommande.getBonDeCommande().getIdBon());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            ConsultationBonCommande persistentConsultationBonCommande = em.find(ConsultationBonCommande.class, consultationBonCommande.getConsultationBonCommandePK());
            BonDeCommande bonDeCommandeOld = persistentConsultationBonCommande.getBonDeCommande();
            BonDeCommande bonDeCommandeNew = consultationBonCommande.getBonDeCommande();
            Utilisateur utilisateurOld = persistentConsultationBonCommande.getUtilisateur();
            Utilisateur utilisateurNew = consultationBonCommande.getUtilisateur();
            if (bonDeCommandeNew != null) {
                bonDeCommandeNew = em.getReference(bonDeCommandeNew.getClass(), bonDeCommandeNew.getIdBon());
                consultationBonCommande.setBonDeCommande(bonDeCommandeNew);
            }
            if (utilisateurNew != null) {
                utilisateurNew = em.getReference(utilisateurNew.getClass(), utilisateurNew.getIdUtilisateur());
                consultationBonCommande.setUtilisateur(utilisateurNew);
            }
            consultationBonCommande = em.merge(consultationBonCommande);
            if (bonDeCommandeOld != null && !bonDeCommandeOld.equals(bonDeCommandeNew)) {
                bonDeCommandeOld.getConsultationBonCommandeList().remove(consultationBonCommande);
                bonDeCommandeOld = em.merge(bonDeCommandeOld);
            }
            if (bonDeCommandeNew != null && !bonDeCommandeNew.equals(bonDeCommandeOld)) {
                bonDeCommandeNew.getConsultationBonCommandeList().add(consultationBonCommande);
                bonDeCommandeNew = em.merge(bonDeCommandeNew);
            }
            if (utilisateurOld != null && !utilisateurOld.equals(utilisateurNew)) {
                utilisateurOld.getConsultationBonCommandeList().remove(consultationBonCommande);
                utilisateurOld = em.merge(utilisateurOld);
            }
            if (utilisateurNew != null && !utilisateurNew.equals(utilisateurOld)) {
                utilisateurNew.getConsultationBonCommandeList().add(consultationBonCommande);
                utilisateurNew = em.merge(utilisateurNew);
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
                ConsultationBonCommandePK id = consultationBonCommande.getConsultationBonCommandePK();
                if (findConsultationBonCommande(id) == null) {
                    throw new NonexistentEntityException("The consultationBonCommande with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ConsultationBonCommandePK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            ConsultationBonCommande consultationBonCommande;
            try {
                consultationBonCommande = em.getReference(ConsultationBonCommande.class, id);
                consultationBonCommande.getConsultationBonCommandePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The consultationBonCommande with id " + id + " no longer exists.", enfe);
            }
            BonDeCommande bonDeCommande = consultationBonCommande.getBonDeCommande();
            if (bonDeCommande != null) {
                bonDeCommande.getConsultationBonCommandeList().remove(consultationBonCommande);
                bonDeCommande = em.merge(bonDeCommande);
            }
            Utilisateur utilisateur = consultationBonCommande.getUtilisateur();
            if (utilisateur != null) {
                utilisateur.getConsultationBonCommandeList().remove(consultationBonCommande);
                utilisateur = em.merge(utilisateur);
            }
            em.remove(consultationBonCommande);
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

    public List<ConsultationBonCommande> findConsultationBonCommandeEntities() {
        return findConsultationBonCommandeEntities(true, -1, -1);
    }

    public List<ConsultationBonCommande> findConsultationBonCommandeEntities(int maxResults, int firstResult) {
        return findConsultationBonCommandeEntities(false, maxResults, firstResult);
    }

    private List<ConsultationBonCommande> findConsultationBonCommandeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ConsultationBonCommande.class));
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

    public ConsultationBonCommande findConsultationBonCommande(ConsultationBonCommandePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ConsultationBonCommande.class, id);
        } finally {
            em.close();
        }
    }

    public int getConsultationBonCommandeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ConsultationBonCommande> rt = cq.from(ConsultationBonCommande.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
