/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import controllers.exceptions.IllegalOrphanException;
import controllers.exceptions.NonexistentEntityException;
import controllers.exceptions.PreexistingEntityException;
import controllers.exceptions.RollbackFailureException;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import entities.Utilisateur;
import entities.LigneVente;
import entities.Vente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author adk
 */
public class VenteJpaController implements Serializable {

    public VenteJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Vente vente) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (vente.getLigneVenteList() == null) {
            vente.setLigneVenteList(new ArrayList<LigneVente>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Utilisateur idUtilisateur = vente.getIdUtilisateur();
            if (idUtilisateur != null) {
                idUtilisateur = em.getReference(idUtilisateur.getClass(), idUtilisateur.getIdUtilisateur());
                vente.setIdUtilisateur(idUtilisateur);
            }
            List<LigneVente> attachedLigneVenteList = new ArrayList<LigneVente>();
            for (LigneVente ligneVenteListLigneVenteToAttach : vente.getLigneVenteList()) {
                ligneVenteListLigneVenteToAttach = em.getReference(ligneVenteListLigneVenteToAttach.getClass(), ligneVenteListLigneVenteToAttach.getLigneVentePK());
                attachedLigneVenteList.add(ligneVenteListLigneVenteToAttach);
            }
            vente.setLigneVenteList(attachedLigneVenteList);
            em.persist(vente);
            if (idUtilisateur != null) {
                idUtilisateur.getVenteList().add(vente);
                idUtilisateur = em.merge(idUtilisateur);
            }
            for (LigneVente ligneVenteListLigneVente : vente.getLigneVenteList()) {
                Vente oldVenteOfLigneVenteListLigneVente = ligneVenteListLigneVente.getVente();
                ligneVenteListLigneVente.setVente(vente);
                ligneVenteListLigneVente = em.merge(ligneVenteListLigneVente);
                if (oldVenteOfLigneVenteListLigneVente != null) {
                    oldVenteOfLigneVenteListLigneVente.getLigneVenteList().remove(ligneVenteListLigneVente);
                    oldVenteOfLigneVenteListLigneVente = em.merge(oldVenteOfLigneVenteListLigneVente);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findVente(vente.getIdV()) != null) {
                throw new PreexistingEntityException("Vente " + vente + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Vente vente) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Vente persistentVente = em.find(Vente.class, vente.getIdV());
            Utilisateur idUtilisateurOld = persistentVente.getIdUtilisateur();
            Utilisateur idUtilisateurNew = vente.getIdUtilisateur();
            List<LigneVente> ligneVenteListOld = persistentVente.getLigneVenteList();
            List<LigneVente> ligneVenteListNew = vente.getLigneVenteList();
            List<String> illegalOrphanMessages = null;
            for (LigneVente ligneVenteListOldLigneVente : ligneVenteListOld) {
                if (!ligneVenteListNew.contains(ligneVenteListOldLigneVente)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain LigneVente " + ligneVenteListOldLigneVente + " since its vente field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idUtilisateurNew != null) {
                idUtilisateurNew = em.getReference(idUtilisateurNew.getClass(), idUtilisateurNew.getIdUtilisateur());
                vente.setIdUtilisateur(idUtilisateurNew);
            }
            List<LigneVente> attachedLigneVenteListNew = new ArrayList<LigneVente>();
            for (LigneVente ligneVenteListNewLigneVenteToAttach : ligneVenteListNew) {
                ligneVenteListNewLigneVenteToAttach = em.getReference(ligneVenteListNewLigneVenteToAttach.getClass(), ligneVenteListNewLigneVenteToAttach.getLigneVentePK());
                attachedLigneVenteListNew.add(ligneVenteListNewLigneVenteToAttach);
            }
            ligneVenteListNew = attachedLigneVenteListNew;
            vente.setLigneVenteList(ligneVenteListNew);
            vente = em.merge(vente);
            if (idUtilisateurOld != null && !idUtilisateurOld.equals(idUtilisateurNew)) {
                idUtilisateurOld.getVenteList().remove(vente);
                idUtilisateurOld = em.merge(idUtilisateurOld);
            }
            if (idUtilisateurNew != null && !idUtilisateurNew.equals(idUtilisateurOld)) {
                idUtilisateurNew.getVenteList().add(vente);
                idUtilisateurNew = em.merge(idUtilisateurNew);
            }
            for (LigneVente ligneVenteListNewLigneVente : ligneVenteListNew) {
                if (!ligneVenteListOld.contains(ligneVenteListNewLigneVente)) {
                    Vente oldVenteOfLigneVenteListNewLigneVente = ligneVenteListNewLigneVente.getVente();
                    ligneVenteListNewLigneVente.setVente(vente);
                    ligneVenteListNewLigneVente = em.merge(ligneVenteListNewLigneVente);
                    if (oldVenteOfLigneVenteListNewLigneVente != null && !oldVenteOfLigneVenteListNewLigneVente.equals(vente)) {
                        oldVenteOfLigneVenteListNewLigneVente.getLigneVenteList().remove(ligneVenteListNewLigneVente);
                        oldVenteOfLigneVenteListNewLigneVente = em.merge(oldVenteOfLigneVenteListNewLigneVente);
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
                String id = vente.getIdV();
                if (findVente(id) == null) {
                    throw new NonexistentEntityException("The vente with id " + id + " no longer exists.");
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
            Vente vente;
            try {
                vente = em.getReference(Vente.class, id);
                vente.getIdV();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vente with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<LigneVente> ligneVenteListOrphanCheck = vente.getLigneVenteList();
            for (LigneVente ligneVenteListOrphanCheckLigneVente : ligneVenteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Vente (" + vente + ") cannot be destroyed since the LigneVente " + ligneVenteListOrphanCheckLigneVente + " in its ligneVenteList field has a non-nullable vente field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Utilisateur idUtilisateur = vente.getIdUtilisateur();
            if (idUtilisateur != null) {
                idUtilisateur.getVenteList().remove(vente);
                idUtilisateur = em.merge(idUtilisateur);
            }
            em.remove(vente);
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

    public List<Vente> findVenteEntities() {
        return findVenteEntities(true, -1, -1);
    }

    public List<Vente> findVenteEntities(int maxResults, int firstResult) {
        return findVenteEntities(false, maxResults, firstResult);
    }

    private List<Vente> findVenteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Vente.class));
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

    public Vente findVente(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Vente.class, id);
        } finally {
            em.close();
        }
    }

    public int getVenteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Vente> rt = cq.from(Vente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
