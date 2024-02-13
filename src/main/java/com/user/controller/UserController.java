package com.user.controller;

import com.user.exceptions.AuthenticationException;
import com.user.exceptions.BusinessException;
import com.user.exceptions.SystemException;
import com.user.models.request.SignInRequest;
import com.user.models.response.APIResponseEntity;
import com.user.models.response.AuthenticationResponse;
import com.user.pojo.Address;
import com.user.pojo.User;
import com.user.security.LogoutService;
import com.user.service.IUserService;
import com.user.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.ValidationException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final IUserService userService;

    private final LogoutService logoutService;

    @Autowired
    public UserController(final IUserService userService,
                          final LogoutService logoutService) {
        this.userService = userService;
        this.logoutService = logoutService;
    }

    /**
     * User Login
     *
     * @param user User Login Request Body
     * @return Authorization Response with JWT Token
     * @throws com.user.exceptions.BusinessException BusinessException
     * @throws com.user.exceptions.ValidationException ValidationException
     * @throws com.user.exceptions.SystemException SystemException
     */
    @Operation(summary = "User Login",
            description = "This API is used to Login User",
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
    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<APIResponseEntity<AuthenticationResponse>> userSignIn(
            @Parameter(description = "Login Request", required = true) @RequestBody @Valid SignInRequest user
    ) throws AuthenticationException, BusinessException, SystemException {

        log.info("Logging in User Request: {}", user);

        AuthenticationResponse auth = userService.userSignIn(user);

        APIResponseEntity<AuthenticationResponse> response = new APIResponseEntity<>(
                Constants.STATUS_SUCCESS,
                Constants.SUCCESS_CODE,
                auth
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * New User Signup
     *
     * @param user User Signup Request Body
     * @return Authorization Response with JWT Token
     * @throws com.user.exceptions.BusinessException BusinessException
     * @throws com.user.exceptions.ValidationException ValidationException
     * @throws com.user.exceptions.SystemException SystemException
     */
    @Operation(summary = "User Signup",
            description = "This API is used to Signup New User",
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
    @PostMapping(
            value = "/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<APIResponseEntity<AuthenticationResponse>> userSignup(
            @Parameter(description = "Signup Request", required = true) @RequestBody @Valid User user
    ) throws ValidationException, BusinessException, SystemException {

        log.info("Signing up New User Request: {}", user);

        AuthenticationResponse authResponse = userService.userSignup(user);

        APIResponseEntity<AuthenticationResponse> response = new APIResponseEntity<>(
                Constants.STATUS_SUCCESS,
                Constants.SUCCESS_CODE,
                authResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get User Profile for given user
     *
     * @param id User IDENTIFIER
     * @return User Details for given User Id
     * @throws BusinessException BusinessException
     * @throws SystemException   SystemException
     */
    @Operation(summary = "Get User Profile for given user",
            description = "This API is used to Get User Profile for given user Id",
            tags = {"Checks"},
            method = "GET"
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
            value = "/profile/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<APIResponseEntity<User>> getUserProfile(
            @Parameter(description = "User Identifier", required = true) @PathVariable("id") int id
    ) throws BusinessException, Exception {

        log.info("Getting User Profile for given User Id : " + id);

        User foundUser = userService.getProfile(id);

        APIResponseEntity<User> response = new APIResponseEntity<>(
                Constants.STATUS_SUCCESS,
                Constants.SUCCESS_CODE,
                foundUser
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get all the addresses for given user
     *
     * @param userId User IDENTIFIER
     * @return List of Addresses for given User Id
     * @throws BusinessException BusinessException
     * @throws SystemException   SystemException
     */
    @Operation(summary = "Get all the addresses for given user",
            description = "This API is used to get all the addresses for the User with given userId",
            tags = {"Checks"},
            method = "GET"
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
            value = "/address/list/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<APIResponseEntity<List<Address>>> getAllAddressList(
            @Parameter(description = "User Identifier", required = true) @PathVariable("userId") Integer userId
    ) throws Exception {

        log.info("Getting all addresses for given User Id: {}", userId);

        List<Address> allAddresses = userService.getAddress(userId);

        APIResponseEntity<List<Address>> response =
                new APIResponseEntity<>(
                        Constants.STATUS_SUCCESS,
                        Constants.SUCCESS_CODE,
                        allAddresses);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
