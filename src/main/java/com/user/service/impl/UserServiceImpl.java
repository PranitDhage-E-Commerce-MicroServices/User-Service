package com.user.service.impl;

import com.user.exceptions.AuthenticationException;
import com.user.exceptions.BusinessException;
import com.user.exceptions.ResourceNotFoundException;
import com.user.models.request.SignInRequest;
import com.user.models.response.APIResponseEntity;
import com.user.models.response.AuthenticationResponse;
import com.user.pojo.Address;
import com.user.pojo.Credentials;
import com.user.pojo.Role;
import com.user.pojo.User;
import com.user.repository.CredentialsRepository;
import com.user.repository.UserRepository;
import com.user.security.JwtService;
import com.user.service.IAddressClient;
import com.user.service.IUserService;
import com.user.token.Token;
import com.user.token.TokenRepository;
import com.user.token.TokenType;
import com.user.utils.Constants;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements IUserService {

    final private UserRepository userRepo;

    final private CredentialsRepository credentialsRepo;

    final private AuthenticationManager authenticationManager;

    final private PasswordEncoder passwordEncoder;

    final private TokenRepository tokenRepository;

    final private IAddressClient addressClient;

    final private JwtService jwtService;

    @Autowired
    public UserServiceImpl(final UserRepository userRepo,
                           final CredentialsRepository credentialsRepo,
                           final TokenRepository tokenRepository,
                           final AuthenticationManager authenticationManager,
                           final PasswordEncoder passwordEncoder,
                           final IAddressClient addressClient,
                           final JwtService jwtService) {

        this.userRepo = userRepo;
        this.credentialsRepo = credentialsRepo;
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.addressClient = addressClient;
        this.jwtService = jwtService;
    }

    @Override
    public Credentials addNewAuth(Credentials credentials) throws BusinessException {
        try {
            return credentialsRepo.save(credentials);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), Constants.ERR_BUSINESS);
        }
    }

    @Override
    public AuthenticationResponse userSignup(User user) throws BusinessException {

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.USER);
            user.setStatus(0);
            user.setDate(new Date());

            // Add Email and Password to credentials table
            addNewAuth(new Credentials(user.getEmail(), user.getPassword()));

            User savedUser = userRepo.save(user);

            String jwtToken = jwtService.generateToken(savedUser);
            String refreshToken = jwtService.generateRefreshToken(savedUser);
            revokeAllUserTokens(savedUser);
            saveUserToken(savedUser, jwtToken);

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), Constants.ERR_BUSINESS);
        }
    }

    @Override
    public AuthenticationResponse userSignIn(SignInRequest signInRequest)
            throws AuthenticationException, BusinessException {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signInRequest.getEmail(),
                            signInRequest.getPassword()
                    )
            );

            User user = Optional.ofNullable(userRepo.findByEmail(signInRequest.getEmail())
                            .orElseThrow(
                                    () -> new AuthenticationException(
                                            "Account does not exist. Please Signup",
                                            Constants.ERR_AUTHENTICATION)))
                    .get();

            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);
            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
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

    @Override
    public List<Address> getAddress(Integer userId) throws BusinessException, Exception {

        try {
            APIResponseEntity<List<Address>> response = addressClient.getAddresses(userId);
            return response.getData();
        } catch (Exception e) {
            log.info("Exception occurred while getting User Addresses for User Id: {}", userId);
            throw new BusinessException(e.getMessage(), Constants.ERR_BUSINESS);
        }
    }


    public Token saveUserToken(User user, String jwtToken) throws BusinessException {

        try {
            Token token = Token.builder()
                    .user(user)
                    .token(jwtToken)
                    .tokenType(TokenType.BEARER)
                    .expired(false)
                    .revoked(false)
                    .build();
            return tokenRepository.save(token);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), Constants.ERR_BUSINESS);
        }
    }

    public List<Token> revokeAllUserTokens(User user) throws BusinessException {

        try {

            List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUserId());

            if (validUserTokens.isEmpty())
                return null;

            validUserTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });

            return tokenRepository.saveAll(validUserTokens);
//            tokenRepository.deleteAll(validUserTokens);

        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), Constants.ERR_BUSINESS);
        }
    }

}
