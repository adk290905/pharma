package dao;

import entities.LigneVente;
import entities.LigneVentePK;
import entities.Produit;
import entities.Vente;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * EJB Stateless pour la gestion des ventes et lignes de vente avec décrémentation automatique du stock
 */
@Stateless
public class VenteDAO extends AbstractDAO<Vente> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    @EJB
    private ProduitDAO produitDAO;

    public VenteDAO() {
        super(Vente.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Liste toutes les ventes ordonnées par date décroissante
     */
    public List<Vente> findAllOrderByDateDesc() {
        return em.createQuery("SELECT v FROM Vente v ORDER BY v.dateVente DESC", Vente.class)
                .getResultList();
    }

    /**
     * Récupère les lignes d'une vente donnée
     */
    public List<LigneVente> findLignesByVente(String idV) {
        return em.createQuery("SELECT l FROM LigneVente l WHERE l.ligneVentePK.idV = :idV", LigneVente.class)
                .setParameter("idV", idV)
                .getResultList();
    }

    /**
     * Enregistre une vente complète (entête vente + lignes de vente)
     * et met à jour automatiquement le stock des produits vendus.
     * Transaction gérée par le conteneur (tout réussit ou tout est annulé).
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Vente finaliserVente(Vente vente, List<LigneVente> lignes) {
        if (lignes == null || lignes.isEmpty()) {
            throw new IllegalArgumentException("Impossible d'enregistrer une vente sans aucune ligne de vente.");
        }

        // Calcul du total général et validation
        BigDecimal totalCalcule = BigDecimal.ZERO;
        int totalQuantite = 0;
        StringBuilder nomsProduits = new StringBuilder();

        for (LigneVente ligne : lignes) {
            BigDecimal sousTotal = ligne.getPrixUnitaire().multiply(BigDecimal.valueOf(ligne.getQuantite()));
            totalCalcule = totalCalcule.add(sousTotal);
            totalQuantite += ligne.getQuantite();

            if (ligne.getProduit() != null) {
                if (nomsProduits.length() > 0) nomsProduits.append(", ");
                nomsProduits.append(ligne.getProduit().getNomC());
            }
        }

        if (vente.getTotal() == null || vente.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            vente.setTotal(totalCalcule);
        }
        vente.setQte(totalQuantite);
        if (vente.getPpa() == null) {
            vente.setPpa(totalCalcule);
        }
        if (vente.getNomC() == null || vente.getNomC().trim().isEmpty()) {
            String resume = nomsProduits.toString();
            if (resume.length() > 150) {
                resume = resume.substring(0, 147) + "...";
            }
            vente.setNomC(resume.isEmpty() ? "Vente comptoir" : resume);
        }
        if (vente.getDateVente() == null) {
            vente.setDateVente(new Date());
        }

        // Persistance de l'entête vente
        em.persist(vente);

        // Persistance des lignes de vente et mise à jour des stocks
        List<LigneVente> lignesPersistees = new ArrayList<>();
        for (LigneVente ligne : lignes) {
            String nLot = ligne.getProduit().getNLot();
            ligne.setVente(vente);
            ligne.setLigneVentePK(new LigneVentePK(nLot, vente.getIdV()));

            em.persist(ligne);
            lignesPersistees.add(ligne);

            // Décrémenter le stock du produit correspondant
            produitDAO.adjustStock(nLot, -ligne.getQuantite());
        }

        vente.setLigneVenteList(lignesPersistees);
        return vente;
    }

    /**
     * Calcul du chiffre d'affaires total
     */
    public BigDecimal calculerChiffreAffairesTotal() {
        BigDecimal total = em.createQuery("SELECT SUM(v.total) FROM Vente v", BigDecimal.class)
                .getSingleResult();
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Total encaissé aujourd'hui
     */
    public BigDecimal calculerVentesAujourdhui() {
        java.time.LocalDate today = java.time.LocalDate.now();
        Date debutJour = java.sql.Date.valueOf(today);
        Date finJour = java.sql.Date.valueOf(today.plusDays(1));

        BigDecimal total = em.createQuery(
                "SELECT SUM(v.total) FROM Vente v WHERE v.dateVente >= :debut AND v.dateVente < :fin", BigDecimal.class)
                .setParameter("debut", debutJour)
                .setParameter("fin", finJour)
                .getSingleResult();
        return total != null ? total : BigDecimal.ZERO;
    }
}
