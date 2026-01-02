package org.revizit.rest.api;

import org.revizit.rest.model.ApiError;
import org.springframework.lang.Nullable;
import org.revizit.rest.model.Profile;
import org.revizit.rest.model.UserSelector;
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
 * A delegate to be called by the {@link UserManagementApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")
public interface UserManagementApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /umgmt : Creates users by standard file upload
     * Creates users by standard file upload.
     *
     * @param file  (optional)
     * @return Created (status code 201)
     *         or Bad request (status code 400)
     * @see UserManagementApi#createUsers
     */
    default ResponseEntity<Void> createUsers(MultipartFile file) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"description\" : \"description\", \"message\" : \"message\", \"status\" : 0, \"timestamp\" : \"2000-01-23T04:56:07.000+00:00\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * DELETE /umgmt : Deletes a user
     * Deletes a user.
     *
     * @param userSelector  (required)
     * @return Ok (status code 200)
     * @see UserManagementApi#deleteUser
     */
    default ResponseEntity<Void> deleteUser(UserSelector userSelector) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /umgmt : Returns all users of the application.
     * Returns all users of the application.
     *
     * @return Ok (status code 200)
     * @see UserManagementApi#getAllUsers
     */
    default ResponseEntity<List<Profile>> getAllUsers() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"data\" : { \"name\" : \"name\", \"email\" : \"foo@bar.com\" }, \"isAdmin\" : false, \"pfp\" : \"pfp\", \"username\" : \"username\" }, { \"data\" : { \"name\" : \"name\", \"email\" : \"foo@bar.com\" }, \"isAdmin\" : false, \"pfp\" : \"pfp\", \"username\" : \"username\" } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /umgmt/reset : Resets a user&#39;s password
     * Resets a user&#39;s password.
     *
     * @param userSelector  (required)
     * @return Ok (status code 200)
     *         or Bad request (status code 400)
     * @see UserManagementApi#resetUserPassword
     */
    default ResponseEntity<Void> resetUserPassword(UserSelector userSelector) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"description\" : \"description\", \"message\" : \"message\", \"status\" : 0, \"timestamp\" : \"2000-01-23T04:56:07.000+00:00\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PUT /umgmt : Updates user profile
     * Updates user profile.
     *
     * @param profile  (required)
     * @return Ok (status code 200)
     *         or Bad request (status code 400)
     * @see UserManagementApi#updateUser
     */
    default ResponseEntity<Profile> updateUser(Profile profile) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"name\" : \"name\", \"email\" : \"foo@bar.com\" }, \"isAdmin\" : false, \"pfp\" : \"pfp\", \"username\" : \"username\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"description\" : \"description\", \"message\" : \"message\", \"status\" : 0, \"timestamp\" : \"2000-01-23T04:56:07.000+00:00\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
