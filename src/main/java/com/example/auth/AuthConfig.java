package com.example.auth;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.LoginToContinue;
import jakarta.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;

@DeclareRoles({"ADMIN", "USER"})
@FormAuthenticationMechanismDefinition(
    loginToContinue = @LoginToContinue(
        loginPage="/login",
        errorPage="/login?error=invalid_user",
        useForwardToLogin = true
    )
)
@DatabaseIdentityStoreDefinition(
		dataSourceLookup = "jdbc/__default",
		callerQuery = "select password from caller where name = ?",
		groupsQuery = "select group_name from caller_groups where caller_name = ?",
		hashAlgorithmParameters = {
				"Pbkdf2PasswordHash.Iterations=210000",
				"Pbkdf2PasswordHash.Algorithm=PBKDF2WithHmacSHA512",
				"Pbkdf2PasswordHash.SaltSizeBytes=32"
				})
@ApplicationScoped
public class AuthConfig {
	public Map<String, String> getHashAlgorithmParams() {
		String[] params = AuthConfig.class.getAnnotation(DatabaseIdentityStoreDefinition.class).hashAlgorithmParameters();
		var hashParams = new HashMap<String, String>();
		for (var param : params) {
			var pair = param.split("=");
			hashParams.put(pair[0], pair[1]);
		}
		return hashParams;
	}
}