/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import controllers.exceptions.IllegalOrphanException;
import controllers.exceptions.NonexistentEntityException;
import controllers.exceptions.PreexistingEntityException;
import controllers.exceptions.RollbackFailureException;
import entities.BonDeCommande;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import entities.Utilisateur;
import entities.ConsultationBonCommande;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author adk
 */
public class BonDeCommandeJpaController implements Serializable {

    public BonDeCommandeJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(BonDeCommande bonDeCommande) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (bonDeCommande.getConsultationBonCommandeList() == null) {
            bonDeCommande.setConsultationBonCommandeList(new ArrayList<ConsultationBonCommande>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Utilisateur idGerantCreateur = bonDeCommande.getIdGerantCreateur();
            if (idGerantCreateur != null) {
                idGerantCreateur = em.getReference(idGerantCreateur.getClass(), idGerantCreateur.getIdUtilisateur());
                bonDeCommande.setIdGerantCreateur(idGerantCreateur);
            }
            List<ConsultationBonCommande> attachedConsultationBonCommandeList = new ArrayList<ConsultationBonCommande>();
            for (ConsultationBonCommande consultationBonCommandeListConsultationBonCommandeToAttach : bonDeCommande.getConsultationBonCommandeList()) {
                consultationBonCommandeListConsultationBonCommandeToAttach = em.getReference(consultationBonCommandeListConsultationBonCommandeToAttach.getClass(), consultationBonCommandeListConsultationBonCommandeToAttach.getConsultationBonCommandePK());
                attachedConsultationBonCommandeList.add(consultationBonCommandeListConsultationBonCommandeToAttach);
            }
            bonDeCommande.setConsultationBonCommandeList(attachedConsultationBonCommandeList);
            em.persist(bonDeCommande);
            if (idGerantCreateur != null) {
                idGerantCreateur.getBonDeCommandeList().add(bonDeCommande);
                idGerantCreateur = em.merge(idGerantCreateur);
            }
            for (ConsultationBonCommande consultationBonCommandeListConsultationBonCommande : bonDeCommande.getConsultationBonCommandeList()) {
                BonDeCommande oldBonDeCommandeOfConsultationBonCommandeListConsultationBonCommande = consultationBonCommandeListConsultationBonCommande.getBonDeCommande();
                consultationBonCommandeListConsultationBonCommande.setBonDeCommande(bonDeCommande);
                consultationBonCommandeListConsultationBonCommande = em.merge(consultationBonCommandeListConsultationBonCommande);
                if (oldBonDeCommandeOfConsultationBonCommandeListConsultationBonCommande != null) {
                    oldBonDeCommandeOfConsultationBonCommandeListConsultationBonCommande.getConsultationBonCommandeList().remove(consultationBonCommandeListConsultationBonCommande);
                    oldBonDeCommandeOfConsultationBonCommandeListConsultationBonCommande = em.merge(oldBonDeCommandeOfConsultationBonCommandeListConsultationBonCommande);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findBonDeCommande(bonDeCommande.getIdBon()) != null) {
                throw new PreexistingEntityException("BonDeCommande " + bonDeCommande + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(BonDeCommande bonDeCommande) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            BonDeCommande persistentBonDeCommande = em.find(BonDeCommande.class, bonDeCommande.getIdBon());
            Utilisateur idGerantCreateurOld = persistentBonDeCommande.getIdGerantCreateur();
            Utilisateur idGerantCreateurNew = bonDeCommande.getIdGerantCreateur();
            List<ConsultationBonCommande> consultationBonCommandeListOld = persistentBonDeCommande.getConsultationBonCommandeList();
            List<ConsultationBonCommande> consultationBonCommandeListNew = bonDeCommande.getConsultationBonCommandeList();
            List<String> illegalOrphanMessages = null;
            for (ConsultationBonCommande consultationBonCommandeListOldConsultationBonCommande : consultationBonCommandeListOld) {
                if (!consultationBonCommandeListNew.contains(consultationBonCommandeListOldConsultationBonCommande)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ConsultationBonCommande " + consultationBonCommandeListOldConsultationBonCommande + " since its bonDeCommande field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idGerantCreateurNew != null) {
                idGerantCreateurNew = em.getReference(idGerantCreateurNew.getClass(), idGerantCreateurNew.getIdUtilisateur());
                bonDeCommande.setIdGerantCreateur(idGerantCreateurNew);
            }
            List<ConsultationBonCommande> attachedConsultationBonCommandeListNew = new ArrayList<ConsultationBonCommande>();
            for (ConsultationBonCommande consultationBonCommandeListNewConsultationBonCommandeToAttach : consultationBonCommandeListNew) {
                consultationBonCommandeListNewConsultationBonCommandeToAttach = em.getReference(consultationBonCommandeListNewConsultationBonCommandeToAttach.getClass(), consultationBonCommandeListNewConsultationBonCommandeToAttach.getConsultationBonCommandePK());
                attachedConsultationBonCommandeListNew.add(consultationBonCommandeListNewConsultationBonCommandeToAttach);
            }
            consultationBonCommandeListNew = attachedConsultationBonCommandeListNew;
            bonDeCommande.setConsultationBonCommandeList(consultationBonCommandeListNew);
            bonDeCommande = em.merge(bonDeCommande);
            if (idGerantCreateurOld != null && !idGerantCreateurOld.equals(idGerantCreateurNew)) {
                idGerantCreateurOld.getBonDeCommandeList().remove(bonDeCommande);
                idGerantCreateurOld = em.merge(idGerantCreateurOld);
            }
            if (idGerantCreateurNew != null && !idGerantCreateurNew.equals(idGerantCreateurOld)) {
                idGerantCreateurNew.getBonDeCommandeList().add(bonDeCommande);
                idGerantCreateurNew = em.merge(idGerantCreateurNew);
            }
            for (ConsultationBonCommande consultationBonCommandeListNewConsultationBonCommande : consultationBonCommandeListNew) {
                if (!consultationBonCommandeListOld.contains(consultationBonCommandeListNewConsultationBonCommande)) {
                    BonDeCommande oldBonDeCommandeOfConsultationBonCommandeListNewConsultationBonCommande = consultationBonCommandeListNewConsultationBonCommande.getBonDeCommande();
                    consultationBonCommandeListNewConsultationBonCommande.setBonDeCommande(bonDeCommande);
                    consultationBonCommandeListNewConsultationBonCommande = em.merge(consultationBonCommandeListNewConsultationBonCommande);
                    if (oldBonDeCommandeOfConsultationBonCommandeListNewConsultationBonCommande != null && !oldBonDeCommandeOfConsultationBonCommandeListNewConsultationBonCommande.equals(bonDeCommande)) {
                        oldBonDeCommandeOfConsultationBonCommandeListNewConsultationBonCommande.getConsultationBonCommandeList().remove(consultationBonCommandeListNewConsultationBonCommande);
                        oldBonDeCommandeOfConsultationBonCommandeListNewConsultationBonCommande = em.merge(oldBonDeCommandeOfConsultationBonCommandeListNewConsultationBonCommande);
                    }
                }
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
                String id = bonDeCommande.getIdBon();
                if (findBonDeCommande(id) == null) {
                    throw new NonexistentEntityException("The bonDeCommande with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            BonDeCommande bonDeCommande;
            try {
                bonDeCommande = em.getReference(BonDeCommande.class, id);
                bonDeCommande.getIdBon();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The bonDeCommande with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<ConsultationBonCommande> consultationBonCommandeListOrphanCheck = bonDeCommande.getConsultationBonCommandeList();
            for (ConsultationBonCommande consultationBonCommandeListOrphanCheckConsultationBonCommande : consultationBonCommandeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This BonDeCommande (" + bonDeCommande + ") cannot be destroyed since the ConsultationBonCommande " + consultationBonCommandeListOrphanCheckConsultationBonCommande + " in its consultationBonCommandeList field has a non-nullable bonDeCommande field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Utilisateur idGerantCreateur = bonDeCommande.getIdGerantCreateur();
            if (idGerantCreateur != null) {
                idGerantCreateur.getBonDeCommandeList().remove(bonDeCommande);
                idGerantCreateur = em.merge(idGerantCreateur);
            }
            em.remove(bonDeCommande);
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

    public List<BonDeCommande> findBonDeCommandeEntities() {
        return findBonDeCommandeEntities(true, -1, -1);
    }

    public List<BonDeCommande> findBonDeCommandeEntities(int maxResults, int firstResult) {
        return findBonDeCommandeEntities(false, maxResults, firstResult);
    }

    private List<BonDeCommande> findBonDeCommandeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(BonDeCommande.class));
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

    public BonDeCommande findBonDeCommande(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(BonDeCommande.class, id);
        } finally {
            em.close();
        }
    }

    public int getBonDeCommandeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<BonDeCommande> rt = cq.from(BonDeCommande.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
