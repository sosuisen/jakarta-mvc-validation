package com.example.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.model.Message;
import com.example.model.MessageDAO;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.RedirectScoped;
import jakarta.mvc.binding.BindingResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@RedirectScoped
@Getter
class FormResult implements Serializable {
	private final ArrayList<String> errors = new ArrayList<>();
	private final ArrayList<String> successes = new ArrayList<>();
	@Setter
	private Message input = new Message();
}

@Controller
@RequestScoped
@Slf4j
@PermitAll
@Path("/")
public class MessageController {
	@Inject
	private Models models;
	@Inject
	private MessageDAO messagesDAO;
	@Inject
	private HttpServletRequest req;
	@Inject
	private BindingResult bindingResult;
	@Inject
	private FormResult formResult;

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

	@GET
	@Path("logout")
	public String logout() {
		try {
			req.logout();
			req.getSession().invalidate();
		} catch (ServletException e) {
			log.warn("Cannot logout: " + e.getMessage());
		}
		return "redirect:/";
	}

	@GET
	@RolesAllowed("USER")
	@Path("messages")
	public String getMessages() throws SQLException {
		models.put("userName", req.getRemoteUser());
		models.put("isAdmin", req.isUserInRole("ADMIN"));
		models.put("messages", messagesDAO.getAll());
		models.put("successes", formResult.getSuccesses());
		models.put("errors", formResult.getErrors());
		models.put("input", formResult.getInput());
		return "messages.html";
	}

	@POST
	@RolesAllowed("USER")
	@Path("messages")
	public String postMessages(@Valid @BeanParam Message mes) throws SQLException {
		if (bindingResult.isFailed()) {
			formResult.getErrors().addAll(bindingResult.getAllMessages());
			formResult.setInput(mes);
			return "redirect:messages";
		}
		mes.setName(req.getRemoteUser());
		messagesDAO.create(mes);
		formResult.getSuccesses().add("Message posted successfully.");
		return "redirect:messages";
	}

	@GET
	@RolesAllowed("ADMIN")
	@Path("users")
	public String getUsers() {
		return "users.html";
	}

	@POST
	@RolesAllowed("ADMIN")
	@Path("clear")
	public String clearMessages() throws SQLException {
		messagesDAO.deleteAll();
		return "redirect:messages";
	}
}
