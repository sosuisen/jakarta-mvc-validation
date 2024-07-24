package com.example;

import java.util.HashMap;
import java.util.Map;

import jakarta.mvc.security.Csrf;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/mvc")
public class MyApplication extends Application {
    @Override
	public Map<String, Object> getProperties() {
		final Map<String, Object> map = new HashMap<>();
		map.put(Csrf.CSRF_PROTECTION, Csrf.CsrfOptions.IMPLICIT);
		return map;
	}	
}
