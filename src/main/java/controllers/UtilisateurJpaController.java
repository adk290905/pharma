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
import entities.Produit;
import java.util.ArrayList;
import java.util.List;
import entities.BonDeCommande;
import entities.ConsultationBonCommande;
import entities.Echange;
import entities.Utilisateur;
import entities.Vente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.UserTransaction;

/**
 *
 * @author adk
 */
public class UtilisateurJpaController implements Serializable {

    public UtilisateurJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Utilisateur utilisateur) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (utilisateur.getProduitList() == null) {
            utilisateur.setProduitList(new ArrayList<Produit>());
        }
        if (utilisateur.getBonDeCommandeList() == null) {
            utilisateur.setBonDeCommandeList(new ArrayList<BonDeCommande>());
        }
        if (utilisateur.getConsultationBonCommandeList() == null) {
            utilisateur.setConsultationBonCommandeList(new ArrayList<ConsultationBonCommande>());
        }
        if (utilisateur.getEchangeList() == null) {
            utilisateur.setEchangeList(new ArrayList<Echange>());
        }
        if (utilisateur.getVenteList() == null) {
            utilisateur.setVenteList(new ArrayList<Vente>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Produit> attachedProduitList = new ArrayList<Produit>();
            for (Produit produitListProduitToAttach : utilisateur.getProduitList()) {
                produitListProduitToAttach = em.getReference(produitListProduitToAttach.getClass(), produitListProduitToAttach.getNLot());
                attachedProduitList.add(produitListProduitToAttach);
            }
            utilisateur.setProduitList(attachedProduitList);
            List<BonDeCommande> attachedBonDeCommandeList = new ArrayList<BonDeCommande>();
            for (BonDeCommande bonDeCommandeListBonDeCommandeToAttach : utilisateur.getBonDeCommandeList()) {
                bonDeCommandeListBonDeCommandeToAttach = em.getReference(bonDeCommandeListBonDeCommandeToAttach.getClass(), bonDeCommandeListBonDeCommandeToAttach.getIdBon());
                attachedBonDeCommandeList.add(bonDeCommandeListBonDeCommandeToAttach);
            }
            utilisateur.setBonDeCommandeList(attachedBonDeCommandeList);
            List<ConsultationBonCommande> attachedConsultationBonCommandeList = new ArrayList<ConsultationBonCommande>();
            for (ConsultationBonCommande consultationBonCommandeListConsultationBonCommandeToAttach : utilisateur.getConsultationBonCommandeList()) {
                consultationBonCommandeListConsultationBonCommandeToAttach = em.getReference(consultationBonCommandeListConsultationBonCommandeToAttach.getClass(), consultationBonCommandeListConsultationBonCommandeToAttach.getConsultationBonCommandePK());
                attachedConsultationBonCommandeList.add(consultationBonCommandeListConsultationBonCommandeToAttach);
            }
            utilisateur.setConsultationBonCommandeList(attachedConsultationBonCommandeList);
            List<Echange> attachedEchangeList = new ArrayList<Echange>();
            for (Echange echangeListEchangeToAttach : utilisateur.getEchangeList()) {
                echangeListEchangeToAttach = em.getReference(echangeListEchangeToAttach.getClass(), echangeListEchangeToAttach.getIdEchange());
                attachedEchangeList.add(echangeListEchangeToAttach);
            }
            utilisateur.setEchangeList(attachedEchangeList);
            List<Vente> attachedVenteList = new ArrayList<Vente>();
            for (Vente venteListVenteToAttach : utilisateur.getVenteList()) {
                venteListVenteToAttach = em.getReference(venteListVenteToAttach.getClass(), venteListVenteToAttach.getIdV());
                attachedVenteList.add(venteListVenteToAttach);
            }
            utilisateur.setVenteList(attachedVenteList);
            em.persist(utilisateur);
            for (Produit produitListProduit : utilisateur.getProduitList()) {
                Utilisateur oldIdUtilisateurEnregOfProduitListProduit = produitListProduit.getIdUtilisateurEnreg();
                produitListProduit.setIdUtilisateurEnreg(utilisateur);
                produitListProduit = em.merge(produitListProduit);
                if (oldIdUtilisateurEnregOfProduitListProduit != null) {
                    oldIdUtilisateurEnregOfProduitListProduit.getProduitList().remove(produitListProduit);
                    oldIdUtilisateurEnregOfProduitListProduit = em.merge(oldIdUtilisateurEnregOfProduitListProduit);
                }
            }
            for (BonDeCommande bonDeCommandeListBonDeCommande : utilisateur.getBonDeCommandeList()) {
                Utilisateur oldIdGerantCreateurOfBonDeCommandeListBonDeCommande = bonDeCommandeListBonDeCommande.getIdGerantCreateur();
                bonDeCommandeListBonDeCommande.setIdGerantCreateur(utilisateur);
                bonDeCommandeListBonDeCommande = em.merge(bonDeCommandeListBonDeCommande);
                if (oldIdGerantCreateurOfBonDeCommandeListBonDeCommande != null) {
                    oldIdGerantCreateurOfBonDeCommandeListBonDeCommande.getBonDeCommandeList().remove(bonDeCommandeListBonDeCommande);
                    oldIdGerantCreateurOfBonDeCommandeListBonDeCommande = em.merge(oldIdGerantCreateurOfBonDeCommandeListBonDeCommande);
                }
            }
            for (ConsultationBonCommande consultationBonCommandeListConsultationBonCommande : utilisateur.getConsultationBonCommandeList()) {
                Utilisateur oldUtilisateurOfConsultationBonCommandeListConsultationBonCommande = consultationBonCommandeListConsultationBonCommande.getUtilisateur();
                consultationBonCommandeListConsultationBonCommande.setUtilisateur(utilisateur);
                consultationBonCommandeListConsultationBonCommande = em.merge(consultationBonCommandeListConsultationBonCommande);
                if (oldUtilisateurOfConsultationBonCommandeListConsultationBonCommande != null) {
                    oldUtilisateurOfConsultationBonCommandeListConsultationBonCommande.getConsultationBonCommandeList().remove(consultationBonCommandeListConsultationBonCommande);
                    oldUtilisateurOfConsultationBonCommandeListConsultationBonCommande = em.merge(oldUtilisateurOfConsultationBonCommandeListConsultationBonCommande);
                }
            }
            for (Echange echangeListEchange : utilisateur.getEchangeList()) {
                Utilisateur oldIdGerantOfEchangeListEchange = echangeListEchange.getIdGerant();
                echangeListEchange.setIdGerant(utilisateur);
                echangeListEchange = em.merge(echangeListEchange);
                if (oldIdGerantOfEchangeListEchange != null) {
                    oldIdGerantOfEchangeListEchange.getEchangeList().remove(echangeListEchange);
                    oldIdGerantOfEchangeListEchange = em.merge(oldIdGerantOfEchangeListEchange);
                }
            }
            for (Vente venteListVente : utilisateur.getVenteList()) {
                Utilisateur oldIdUtilisateurOfVenteListVente = venteListVente.getIdUtilisateur();
                venteListVente.setIdUtilisateur(utilisateur);
                venteListVente = em.merge(venteListVente);
                if (oldIdUtilisateurOfVenteListVente != null) {
                    oldIdUtilisateurOfVenteListVente.getVenteList().remove(venteListVente);
                    oldIdUtilisateurOfVenteListVente = em.merge(oldIdUtilisateurOfVenteListVente);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findUtilisateur(utilisateur.getIdUtilisateur()) != null) {
                throw new PreexistingEntityException("Utilisateur " + utilisateur + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Utilisateur utilisateur) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Utilisateur persistentUtilisateur = em.find(Utilisateur.class, utilisateur.getIdUtilisateur());
            List<Produit> produitListOld = persistentUtilisateur.getProduitList();
            List<Produit> produitListNew = utilisateur.getProduitList();
            List<BonDeCommande> bonDeCommandeListOld = persistentUtilisateur.getBonDeCommandeList();
            List<BonDeCommande> bonDeCommandeListNew = utilisateur.getBonDeCommandeList();
            List<ConsultationBonCommande> consultationBonCommandeListOld = persistentUtilisateur.getConsultationBonCommandeList();
            List<ConsultationBonCommande> consultationBonCommandeListNew = utilisateur.getConsultationBonCommandeList();
            List<Echange> echangeListOld = persistentUtilisateur.getEchangeList();
            List<Echange> echangeListNew = utilisateur.getEchangeList();
            List<Vente> venteListOld = persistentUtilisateur.getVenteList();
            List<Vente> venteListNew = utilisateur.getVenteList();
            List<String> illegalOrphanMessages = null;
            for (BonDeCommande bonDeCommandeListOldBonDeCommande : bonDeCommandeListOld) {
                if (!bonDeCommandeListNew.contains(bonDeCommandeListOldBonDeCommande)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain BonDeCommande " + bonDeCommandeListOldBonDeCommande + " since its idGerantCreateur field is not nullable.");
                }
            }
            for (ConsultationBonCommande consultationBonCommandeListOldConsultationBonCommande : consultationBonCommandeListOld) {
                if (!consultationBonCommandeListNew.contains(consultationBonCommandeListOldConsultationBonCommande)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ConsultationBonCommande " + consultationBonCommandeListOldConsultationBonCommande + " since its utilisateur field is not nullable.");
                }
            }
            for (Echange echangeListOldEchange : echangeListOld) {
                if (!echangeListNew.contains(echangeListOldEchange)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Echange " + echangeListOldEchange + " since its idGerant field is not nullable.");
                }
            }
            for (Vente venteListOldVente : venteListOld) {
                if (!venteListNew.contains(venteListOldVente)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Vente " + venteListOldVente + " since its idUtilisateur field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Produit> attachedProduitListNew = new ArrayList<Produit>();
            for (Produit produitListNewProduitToAttach : produitListNew) {
                produitListNewProduitToAttach = em.getReference(produitListNewProduitToAttach.getClass(), produitListNewProduitToAttach.getNLot());
                attachedProduitListNew.add(produitListNewProduitToAttach);
            }
            produitListNew = attachedProduitListNew;
            utilisateur.setProduitList(produitListNew);
            List<BonDeCommande> attachedBonDeCommandeListNew = new ArrayList<BonDeCommande>();
            for (BonDeCommande bonDeCommandeListNewBonDeCommandeToAttach : bonDeCommandeListNew) {
                bonDeCommandeListNewBonDeCommandeToAttach = em.getReference(bonDeCommandeListNewBonDeCommandeToAttach.getClass(), bonDeCommandeListNewBonDeCommandeToAttach.getIdBon());
                attachedBonDeCommandeListNew.add(bonDeCommandeListNewBonDeCommandeToAttach);
            }
            bonDeCommandeListNew = attachedBonDeCommandeListNew;
            utilisateur.setBonDeCommandeList(bonDeCommandeListNew);
            List<ConsultationBonCommande> attachedConsultationBonCommandeListNew = new ArrayList<ConsultationBonCommande>();
            for (ConsultationBonCommande consultationBonCommandeListNewConsultationBonCommandeToAttach : consultationBonCommandeListNew) {
                consultationBonCommandeListNewConsultationBonCommandeToAttach = em.getReference(consultationBonCommandeListNewConsultationBonCommandeToAttach.getClass(), consultationBonCommandeListNewConsultationBonCommandeToAttach.getConsultationBonCommandePK());
                attachedConsultationBonCommandeListNew.add(consultationBonCommandeListNewConsultationBonCommandeToAttach);
            }
            consultationBonCommandeListNew = attachedConsultationBonCommandeListNew;
            utilisateur.setConsultationBonCommandeList(consultationBonCommandeListNew);
            List<Echange> attachedEchangeListNew = new ArrayList<Echange>();
            for (Echange echangeListNewEchangeToAttach : echangeListNew) {
                echangeListNewEchangeToAttach = em.getReference(echangeListNewEchangeToAttach.getClass(), echangeListNewEchangeToAttach.getIdEchange());
                attachedEchangeListNew.add(echangeListNewEchangeToAttach);
            }
            echangeListNew = attachedEchangeListNew;
            utilisateur.setEchangeList(echangeListNew);
            List<Vente> attachedVenteListNew = new ArrayList<Vente>();
            for (Vente venteListNewVenteToAttach : venteListNew) {
                venteListNewVenteToAttach = em.getReference(venteListNewVenteToAttach.getClass(), venteListNewVenteToAttach.getIdV());
                attachedVenteListNew.add(venteListNewVenteToAttach);
            }
            venteListNew = attachedVenteListNew;
            utilisateur.setVenteList(venteListNew);
            utilisateur = em.merge(utilisateur);
            for (Produit produitListOldProduit : produitListOld) {
                if (!produitListNew.contains(produitListOldProduit)) {
                    produitListOldProduit.setIdUtilisateurEnreg(null);
                    produitListOldProduit = em.merge(produitListOldProduit);
                }
            }
            for (Produit produitListNewProduit : produitListNew) {
                if (!produitListOld.contains(produitListNewProduit)) {
                    Utilisateur oldIdUtilisateurEnregOfProduitListNewProduit = produitListNewProduit.getIdUtilisateurEnreg();
                    produitListNewProduit.setIdUtilisateurEnreg(utilisateur);
                    produitListNewProduit = em.merge(produitListNewProduit);
                    if (oldIdUtilisateurEnregOfProduitListNewProduit != null && !oldIdUtilisateurEnregOfProduitListNewProduit.equals(utilisateur)) {
                        oldIdUtilisateurEnregOfProduitListNewProduit.getProduitList().remove(produitListNewProduit);
                        oldIdUtilisateurEnregOfProduitListNewProduit = em.merge(oldIdUtilisateurEnregOfProduitListNewProduit);
                    }
                }
            }
            for (BonDeCommande bonDeCommandeListNewBonDeCommande : bonDeCommandeListNew) {
                if (!bonDeCommandeListOld.contains(bonDeCommandeListNewBonDeCommande)) {
                    Utilisateur oldIdGerantCreateurOfBonDeCommandeListNewBonDeCommande = bonDeCommandeListNewBonDeCommande.getIdGerantCreateur();
                    bonDeCommandeListNewBonDeCommande.setIdGerantCreateur(utilisateur);
                    bonDeCommandeListNewBonDeCommande = em.merge(bonDeCommandeListNewBonDeCommande);
                    if (oldIdGerantCreateurOfBonDeCommandeListNewBonDeCommande != null && !oldIdGerantCreateurOfBonDeCommandeListNewBonDeCommande.equals(utilisateur)) {
                        oldIdGerantCreateurOfBonDeCommandeListNewBonDeCommande.getBonDeCommandeList().remove(bonDeCommandeListNewBonDeCommande);
                        oldIdGerantCreateurOfBonDeCommandeListNewBonDeCommande = em.merge(oldIdGerantCreateurOfBonDeCommandeListNewBonDeCommande);
                    }
                }
            }
            for (ConsultationBonCommande consultationBonCommandeListNewConsultationBonCommande : consultationBonCommandeListNew) {
                if (!consultationBonCommandeListOld.contains(consultationBonCommandeListNewConsultationBonCommande)) {
                    Utilisateur oldUtilisateurOfConsultationBonCommandeListNewConsultationBonCommande = consultationBonCommandeListNewConsultationBonCommande.getUtilisateur();
                    consultationBonCommandeListNewConsultationBonCommande.setUtilisateur(utilisateur);
                    consultationBonCommandeListNewConsultationBonCommande = em.merge(consultationBonCommandeListNewConsultationBonCommande);
                    if (oldUtilisateurOfConsultationBonCommandeListNewConsultationBonCommande != null && !oldUtilisateurOfConsultationBonCommandeListNewConsultationBonCommande.equals(utilisateur)) {
                        oldUtilisateurOfConsultationBonCommandeListNewConsultationBonCommande.getConsultationBonCommandeList().remove(consultationBonCommandeListNewConsultationBonCommande);
                        oldUtilisateurOfConsultationBonCommandeListNewConsultationBonCommande = em.merge(oldUtilisateurOfConsultationBonCommandeListNewConsultationBonCommande);
                    }
                }
            }
            for (Echange echangeListNewEchange : echangeListNew) {
                if (!echangeListOld.contains(echangeListNewEchange)) {
                    Utilisateur oldIdGerantOfEchangeListNewEchange = echangeListNewEchange.getIdGerant();
                    echangeListNewEchange.setIdGerant(utilisateur);
                    echangeListNewEchange = em.merge(echangeListNewEchange);
                    if (oldIdGerantOfEchangeListNewEchange != null && !oldIdGerantOfEchangeListNewEchange.equals(utilisateur)) {
                        oldIdGerantOfEchangeListNewEchange.getEchangeList().remove(echangeListNewEchange);
                        oldIdGerantOfEchangeListNewEchange = em.merge(oldIdGerantOfEchangeListNewEchange);
                    }
                }
            }
            for (Vente venteListNewVente : venteListNew) {
                if (!venteListOld.contains(venteListNewVente)) {
                    Utilisateur oldIdUtilisateurOfVenteListNewVente = venteListNewVente.getIdUtilisateur();
                    venteListNewVente.setIdUtilisateur(utilisateur);
                    venteListNewVente = em.merge(venteListNewVente);
                    if (oldIdUtilisateurOfVenteListNewVente != null && !oldIdUtilisateurOfVenteListNewVente.equals(utilisateur)) {
                        oldIdUtilisateurOfVenteListNewVente.getVenteList().remove(venteListNewVente);
                        oldIdUtilisateurOfVenteListNewVente = em.merge(oldIdUtilisateurOfVenteListNewVente);
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
                String id = utilisateur.getIdUtilisateur();
                if (findUtilisateur(id) == null) {
                    throw new NonexistentEntityException("The utilisateur with id " + id + " no longer exists.");
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
            Utilisateur utilisateur;
            try {
                utilisateur = em.getReference(Utilisateur.class, id);
                utilisateur.getIdUtilisateur();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The utilisateur with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<BonDeCommande> bonDeCommandeListOrphanCheck = utilisateur.getBonDeCommandeList();
            for (BonDeCommande bonDeCommandeListOrphanCheckBonDeCommande : bonDeCommandeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Utilisateur (" + utilisateur + ") cannot be destroyed since the BonDeCommande " + bonDeCommandeListOrphanCheckBonDeCommande + " in its bonDeCommandeList field has a non-nullable idGerantCreateur field.");
            }
            List<ConsultationBonCommande> consultationBonCommandeListOrphanCheck = utilisateur.getConsultationBonCommandeList();
            for (ConsultationBonCommande consultationBonCommandeListOrphanCheckConsultationBonCommande : consultationBonCommandeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Utilisateur (" + utilisateur + ") cannot be destroyed since the ConsultationBonCommande " + consultationBonCommandeListOrphanCheckConsultationBonCommande + " in its consultationBonCommandeList field has a non-nullable utilisateur field.");
            }
            List<Echange> echangeListOrphanCheck = utilisateur.getEchangeList();
            for (Echange echangeListOrphanCheckEchange : echangeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Utilisateur (" + utilisateur + ") cannot be destroyed since the Echange " + echangeListOrphanCheckEchange + " in its echangeList field has a non-nullable idGerant field.");
            }
            List<Vente> venteListOrphanCheck = utilisateur.getVenteList();
            for (Vente venteListOrphanCheckVente : venteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Utilisateur (" + utilisateur + ") cannot be destroyed since the Vente " + venteListOrphanCheckVente + " in its venteList field has a non-nullable idUtilisateur field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Produit> produitList = utilisateur.getProduitList();
            for (Produit produitListProduit : produitList) {
                produitListProduit.setIdUtilisateurEnreg(null);
                produitListProduit = em.merge(produitListProduit);
            }
            em.remove(utilisateur);
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

    public List<Utilisateur> findUtilisateurEntities() {
        return findUtilisateurEntities(true, -1, -1);
    }

    public List<Utilisateur> findUtilisateurEntities(int maxResults, int firstResult) {
        return findUtilisateurEntities(false, maxResults, firstResult);
    }

    private List<Utilisateur> findUtilisateurEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Utilisateur.class));
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

    public Utilisateur findUtilisateur(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Utilisateur.class, id);
        } finally {
            em.close();
        }
    }

    public int getUtilisateurCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Utilisateur> rt = cq.from(Utilisateur.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
