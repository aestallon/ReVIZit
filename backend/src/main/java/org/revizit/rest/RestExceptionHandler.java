package org.revizit.rest;

import java.time.OffsetDateTime;
import org.revizit.rest.model.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class RestExceptionHandler {

  private static ResponseEntity<ApiError> errorOf(final HttpStatus httpStatus,
                                                  final String message,
                                                  final WebRequest request) {
    return new ResponseEntity<>(
        new ApiError()
            .status(httpStatus.value())
            .timestamp(OffsetDateTime.now())
            .message(message)
            .description(request.getDescription(false)),
        httpStatus);
  }

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ApiError> nullPointerException(final NullPointerException e,
                                                       final WebRequest request) {
    return errorOf(HttpStatus.INTERNAL_SERVER_ERROR, "Oops, something went awry!", request);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiError> illegalStateException(final IllegalStateException e,
                                                        final WebRequest request) {
    return errorOf(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> illegalArgumentException(final IllegalArgumentException e,
                                                           final WebRequest request) {
    return errorOf(HttpStatus.BAD_REQUEST, e.getMessage(), request);
  }


//  @ExceptionHandler(NotFoundException.class)
//  public ResponseEntity<ApiError> notFoundException(final NotFoundException e,
//                                                    final WebRequest request) {
//    return errorOf(HttpStatus.NOT_FOUND, e.getMessage(), request);
//  }


}
