package com.user.controller;

import com.user.exceptions.AuthenticationException;
import com.user.exceptions.BusinessException;
import com.user.exceptions.SystemException;
import com.user.models.response.APIResponseEntity;
import com.user.models.response.AuthenticationResponse;
import com.user.security.JwtService;
import com.user.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final JwtService jwtService;

    @Autowired
    public AuthController(final JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Validate Logged In User
     *
     * @param token Token
     * @throws com.user.exceptions.BusinessException   BusinessException
     * @throws com.user.exceptions.ValidationException ValidationException
     * @throws com.user.exceptions.SystemException     SystemException
     */
    @Operation(summary = "Validate Logged In User",
            description = "This API is used to Validate Logged In User",
            tags = {"Checks"},
            method = "POST"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Unexpected Error", content = @Content(schema = @Schema(implementation = Error.class))),
                    @ApiResponse(responseCode = "403", description = "Access Denied - User is either invalid or is not entitled to requested api action", content = @Content(schema = @Schema(implementation = Error.class))),
                    @ApiResponse(responseCode = "404", description = "Entity Not Found", content = @Content(schema = @Schema(implementation = Error.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = Error.class)))
            }
    )
    @GetMapping(
            value = "/validate-jwt",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Boolean> validateJwt(
            @Parameter(description = "JWT Token", required = true) @RequestParam(value = "token", required = true) String token
    ) throws AuthenticationException, BusinessException, SystemException, ServletException, IOException {

        log.info("Validate Logged In User Token: {}", token);

        jwtService.validateToken(token);

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

}
