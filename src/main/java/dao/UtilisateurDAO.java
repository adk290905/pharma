package dao;

import entities.Utilisateur;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 * EJB Stateless pour la gestion des utilisateurs et l'authentification
 */
@Stateless
public class UtilisateurDAO extends AbstractDAO<Utilisateur> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public UtilisateurDAO() {
        super(Utilisateur.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Authentification par identifiant et mot de passe en clair (selon choix du projet)
     */
    public Utilisateur authentifier(String idUtilisateur, String motDePasse) {
        if (idUtilisateur == null || motDePasse == null) {
            return null;
        }
        try {
            return em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE LOWER(TRIM(u.idUtilisateur)) = LOWER(TRIM(:login)) AND TRIM(u.motDePasse) = TRIM(:pass)",
                    Utilisateur.class)
                    .setParameter("login", idUtilisateur.trim())
                    .setParameter("pass", motDePasse.trim())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Recherche par type / rôle (GERANT ou EMPLOYE)
     */
    public List<Utilisateur> findByType(String typeUtilisateur) {
        return em.createQuery(
                "SELECT u FROM Utilisateur u WHERE UPPER(u.typeUtilisateur) = :type ORDER BY u.nom ASC",
                Utilisateur.class)
                .setParameter("type", typeUtilisateur.toUpperCase())
                .getResultList();
    }
}
