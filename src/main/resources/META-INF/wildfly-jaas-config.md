# Configuration de référence JAAS / Elytron (WildFly) avec PostgreSQL

Pour activer l'authentification conteneur JAAS / Elytron dans WildFly pour `projectPHARMA`, voici la configuration JDBC de référence correspondant au schéma de la table `utilisateur`.

## 1. Requêtes SQL pour le Realm JDBC

- **Requête d'authentification (mot de passe en clair)** :
  ```sql
  SELECT mot_de_passe FROM utilisateur WHERE id_utilisateur = ?
  ```

- **Requête de mapping des rôles (`GERANT`, `EMPLOYE`)** :
  ```sql
  SELECT type_utilisateur, 'Roles' FROM utilisateur WHERE id_utilisateur = ?
  ```

## 2. Commandes JBoss CLI (WildFly)

Exécuter via `jboss-cli.sh --connect` :

```bash
# 1. Créer le JDBC Security Realm
/subsystem=elytron/jdbc-realm=pharmacie-realm:add(principal-query=[{data-source="PostgresDS",sql="SELECT mot_de_passe FROM utilisateur WHERE id_utilisateur = ?",clear-password-mapper={password-index=1}},{data-source="PostgresDS",sql="SELECT type_utilisateur, 'Roles' FROM utilisateur WHERE id_utilisateur = ?",attribute-mapping=[{index=1,to="Roles"}]}])

# 2. Créer le Security Domain
/subsystem=elytron/security-domain=pharmacie-domain:add(default-realm=pharmacie-realm,realms=[{realm=pharmacie-realm,role-decoder=groups-to-roles}],permission-mapper=default-permission-mapper)

# 3. Créer l'HTTP Authentication Factory
/subsystem=elytron/http-authentication-factory=pharmacie-http-auth:add(http-server-mechanism-factory=global,security-domain=pharmacie-domain,mechanism-configurations=[{mechanism-name=FORM}])

# 4. Associer au sous-système Undertow pour l'application
/subsystem=undertow/application-security-domain=pharmacie-domain:add(http-authentication-factory=pharmacie-http-auth)

# Recharger la configuration
:reload
```

## 3. Configuration dans l'application (`projectPHARMA`)
L'authentification est gérée de façon unifiée :
- Via `AuthBean` qui utilise `HttpServletRequest.login(username, password)`.
- Si le conteneur dispose du domaine Elytron ci-dessus, le principal JAAS et `isUserInRole()` sont directement alimentés.
- En environnement de test / développement sans realm configuré, `AuthBean` utilise directement `UtilisateurDAO.authentifier()` sans bloquer le développeur.
- Le filtre `AuthFilter` garantit la protection stricte de toutes les URLs et restreint les écrans d'administration au rôle `GERANT`.
