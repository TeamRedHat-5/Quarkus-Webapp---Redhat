package uk.ac.newcastle.enterprisemiddleware.infrastructure.exceptions;

import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.persistence.OptimisticLockException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

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

        if (exception instanceof RestServiceException) {
            RestServiceException restException = (RestServiceException) exception;
            status = restException.getStatus().getStatusCode();
            error = restException.getStatus().getReasonPhrase();
            message = restException.getMessage();
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
        } else if (hasOptimisticLockCause(exception)) {
            status = Response.Status.CONFLICT.getStatusCode();
            error = Response.Status.CONFLICT.getReasonPhrase();
            message = "The resource was modified by another request. Please reload and try again.";
        }

        ErrorResponseDTO responseBody = new ErrorResponseDTO(
                OffsetDateTime.now(ZoneOffset.UTC).toString(),
                status,
                error,
                message,
                uriInfo.getRequestUri().getPath()
        );

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(responseBody)
                .build();
    }

    private boolean hasOptimisticLockCause(Throwable exception) {
        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof OptimisticLockException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}