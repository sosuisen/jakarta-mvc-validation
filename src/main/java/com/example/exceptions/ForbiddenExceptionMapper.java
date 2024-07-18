package com.example.exceptions;

import java.net.URI;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {
	@Inject
	private HttpServletRequest req;

	@Override
	public Response toResponse(ForbiddenException exception) {
		try {
			req.logout();
			req.getSession().invalidate();
		} catch (ServletException e) {
			e.printStackTrace();
		}

		// Use Response.seeOther() to redirect and display the login screen.
		if(req.getMethod().equals("GET")){
			// Redirecting to the forbidden request URL is always interrupted by the login screen.
			return Response.seeOther(URI.create(req.getRequestURL().toString() + "?error=forbidden")).build();
		}
		else {
			// Since redirects cannot be made for any requests other than GET,
			// The redirect destination will be forcibly set to the main screen.
			return Response.seeOther(URI.create(req.getContextPath() + "/app/messages?error=forbidden")).build();			
		}
	}
}
