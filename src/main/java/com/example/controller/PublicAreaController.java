package com.example.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@Controller
@RequestScoped
@Path("/")
public class PublicAreaController {
	@Inject
	private Models models;

	@GET
	public String home() {
		models.put("appName", "Message Board");
		return "index.html";
	}

	@GET
	@Path("login")
	public String login(@QueryParam("error") final String error) {
		models.put("error", error);
		return "login.html";
	}
}
