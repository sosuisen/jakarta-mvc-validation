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
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
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

		update(ds, "DROP TABLE IF EXISTS caller");
		update(ds, "DROP TABLE IF EXISTS caller_groups");
		update(ds, "DROP TABLE IF EXISTS messages");

		update(ds, """
				CREATE TABLE IF NOT EXISTS caller(
					name VARCHAR(64) PRIMARY KEY,
					password VARCHAR(255) NOT NULL)
				 """);

		update(ds, """
				CREATE TABLE IF NOT EXISTS caller_groups (
					caller_name VARCHAR(64) NOT NULL,
					group_name VARCHAR(64) NOT NULL,
					PRIMARY KEY (caller_name, group_name))
				""");

		update(ds, """
				CREATE TABLE IF NOT EXISTS messages (
					id INT AUTO_INCREMENT PRIMARY KEY,
					name VARCHAR(64) NOT NULL,
					title VARCHAR(70) NOT NULL,
					body VARCHAR(400) NOT NULL)
				""");

		// Initial password is "foo", which must be changed after first login
		update(ds,
				"INSERT INTO caller VALUES('myuser', '" + passwordHash.generate("foo".toCharArray()) + "')");
		update(ds,
				"INSERT INTO caller VALUES('myadmin', '" + passwordHash.generate("foo".toCharArray()) + "')");

		update(ds, "INSERT INTO caller_groups VALUES('myadmin', 'ADMIN')");
		update(ds, "INSERT INTO caller_groups VALUES('myadmin', 'USER')");

		update(ds, "INSERT INTO caller_groups VALUES('myuser', 'USER')");

		try {
			log.info("Current datasource: " + ds.getConnection().getMetaData().getURL());
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	public void onApplicationScopedDestroyed(@Observes @BeforeDestroyed(ApplicationScoped.class) Object event) {		
		try {
			update(ds, "DROP TABLE IF EXISTS caller");
			update(ds, "DROP TABLE IF EXISTS caller_groups");
			update(ds, "DROP TABLE IF EXISTS messages");
		} catch (Exception e) {
			log.warn("Exception on drop tables: " + e.getMessage());
		}
	}

	private void update(DataSource ds, String query) {
		try (
				Connection con = ds.getConnection();
				PreparedStatement pstmt = con.prepareStatement(query);) {
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
}