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
import entities.LigneEchange;
import java.util.ArrayList;
import java.util.List;
import entities.LigneVente;
import entities.Produit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.UserTransaction;

/**
 *
 * @author adk
 */
public class ProduitJpaController implements Serializable {

    public ProduitJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Produit produit) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (produit.getLigneEchangeList() == null) {
            produit.setLigneEchangeList(new ArrayList<LigneEchange>());
        }
        if (produit.getLigneVenteList() == null) {
            produit.setLigneVenteList(new ArrayList<LigneVente>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Utilisateur idUtilisateurEnreg = produit.getIdUtilisateurEnreg();
            if (idUtilisateurEnreg != null) {
                idUtilisateurEnreg = em.getReference(idUtilisateurEnreg.getClass(), idUtilisateurEnreg.getIdUtilisateur());
                produit.setIdUtilisateurEnreg(idUtilisateurEnreg);
            }
            List<LigneEchange> attachedLigneEchangeList = new ArrayList<LigneEchange>();
            for (LigneEchange ligneEchangeListLigneEchangeToAttach : produit.getLigneEchangeList()) {
                ligneEchangeListLigneEchangeToAttach = em.getReference(ligneEchangeListLigneEchangeToAttach.getClass(), ligneEchangeListLigneEchangeToAttach.getLigneEchangePK());
                attachedLigneEchangeList.add(ligneEchangeListLigneEchangeToAttach);
            }
            produit.setLigneEchangeList(attachedLigneEchangeList);
            List<LigneVente> attachedLigneVenteList = new ArrayList<LigneVente>();
            for (LigneVente ligneVenteListLigneVenteToAttach : produit.getLigneVenteList()) {
                ligneVenteListLigneVenteToAttach = em.getReference(ligneVenteListLigneVenteToAttach.getClass(), ligneVenteListLigneVenteToAttach.getLigneVentePK());
                attachedLigneVenteList.add(ligneVenteListLigneVenteToAttach);
            }
            produit.setLigneVenteList(attachedLigneVenteList);
            em.persist(produit);
            if (idUtilisateurEnreg != null) {
                idUtilisateurEnreg.getProduitList().add(produit);
                idUtilisateurEnreg = em.merge(idUtilisateurEnreg);
            }
            for (LigneEchange ligneEchangeListLigneEchange : produit.getLigneEchangeList()) {
                Produit oldProduitOfLigneEchangeListLigneEchange = ligneEchangeListLigneEchange.getProduit();
                ligneEchangeListLigneEchange.setProduit(produit);
                ligneEchangeListLigneEchange = em.merge(ligneEchangeListLigneEchange);
                if (oldProduitOfLigneEchangeListLigneEchange != null) {
                    oldProduitOfLigneEchangeListLigneEchange.getLigneEchangeList().remove(ligneEchangeListLigneEchange);
                    oldProduitOfLigneEchangeListLigneEchange = em.merge(oldProduitOfLigneEchangeListLigneEchange);
                }
            }
            for (LigneVente ligneVenteListLigneVente : produit.getLigneVenteList()) {
                Produit oldProduitOfLigneVenteListLigneVente = ligneVenteListLigneVente.getProduit();
                ligneVenteListLigneVente.setProduit(produit);
                ligneVenteListLigneVente = em.merge(ligneVenteListLigneVente);
                if (oldProduitOfLigneVenteListLigneVente != null) {
                    oldProduitOfLigneVenteListLigneVente.getLigneVenteList().remove(ligneVenteListLigneVente);
                    oldProduitOfLigneVenteListLigneVente = em.merge(oldProduitOfLigneVenteListLigneVente);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findProduit(produit.getNLot()) != null) {
                throw new PreexistingEntityException("Produit " + produit + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Produit produit) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Produit persistentProduit = em.find(Produit.class, produit.getNLot());
            Utilisateur idUtilisateurEnregOld = persistentProduit.getIdUtilisateurEnreg();
            Utilisateur idUtilisateurEnregNew = produit.getIdUtilisateurEnreg();
            List<LigneEchange> ligneEchangeListOld = persistentProduit.getLigneEchangeList();
            List<LigneEchange> ligneEchangeListNew = produit.getLigneEchangeList();
            List<LigneVente> ligneVenteListOld = persistentProduit.getLigneVenteList();
            List<LigneVente> ligneVenteListNew = produit.getLigneVenteList();
            List<String> illegalOrphanMessages = null;
            for (LigneEchange ligneEchangeListOldLigneEchange : ligneEchangeListOld) {
                if (!ligneEchangeListNew.contains(ligneEchangeListOldLigneEchange)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain LigneEchange " + ligneEchangeListOldLigneEchange + " since its produit field is not nullable.");
                }
            }
            for (LigneVente ligneVenteListOldLigneVente : ligneVenteListOld) {
                if (!ligneVenteListNew.contains(ligneVenteListOldLigneVente)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain LigneVente " + ligneVenteListOldLigneVente + " since its produit field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idUtilisateurEnregNew != null) {
                idUtilisateurEnregNew = em.getReference(idUtilisateurEnregNew.getClass(), idUtilisateurEnregNew.getIdUtilisateur());
                produit.setIdUtilisateurEnreg(idUtilisateurEnregNew);
            }
            List<LigneEchange> attachedLigneEchangeListNew = new ArrayList<LigneEchange>();
            for (LigneEchange ligneEchangeListNewLigneEchangeToAttach : ligneEchangeListNew) {
                ligneEchangeListNewLigneEchangeToAttach = em.getReference(ligneEchangeListNewLigneEchangeToAttach.getClass(), ligneEchangeListNewLigneEchangeToAttach.getLigneEchangePK());
                attachedLigneEchangeListNew.add(ligneEchangeListNewLigneEchangeToAttach);
            }
            ligneEchangeListNew = attachedLigneEchangeListNew;
            produit.setLigneEchangeList(ligneEchangeListNew);
            List<LigneVente> attachedLigneVenteListNew = new ArrayList<LigneVente>();
            for (LigneVente ligneVenteListNewLigneVenteToAttach : ligneVenteListNew) {
                ligneVenteListNewLigneVenteToAttach = em.getReference(ligneVenteListNewLigneVenteToAttach.getClass(), ligneVenteListNewLigneVenteToAttach.getLigneVentePK());
                attachedLigneVenteListNew.add(ligneVenteListNewLigneVenteToAttach);
            }
            ligneVenteListNew = attachedLigneVenteListNew;
            produit.setLigneVenteList(ligneVenteListNew);
            produit = em.merge(produit);
            if (idUtilisateurEnregOld != null && !idUtilisateurEnregOld.equals(idUtilisateurEnregNew)) {
                idUtilisateurEnregOld.getProduitList().remove(produit);
                idUtilisateurEnregOld = em.merge(idUtilisateurEnregOld);
            }
            if (idUtilisateurEnregNew != null && !idUtilisateurEnregNew.equals(idUtilisateurEnregOld)) {
                idUtilisateurEnregNew.getProduitList().add(produit);
                idUtilisateurEnregNew = em.merge(idUtilisateurEnregNew);
            }
            for (LigneEchange ligneEchangeListNewLigneEchange : ligneEchangeListNew) {
                if (!ligneEchangeListOld.contains(ligneEchangeListNewLigneEchange)) {
                    Produit oldProduitOfLigneEchangeListNewLigneEchange = ligneEchangeListNewLigneEchange.getProduit();
                    ligneEchangeListNewLigneEchange.setProduit(produit);
                    ligneEchangeListNewLigneEchange = em.merge(ligneEchangeListNewLigneEchange);
                    if (oldProduitOfLigneEchangeListNewLigneEchange != null && !oldProduitOfLigneEchangeListNewLigneEchange.equals(produit)) {
                        oldProduitOfLigneEchangeListNewLigneEchange.getLigneEchangeList().remove(ligneEchangeListNewLigneEchange);
                        oldProduitOfLigneEchangeListNewLigneEchange = em.merge(oldProduitOfLigneEchangeListNewLigneEchange);
                    }
                }
            }
            for (LigneVente ligneVenteListNewLigneVente : ligneVenteListNew) {
                if (!ligneVenteListOld.contains(ligneVenteListNewLigneVente)) {
                    Produit oldProduitOfLigneVenteListNewLigneVente = ligneVenteListNewLigneVente.getProduit();
                    ligneVenteListNewLigneVente.setProduit(produit);
                    ligneVenteListNewLigneVente = em.merge(ligneVenteListNewLigneVente);
                    if (oldProduitOfLigneVenteListNewLigneVente != null && !oldProduitOfLigneVenteListNewLigneVente.equals(produit)) {
                        oldProduitOfLigneVenteListNewLigneVente.getLigneVenteList().remove(ligneVenteListNewLigneVente);
                        oldProduitOfLigneVenteListNewLigneVente = em.merge(oldProduitOfLigneVenteListNewLigneVente);
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
                String id = produit.getNLot();
                if (findProduit(id) == null) {
                    throw new NonexistentEntityException("The produit with id " + id + " no longer exists.");
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
            Produit produit;
            try {
                produit = em.getReference(Produit.class, id);
                produit.getNLot();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The produit with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<LigneEchange> ligneEchangeListOrphanCheck = produit.getLigneEchangeList();
            for (LigneEchange ligneEchangeListOrphanCheckLigneEchange : ligneEchangeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Produit (" + produit + ") cannot be destroyed since the LigneEchange " + ligneEchangeListOrphanCheckLigneEchange + " in its ligneEchangeList field has a non-nullable produit field.");
            }
            List<LigneVente> ligneVenteListOrphanCheck = produit.getLigneVenteList();
            for (LigneVente ligneVenteListOrphanCheckLigneVente : ligneVenteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Produit (" + produit + ") cannot be destroyed since the LigneVente " + ligneVenteListOrphanCheckLigneVente + " in its ligneVenteList field has a non-nullable produit field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Utilisateur idUtilisateurEnreg = produit.getIdUtilisateurEnreg();
            if (idUtilisateurEnreg != null) {
                idUtilisateurEnreg.getProduitList().remove(produit);
                idUtilisateurEnreg = em.merge(idUtilisateurEnreg);
            }
            em.remove(produit);
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

    public List<Produit> findProduitEntities() {
        return findProduitEntities(true, -1, -1);
    }

    public List<Produit> findProduitEntities(int maxResults, int firstResult) {
        return findProduitEntities(false, maxResults, firstResult);
    }

    private List<Produit> findProduitEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Produit.class));
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

    public Produit findProduit(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Produit.class, id);
        } finally {
            em.close();
        }
    }

    public int getProduitCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produit> rt = cq.from(Produit.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
