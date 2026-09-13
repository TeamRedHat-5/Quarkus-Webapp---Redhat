package uk.ac.newcastle.enterprisemiddleware.infrastructure.exceptions;

import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {
        int status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        String error = Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
        String message = "An unexpected error occurred while processing the request.";
        Map<String, String> reasons = new HashMap<>();

        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) exception;
            status = Response.Status.BAD_REQUEST.getStatusCode();
            error = Response.Status.BAD_REQUEST.getReasonPhrase();
            message = "Validation failed for one or more fields";
            for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                reasons.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
        } else if (exception instanceof RestServiceException) {
            RestServiceException restException = (RestServiceException) exception;
            status = restException.getStatus().getStatusCode();
            error = restException.getStatus().getReasonPhrase();
            message = restException.getMessage();
            reasons = restException.getReasons();
        } else if (exception instanceof WebApplicationException) {
            WebApplicationException webException = (WebApplicationException) exception;
            status = webException.getResponse().getStatus();

            Response.Status responseStatus = Response.Status.fromStatusCode(status);
            if (responseStatus != null) {
                error = responseStatus.getReasonPhrase();
                message = responseStatus.getReasonPhrase();
            } else {
                error = "Request Error";
                message = "The request could not be processed.";
            }
        }

        ErrorResponseDTO responseBody = new ErrorResponseDTO(
                OffsetDateTime.now(ZoneOffset.UTC).toString(),
                status,
                error,
                message,
                uriInfo.getRequestUri().getPath(),
                reasons
        );

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(responseBody)
                .build();
    }
}
