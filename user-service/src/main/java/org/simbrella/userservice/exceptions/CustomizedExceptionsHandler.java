package org.simbrella.userservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestController
@ControllerAdvice
public class CustomizedExceptionsHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(LoanTechException.class)
    public final ResponseEntity<Object> handleGlobalException(LoanTechException loanTechException, final WebRequest request) {
        HttpStatus status = determineHttpStatus(loanTechException);
        final ExceptionResponse exceptionResponse = new ExceptionResponse().builder()
                .error(status.getReasonPhrase())
                .path(request.getDescription(false))
                .message(loanTechException.getMessage())
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(exceptionResponse, status);
    }

    private HttpStatus determineHttpStatus(LoanTechException exception) {
        if (exception instanceof NotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (exception instanceof InvalidDetailsException) {
            return HttpStatus.BAD_REQUEST;
        } else if (exception instanceof UserExistsException) {
            return HttpStatus.CONFLICT;
        } else if (exception instanceof AuthenticationFailedException) {
            return HttpStatus.UNAUTHORIZED;
        }

        return HttpStatus.BAD_REQUEST;
    }

}
