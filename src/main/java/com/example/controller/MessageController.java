package com.example.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.model.MessageDAO;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.RedirectScoped;
import jakarta.mvc.binding.BindingResult;
import jakarta.mvc.binding.MvcBinding;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;


@RedirectScoped
class ResultMessage implements Serializable {
	private ArrayList<String> errors = new ArrayList<>();

	public ArrayList<String> getErrors() {
		return errors;
	}
}

@Controller
@RequestScoped
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
	private ResultMessage resultMessage;

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
			e.printStackTrace();
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
		models.put("errors", resultMessage.getErrors());
		return "messages.html";
	}

	@POST
	@RolesAllowed("USER")
	@Path("messages")
	public String postMessages(
			@Valid
			@MvcBinding
			@NotBlank(message = "{message.NotBlank}")
			@Size(max = 140, message = "{message.Size}")
			@FormParam("message")
			String mes
		) throws SQLException {

		if (bindingResult.isFailed()) {
			resultMessage.getErrors().addAll(bindingResult.getAllMessages());
			return "redirect:messages";
		}
		messagesDAO.create(req.getRemoteUser(), mes);
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
