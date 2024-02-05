package com.user.service.impl;

import com.user.exceptions.AuthenticationException;
import com.user.exceptions.BusinessException;
import com.user.exceptions.ResourceNotFoundException;
import com.user.models.request.SignInRequest;
import com.user.pojo.Role;
import com.user.pojo.User;
import com.user.repository.CredentialsRepository;
import com.user.repository.UserRepository;
import com.user.service.IUserService;
import com.user.token.TokenRepository;
import com.user.utils.Constants;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements IUserService {

    final private UserRepository userRepo;

    final private CredentialsRepository credentialsRepo;

    final private TokenRepository tokenRepository;

    @Autowired
    public UserServiceImpl(final UserRepository userRepo,
                           final CredentialsRepository credentialsRepo,
                           final TokenRepository tokenRepository) {

        this.userRepo = userRepo;
        this.credentialsRepo = credentialsRepo;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public User userSignup(User user) throws BusinessException {

        try {
            user.setRole(Role.USER);
            user.setStatus(0);
            user.setDate(new Date());

            User savedUser = userRepo.save(user);

            return savedUser;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), Constants.ERR_BUSINESS);
        }
    }

    @Override
    public User userSignIn(SignInRequest signInRequest)
            throws AuthenticationException, BusinessException {

        try {

            User user = Optional.ofNullable(userRepo.findByEmail(signInRequest.getEmail())
                            .orElseThrow(
                                    () -> new AuthenticationException(
                                            "Account does not exist. Please Signup",
                                            Constants.ERR_AUTHENTICATION)))
                    .get();


            return user;
        } catch (AuthenticationException e) {
            throw new BusinessException(e.getMessage(), Constants.ERR_BUSINESS);
        }
    }

    @Override
    public User getProfile(int id) throws BusinessException, Exception {
        try {
            return userRepo.findById(id)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User  not found for given user Id : " + id,
                                    Constants.ERR_RESOURCE_NOT_FOUND)
                    );
        } catch (ResourceNotFoundException e) {
            log.info("User not found for given user Id : {}", id);
            throw new BusinessException(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info("Exception occurred while getting User Profile for User Id: {}", id);
            throw new BusinessException(e.getMessage(), Constants.ERR_BUSINESS);
        }
    }
}
