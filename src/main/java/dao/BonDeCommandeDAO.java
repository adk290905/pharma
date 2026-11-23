package dao;

import entities.BonDeCommande;
import entities.ConsultationBonCommande;
import entities.ConsultationBonCommandePK;
import entities.Utilisateur;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

/**
 * EJB Stateless pour la gestion des bons de commande et traçabilité des consultations
 */
@Stateless
public class BonDeCommandeDAO extends AbstractDAO<BonDeCommande> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public BonDeCommandeDAO() {
        super(BonDeCommande.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<BonDeCommande> findAllOrderByDateDesc() {
        return em.createQuery("SELECT b FROM BonDeCommande b ORDER BY b.dateBon DESC", BonDeCommande.class)
                .getResultList();
    }

    /**
     * Enregistre une consultation de bon de commande par un utilisateur
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void enregistrerConsultation(String idUtilisateur, String idBon) {
        Utilisateur user = em.find(Utilisateur.class, idUtilisateur);
        BonDeCommande bon = em.find(BonDeCommande.class, idBon);
        if (user != null && bon != null) {
            ConsultationBonCommande consultation = new ConsultationBonCommande();
            Date now = new Date();
            consultation.setConsultationBonCommandePK(new ConsultationBonCommandePK(idUtilisateur, idBon, now));
            consultation.setUtilisateur(user);
            consultation.setBonDeCommande(bon);
            em.persist(consultation);
        }
    }

    public List<ConsultationBonCommande> findConsultationsByBon(String idBon) {
        return em.createQuery(
                "SELECT c FROM ConsultationBonCommande c WHERE c.consultationBonCommandePK.idBon = :idBon ORDER BY c.consultationBonCommandePK.dateConsultation DESC",
                ConsultationBonCommande.class)
                .setParameter("idBon", idBon)
                .getResultList();
    }
}
