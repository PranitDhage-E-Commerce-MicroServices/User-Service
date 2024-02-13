package com.user.service;

import com.user.exceptions.BusinessException;
import com.user.exceptions.ResourceNotFoundException;
import com.user.models.request.SignInRequest;
import com.user.models.response.AuthenticationResponse;
import com.user.pojo.Address;
import com.user.pojo.Credentials;
import com.user.pojo.User;

import java.util.List;

public interface IUserService {

    /**
     * User Login
     *
     * @param user User Login Request Body
     * @return Authorization Response with JWT Token
     */
    AuthenticationResponse userSignIn(SignInRequest user) throws  BusinessException, ResourceNotFoundException;

    /**
     * User Login
     *
     * @param credentials User Credentials Request Body
     * @return Authorization Response with JWT Token
     */
    Credentials addNewAuth(Credentials credentials) throws BusinessException;

    /**
     * New User Signup
     *
     * @param user User Signup Request Body
     * @return Authorization Response with JWT Token
     */
    AuthenticationResponse userSignup(User user) throws  BusinessException, ResourceNotFoundException;

    /**
     * Get User Profile for given user
     *
     * @param id User IDENTIFIER
     * @return User Details for given User Id
     * @throws BusinessException BusinessException
     */
    User getProfile(int id) throws BusinessException, Exception;

    /**
     * Get User Addresses for given user
     *
     * @param userId User IDENTIFIER
     * @return List of Address for given User Id
     * @throws BusinessException BusinessException
     */
    List<Address> getAddress(Integer userId) throws BusinessException, Exception;
}
