package org.revizit.rest.api;

import org.revizit.rest.model.ApiError;
import org.revizit.rest.model.Profile;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")
@Controller
@RequestMapping("${openapi.reVIZit.base-path:}")
public class ProfileApiController implements ProfileApi {

    private final ProfileApiDelegate delegate;

    public ProfileApiController(@Autowired(required = false) ProfileApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new ProfileApiDelegate() {});
    }

    @Override
    public ProfileApiDelegate getDelegate() {
        return delegate;
    }

}
