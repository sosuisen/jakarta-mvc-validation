package com.example.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import com.example.auth.AuthConfig;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.BeforeDestroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;

@ApplicationScoped
public class DatabaseSetup {

	@Resource
	private DataSource ds;

	@Inject
	private Pbkdf2PasswordHash passwordHash;

	@Inject
	private AuthConfig authConfig;

	public void onStart(@Observes @Initialized(ApplicationScoped.class) Object event) {
		// Initialize a Payara Micro built-in H2 database

		passwordHash.initialize(authConfig.getHashAlgorithmParams());

		update(ds, "DROP TABLE IF EXISTS users");
		update(ds, "DROP TABLE IF EXISTS user_roles");
		update(ds, "DROP TABLE IF EXISTS messages");

		update(ds, """
				CREATE TABLE IF NOT EXISTS users(
					name VARCHAR(64) PRIMARY KEY,
					password VARCHAR(255) NOT NULL)
				 """);

		update(ds, """
				CREATE TABLE IF NOT EXISTS user_roles (
					name VARCHAR(64) NOT NULL,
					role VARCHAR(64) NOT NULL,
					PRIMARY KEY (name, role))					
				""");

		update(ds, """
				CREATE TABLE IF NOT EXISTS messages (
					id INT AUTO_INCREMENT PRIMARY KEY,
					name VARCHAR(64) NOT NULL,
					message VARCHAR(140) NOT NULL)
				""");

		// Initial password is "foo", which must be changed after first login
		update(ds,
				"INSERT INTO users VALUES('myuser', '" + passwordHash.generate("foo".toCharArray()) + "')");
		update(ds,
				"INSERT INTO users VALUES('myadmin', '" + passwordHash.generate("foo".toCharArray()) + "')");

		update(ds, "INSERT INTO user_roles VALUES('myadmin', 'ADMIN')");
		update(ds, "INSERT INTO user_roles VALUES('myadmin', 'USER')");

		update(ds, "INSERT INTO user_roles VALUES('myuser', 'USER')");

		try {
			System.out.println("### current datasource: " + ds.getConnection().getMetaData().getURL());
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	public void onApplicationScopedDestroyed(@Observes @BeforeDestroyed(ApplicationScoped.class) Object event) {		
		try {
			update(ds, "DROP TABLE IF EXISTS users");
			update(ds, "DROP TABLE IF EXISTS user_roles");
			update(ds, "DROP TABLE IF EXISTS messages");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void update(DataSource ds, String query) {
		try (
				Connection con = ds.getConnection();
				PreparedStatement pstmt = con.prepareStatement(query);) {
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}
}