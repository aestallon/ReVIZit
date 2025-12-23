package org.revizit.rest.api;

import org.revizit.rest.model.AuthenticationRequest;
import org.revizit.rest.model.AuthenticationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

/**
 * A delegate to be called by the {@link AuthApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")
public interface AuthApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /auth : Checks for authentication.
     * Pings the server with the authentication token included as a bearer token to check for its validity. 
     *
     * @return Ok (status code 200)
     *         or Unauthorized (status code 403)
     * @see AuthApi#isAuthenticated
     */
    default ResponseEntity<Void> isAuthenticated() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /auth/login : Attempts authentication.
     * Submits an username-password authentication request for login purposes. 
     *
     * @param authenticationRequest  (required)
     * @return Ok (status code 200)
     * @see AuthApi#login
     */
    default ResponseEntity<AuthenticationResponse> login(AuthenticationRequest authenticationRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"token\" : \"token\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
