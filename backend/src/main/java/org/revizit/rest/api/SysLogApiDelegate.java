package org.revizit.rest.api;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import org.revizit.rest.model.SysLogEntry;
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
 * A delegate to be called by the {@link SysLogApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")
public interface SysLogApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /syslog : Returns application events.
     * Returns application events between the FROM and TO values, ordered by timestamp. If no  values are provided, today&#39;s events are returned. 
     *
     * @param from The earliest date to include in the result. (optional)
     * @param to The latest date to include in the result. (optional)
     * @return Ok (status code 200)
     * @see SysLogApi#getSysLogs
     */
    default ResponseEntity<List<SysLogEntry>> getSysLogs(OffsetDateTime from,
        OffsetDateTime to) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"elements\" : [ { \"msg\" : \"msg\", \"qualifier\" : \"qualifier\", \"name\" : \"name\" }, { \"msg\" : \"msg\", \"qualifier\" : \"qualifier\", \"name\" : \"name\" } ], \"action\" : \"action\", \"user\" : \"user\", \"timestamp\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"elements\" : [ { \"msg\" : \"msg\", \"qualifier\" : \"qualifier\", \"name\" : \"name\" }, { \"msg\" : \"msg\", \"qualifier\" : \"qualifier\", \"name\" : \"name\" } ], \"action\" : \"action\", \"user\" : \"user\", \"timestamp\" : \"2000-01-23T04:56:07.000+00:00\" } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
