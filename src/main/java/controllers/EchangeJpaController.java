/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import controllers.exceptions.IllegalOrphanException;
import controllers.exceptions.NonexistentEntityException;
import controllers.exceptions.PreexistingEntityException;
import controllers.exceptions.RollbackFailureException;
import entities.Echange;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import entities.Utilisateur;
import entities.LigneEchange;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author adk
 */
public class EchangeJpaController implements Serializable {

    public EchangeJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Echange echange) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (echange.getLigneEchangeList() == null) {
            echange.setLigneEchangeList(new ArrayList<LigneEchange>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Utilisateur idGerant = echange.getIdGerant();
            if (idGerant != null) {
                idGerant = em.getReference(idGerant.getClass(), idGerant.getIdUtilisateur());
                echange.setIdGerant(idGerant);
            }
            List<LigneEchange> attachedLigneEchangeList = new ArrayList<LigneEchange>();
            for (LigneEchange ligneEchangeListLigneEchangeToAttach : echange.getLigneEchangeList()) {
                ligneEchangeListLigneEchangeToAttach = em.getReference(ligneEchangeListLigneEchangeToAttach.getClass(), ligneEchangeListLigneEchangeToAttach.getLigneEchangePK());
                attachedLigneEchangeList.add(ligneEchangeListLigneEchangeToAttach);
            }
            echange.setLigneEchangeList(attachedLigneEchangeList);
            em.persist(echange);
            if (idGerant != null) {
                idGerant.getEchangeList().add(echange);
                idGerant = em.merge(idGerant);
            }
            for (LigneEchange ligneEchangeListLigneEchange : echange.getLigneEchangeList()) {
                Echange oldEchangeOfLigneEchangeListLigneEchange = ligneEchangeListLigneEchange.getEchange();
                ligneEchangeListLigneEchange.setEchange(echange);
                ligneEchangeListLigneEchange = em.merge(ligneEchangeListLigneEchange);
                if (oldEchangeOfLigneEchangeListLigneEchange != null) {
                    oldEchangeOfLigneEchangeListLigneEchange.getLigneEchangeList().remove(ligneEchangeListLigneEchange);
                    oldEchangeOfLigneEchangeListLigneEchange = em.merge(oldEchangeOfLigneEchangeListLigneEchange);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findEchange(echange.getIdEchange()) != null) {
                throw new PreexistingEntityException("Echange " + echange + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Echange echange) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Echange persistentEchange = em.find(Echange.class, echange.getIdEchange());
            Utilisateur idGerantOld = persistentEchange.getIdGerant();
            Utilisateur idGerantNew = echange.getIdGerant();
            List<LigneEchange> ligneEchangeListOld = persistentEchange.getLigneEchangeList();
            List<LigneEchange> ligneEchangeListNew = echange.getLigneEchangeList();
            List<String> illegalOrphanMessages = null;
            for (LigneEchange ligneEchangeListOldLigneEchange : ligneEchangeListOld) {
                if (!ligneEchangeListNew.contains(ligneEchangeListOldLigneEchange)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain LigneEchange " + ligneEchangeListOldLigneEchange + " since its echange field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idGerantNew != null) {
                idGerantNew = em.getReference(idGerantNew.getClass(), idGerantNew.getIdUtilisateur());
                echange.setIdGerant(idGerantNew);
            }
            List<LigneEchange> attachedLigneEchangeListNew = new ArrayList<LigneEchange>();
            for (LigneEchange ligneEchangeListNewLigneEchangeToAttach : ligneEchangeListNew) {
                ligneEchangeListNewLigneEchangeToAttach = em.getReference(ligneEchangeListNewLigneEchangeToAttach.getClass(), ligneEchangeListNewLigneEchangeToAttach.getLigneEchangePK());
                attachedLigneEchangeListNew.add(ligneEchangeListNewLigneEchangeToAttach);
            }
            ligneEchangeListNew = attachedLigneEchangeListNew;
            echange.setLigneEchangeList(ligneEchangeListNew);
            echange = em.merge(echange);
            if (idGerantOld != null && !idGerantOld.equals(idGerantNew)) {
                idGerantOld.getEchangeList().remove(echange);
                idGerantOld = em.merge(idGerantOld);
            }
            if (idGerantNew != null && !idGerantNew.equals(idGerantOld)) {
                idGerantNew.getEchangeList().add(echange);
                idGerantNew = em.merge(idGerantNew);
            }
            for (LigneEchange ligneEchangeListNewLigneEchange : ligneEchangeListNew) {
                if (!ligneEchangeListOld.contains(ligneEchangeListNewLigneEchange)) {
                    Echange oldEchangeOfLigneEchangeListNewLigneEchange = ligneEchangeListNewLigneEchange.getEchange();
                    ligneEchangeListNewLigneEchange.setEchange(echange);
                    ligneEchangeListNewLigneEchange = em.merge(ligneEchangeListNewLigneEchange);
                    if (oldEchangeOfLigneEchangeListNewLigneEchange != null && !oldEchangeOfLigneEchangeListNewLigneEchange.equals(echange)) {
                        oldEchangeOfLigneEchangeListNewLigneEchange.getLigneEchangeList().remove(ligneEchangeListNewLigneEchange);
                        oldEchangeOfLigneEchangeListNewLigneEchange = em.merge(oldEchangeOfLigneEchangeListNewLigneEchange);
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
                String id = echange.getIdEchange();
                if (findEchange(id) == null) {
                    throw new NonexistentEntityException("The echange with id " + id + " no longer exists.");
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
            Echange echange;
            try {
                echange = em.getReference(Echange.class, id);
                echange.getIdEchange();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The echange with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<LigneEchange> ligneEchangeListOrphanCheck = echange.getLigneEchangeList();
            for (LigneEchange ligneEchangeListOrphanCheckLigneEchange : ligneEchangeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Echange (" + echange + ") cannot be destroyed since the LigneEchange " + ligneEchangeListOrphanCheckLigneEchange + " in its ligneEchangeList field has a non-nullable echange field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Utilisateur idGerant = echange.getIdGerant();
            if (idGerant != null) {
                idGerant.getEchangeList().remove(echange);
                idGerant = em.merge(idGerant);
            }
            em.remove(echange);
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

    public List<Echange> findEchangeEntities() {
        return findEchangeEntities(true, -1, -1);
    }

    public List<Echange> findEchangeEntities(int maxResults, int firstResult) {
        return findEchangeEntities(false, maxResults, firstResult);
    }

    private List<Echange> findEchangeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Echange.class));
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

    public Echange findEchange(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Echange.class, id);
        } finally {
            em.close();
        }
    }

    public int getEchangeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Echange> rt = cq.from(Echange.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
