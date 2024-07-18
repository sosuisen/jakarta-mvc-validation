package com.example.exceptions;

import java.sql.SQLException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SQLExceptionMapper implements ExceptionMapper<SQLException> {
	@Override
	public Response toResponse(SQLException exception) {
		throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
	}
}
