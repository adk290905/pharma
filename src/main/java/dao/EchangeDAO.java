package dao;

import entities.Echange;
import entities.LigneEchange;
import entities.LigneEchangePK;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * EJB Stateless pour la gestion des échanges inter-pharmacies et lignes d'échange
 * avec ajustement automatique des stocks (incrément si ENTRANT, décrément si SORTANT).
 */
@Stateless
public class EchangeDAO extends AbstractDAO<Echange> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    @EJB
    private ProduitDAO produitDAO;

    public EchangeDAO() {
        super(Echange.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Echange> findAllOrderByDateDesc() {
        return em.createQuery("SELECT e FROM Echange e ORDER BY e.dateEchange DESC", Echange.class)
                .getResultList();
    }

    public List<LigneEchange> findLignesByEchange(String idEchange) {
        return em.createQuery("SELECT l FROM LigneEchange l WHERE l.ligneEchangePK.idEchange = :idEchange", LigneEchange.class)
                .setParameter("idEchange", idEchange)
                .getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Echange finaliserEchange(Echange echange, List<LigneEchange> lignes) {
        if (lignes == null || lignes.isEmpty()) {
            throw new IllegalArgumentException("Impossible d'enregistrer un échange sans lignes.");
        }
        if (echange.getDateEchange() == null) {
            echange.setDateEchange(new Date());
        }

        em.persist(echange);

        boolean isEntrant = "ENTRANT".equalsIgnoreCase(echange.getTypeEchange())
                || "ENTREE".equalsIgnoreCase(echange.getTypeEchange())
                || "RECEPTION".equalsIgnoreCase(echange.getTypeEchange());

        List<LigneEchange> lignesPersistees = new ArrayList<>();
        for (LigneEchange ligne : lignes) {
            String nLot = ligne.getProduit().getNLot();
            ligne.setEchange(echange);
            ligne.setLigneEchangePK(new LigneEchangePK(nLot, echange.getIdEchange()));

            em.persist(ligne);
            lignesPersistees.add(ligne);

            // Si échange entrant : on augmente le stock. Si sortant : on diminue le stock.
            int delta = isEntrant ? ligne.getQuantite() : -ligne.getQuantite();
            produitDAO.adjustStock(nLot, delta);
        }

        echange.setLigneEchangeList(lignesPersistees);
        return echange;
    }
}
