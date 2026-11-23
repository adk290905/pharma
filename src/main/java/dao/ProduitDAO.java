package dao;

import entities.Produit;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

/**
 * EJB Stateless gérant l'accès aux données des produits (médicaments)
 */
@Stateless
public class ProduitDAO extends AbstractDAO<Produit> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public ProduitDAO() {
        super(Produit.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Recherche de produits par nom commercial ou numéro de lot
     */
    public List<Produit> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }
        String pattern = "%" + query.trim().toLowerCase() + "%";
        return em.createQuery(
                "SELECT p FROM Produit p WHERE LOWER(p.nomC) LIKE :pattern OR LOWER(p.nLot) LIKE :pattern ORDER BY p.nomC ASC",
                Produit.class)
                .setParameter("pattern", pattern)
                .getResultList();
    }

    /**
     * Produits en stock faible (quantité > 0 et <= seuil)
     */
    public List<Produit> findLowStock(int threshold) {
        return em.createQuery(
                "SELECT p FROM Produit p WHERE p.qte > 0 AND p.qte <= :threshold ORDER BY p.qte ASC",
                Produit.class)
                .setParameter("threshold", threshold)
                .getResultList();
    }

    /**
     * Nombre de produits en rupture de stock (quantité <= 0)
     */
    public int countOutOfStock() {
        Long count = em.createQuery(
                "SELECT COUNT(p) FROM Produit p WHERE p.qte <= 0", Long.class)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    /**
     * Nombre de produits en stock faible
     */
    public int countLowStock(int threshold) {
        Long count = em.createQuery(
                "SELECT COUNT(p) FROM Produit p WHERE p.qte > 0 AND p.qte <= :threshold", Long.class)
                .setParameter("threshold", threshold)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    /**
     * Produits approchant ou ayant dépassé la date de péremption
     */
    public List<Produit> findExpiringBefore(Date date) {
        return em.createQuery(
                "SELECT p FROM Produit p WHERE p.dateExp <= :date ORDER BY p.dateExp ASC",
                Produit.class)
                .setParameter("date", date)
                .getResultList();
    }

    /**
     * Mise à jour atomique du stock (+delta pour approvisionnement/échange entrant, -delta pour vente/échange sortant)
     */
    public void adjustStock(String nLot, int delta) {
        Produit p = find(nLot);
        if (p == null) {
            throw new IllegalArgumentException("Produit introuvable avec le lot: " + nLot);
        }
        int newQte = p.getQte() + delta;
        if (newQte < 0) {
            throw new IllegalStateException("Stock insuffisant pour le produit " + p.getNomC() + " (lot: " + nLot + "). Stock actuel: " + p.getQte() + ", demandé: " + (-delta));
        }
        p.setQte(newQte);
        edit(p);
    }
}
