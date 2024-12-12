package com.sharok.esquela.exception;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final Environment environment;

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex) {
        return problemDetail(ex.getEntity() + " not found", HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleUnknownError(InvalidTokenException ex) {
        return problemDetail(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    private ProblemDetail problemDetail(String title, HttpStatus status, Throwable ex) {
        log.error(title, ex);
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(status, Objects.isNull(ex) ? "" : ex.getMessage());
        problemDetail.setTitle(title);
        if (!isProdProfile()) {
            problemDetail.setProperties(
                    Map.of(
                            "stackTrace",
                            Arrays.stream(ex.getStackTrace())
                                    .map(StackTraceElement::toString)
                                    .collect(Collectors.toList())));
        }
        return problemDetail;
    }

    private boolean isProdProfile() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }
}
