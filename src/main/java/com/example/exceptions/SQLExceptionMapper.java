package com.example.exceptions;

import java.sql.SQLException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class SQLExceptionMapper implements ExceptionMapper<SQLException> {
	@Override
	public Response toResponse(SQLException e) {
		log.error("SQLException: " + e.getMessage());
		// Show a generic error message to the user
		throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		// If you want to show a custom message, do the following:
		// return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
		// 		.entity("Internal server error occurred")
		// 		.build();		
	}
}
