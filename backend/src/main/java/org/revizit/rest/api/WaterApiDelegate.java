package org.revizit.rest.api;

import org.revizit.rest.model.ApiError;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import org.revizit.rest.model.WaterFlavourDto;
import org.revizit.rest.model.WaterReportDetail;
import org.revizit.rest.model.WaterReportDto;
import org.revizit.rest.model.WaterStateDetail;
import org.revizit.rest.model.WaterStateDto;
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
 * A delegate to be called by the {@link WaterApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")
public interface WaterApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * PUT /report/approve : Approves one or more water reports.
     * Approves one or more water reports. The water reports must be pending approval, form a consecutive list (in chronological order), and must start with the oldest pending report. 
     *
     * @param requestBody  (required)
     * @return Ok (status code 200)
     *         or Bad request (status code 400)
     * @see WaterApi#approveWaterReport
     */
    default ResponseEntity<Void> approveWaterReport(List<Long> requestBody) {
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
     * GET /water/current : The current water state.
     * Returns the count of full and empty gallons, as well as the water level in the current gallon. 
     *
     * @return Ok (status code 200)
     *         or Not found (status code 404)
     * @see WaterApi#getCurrentWaterState
     */
    default ResponseEntity<WaterStateDto> getCurrentWaterState() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"emptyGallons\" : 3, \"reportedAt\" : \"2021-01-01T00:00:00Z\", \"fullGallons\" : 4, \"waterLevel\" : 75 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /report : Returns the pending water reports.
     * Returns the pending water reports waiting for approval.
     *
     * @return Ok (status code 200)
     * @see WaterApi#getPendingWaterReports
     */
    default ResponseEntity<List<WaterReportDetail>> getPendingWaterReports() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"reportedAt\" : \"2021-01-01T00:00:00Z\", \"waterReport\" : { \"flavourId\" : 1, \"kind\" : \"PERCENTAGE\", \"value\" : 75 }, \"id\" : 1, \"reportedBy\" : \"John Doe\" }, { \"reportedAt\" : \"2021-01-01T00:00:00Z\", \"waterReport\" : { \"flavourId\" : 1, \"kind\" : \"PERCENTAGE\", \"value\" : 75 }, \"id\" : 1, \"reportedBy\" : \"John Doe\" } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /flavour : Returns all available water flavours.
     * Returns all available water flavours.
     *
     * @return Ok (status code 200)
     * @see WaterApi#getWaterFlavours
     */
    default ResponseEntity<List<WaterFlavourDto>> getWaterFlavours() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"name\" : \"Regular\", \"id\" : 1 }, { \"name\" : \"Regular\", \"id\" : 1 } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /water : Get historic water states.
     * Returns a list of water states, ordered by date. 
     *
     * @param from The earliest date to include in the result. (optional)
     * @param to The latest date to include in the result. (optional)
     * @return Ok (status code 200)
     *         or Unauthorized (status code 403)
     * @see WaterApi#getWaterStates
     */
    default ResponseEntity<List<WaterStateDetail>> getWaterStates(OffsetDateTime from,
        OffsetDateTime to) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"waterState\" : { \"emptyGallons\" : 3, \"reportedAt\" : \"2021-01-01T00:00:00Z\", \"fullGallons\" : 4, \"waterLevel\" : 75 }, \"id\" : 1, \"reportedBy\" : \"John Doe\" }, { \"waterState\" : { \"emptyGallons\" : 3, \"reportedAt\" : \"2021-01-01T00:00:00Z\", \"fullGallons\" : 4, \"waterLevel\" : 75 }, \"id\" : 1, \"reportedBy\" : \"John Doe\" } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PUT /report/reject : Rejects one or more water reports.
     * Rejects one or more water reports. The water reports must be pending approval. 
     *
     * @param requestBody  (required)
     * @return Ok (status code 200)
     *         or Bad request (status code 400)
     * @see WaterApi#rejectWaterReport
     */
    default ResponseEntity<Void> rejectWaterReport(List<Long> requestBody) {
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
     * POST /report : Submits a water report.
     * Submits a water report for the current user (if any).
     *
     * @param waterReportDto  (required)
     * @return Ok (status code 200)
     *         or Unauthorized (status code 403)
     * @see WaterApi#submitWaterReport
     */
    default ResponseEntity<WaterReportDetail> submitWaterReport(WaterReportDto waterReportDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"reportedAt\" : \"2021-01-01T00:00:00Z\", \"waterReport\" : { \"flavourId\" : 1, \"kind\" : \"PERCENTAGE\", \"value\" : 75 }, \"id\" : 1, \"reportedBy\" : \"John Doe\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
