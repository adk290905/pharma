package dao;

import entities.Produit;
import entities.Utilisateur;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Date;

/**
 * EJB Singleton initialisé au démarrage pour garantir la présence des comptes et données de base
 */
@Singleton
@Startup
public class DataInitializerEJB {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    @PostConstruct
    public void init() {
        try {
            Long userCount = em.createQuery("SELECT COUNT(u) FROM Utilisateur u", Long.class).getSingleResult();
            if (userCount == null || userCount == 0) {
                Utilisateur gerant = new Utilisateur("gerant1", "Direction", "Gérant", "admin123", "GERANT");
                Utilisateur emp = new Utilisateur("emp1", "Mansouri", "Amine", "emp123", "EMPLOYE");
                em.persist(gerant);
                em.persist(emp);
                em.flush();
                System.out.println(">>> [PharmaGes] Initialisation automatique : Comptes gerant1 et emp1 créés.");

                Long prodCount = em.createQuery("SELECT COUNT(p) FROM Produit p", Long.class).getSingleResult();
                if (prodCount == null || prodCount == 0) {
                    Produit p1 = new Produit("LOT-PAR-01", "Paracétamol 500mg", 50, new BigDecimal("180.00"), new Date(System.currentTimeMillis() + 86400000L * 365));
                    p1.setDosage("500 mg");
                    p1.setConditionnement("Boîte de 20 comprimés");
                    p1.setIdUtilisateurEnreg(gerant);

                    Produit p2 = new Produit("LOT-AMX-02", "Amoxicilline 1g", 15, new BigDecimal("450.00"), new Date(System.currentTimeMillis() + 86400000L * 250));
                    p2.setDosage("1 g");
                    p2.setConditionnement("Boîte de 14 comprimés");
                    p2.setIdUtilisateurEnreg(gerant);

                    Produit p3 = new Produit("LOT-VIT-03", "Vitamine C 1000mg", 4, new BigDecimal("320.00"), new Date(System.currentTimeMillis() + 86400000L * 180));
                    p3.setDosage("1000 mg");
                    p3.setConditionnement("Tube de 20 effervescents");
                    p3.setIdUtilisateurEnreg(gerant);

                    em.persist(p1);
                    em.persist(p2);
                    em.persist(p3);
                    System.out.println(">>> [PharmaGes] Initialisation automatique : Médicaments de test créés.");
                }
            }
        } catch (Exception e) {
            System.err.println(">>> [PharmaGes] Note initialisation : " + e.getMessage());
        }
    }
}
