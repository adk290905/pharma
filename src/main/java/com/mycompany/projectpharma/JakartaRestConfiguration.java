package com.mycompany.projectpharma;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configures Jakarta RESTful Web Services and PostgreSQL DataSource for the application.
 * @author Juneau
 */
@DataSourceDefinition(
    name = "java:app/datasources/PostgresDS",
    className = "org.postgresql.ds.PGSimpleDataSource",
    serverName = "localhost",
    portNumber = 5432,
    databaseName = "pharmacie_db",
    user = "postgres",
    password = "Ruben@dk290905"
)
@ApplicationPath("resources")
public class JakartaRestConfiguration extends Application {
    
}
