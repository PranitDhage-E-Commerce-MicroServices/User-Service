package com.user.service;

import com.user.exceptions.BusinessException;
import com.user.exceptions.ResourceNotFoundException;
import com.user.models.request.SignInRequest;
import com.user.pojo.User;

import java.util.List;

public interface IUserService {

    /**
     * User Login
     *
     * @param user User Login Request Body
     * @return Authorization Response with JWT Token
     */
    User userSignIn(SignInRequest user) throws  BusinessException, ResourceNotFoundException;

    /**
     * New User Signup
     *
     * @param user User Signup Request Body
     * @return Authorization Response with JWT Token
     */
    User userSignup(User user) throws  BusinessException, ResourceNotFoundException;

    /**
     * Get User Profile for given user
     *
     * @param id User IDENTIFIER
     * @return User Details for given User Id
     * @throws BusinessException BusinessException
     */
    User getProfile(int id) throws BusinessException, Exception;

}
