package com.example.UserServiceMay24.services;

import com.example.UserServiceMay24.exceptions.ExpiredTokenException;
import com.example.UserServiceMay24.exceptions.InvalidTokenException;
import com.example.UserServiceMay24.exceptions.PasswordMissMatchException;
import com.example.UserServiceMay24.models.Token;
import com.example.UserServiceMay24.models.User;
import com.example.UserServiceMay24.repositories.TokenRepository;
import com.example.UserServiceMay24.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
    }


    @Override
    public User signup(String name, String email, String password) throws Exception {
        Optional<User> optionalUser = this.userRepository.findUserByEmail(email);
        if(optionalUser.isPresent()){
            throw new Exception("User already present");
        }


        String encodedPassword = this.bCryptPasswordEncoder.encode(password);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(encodedPassword);

        return this.userRepository.save(user);
    }

    @Override
    public Token login(String email, String password) throws Exception {
        Optional<User> optionalUser = this.userRepository.findUserByEmail(email);
        User user = optionalUser.orElseThrow(() -> new Exception("User Not Found"));
        boolean matches = this.bCryptPasswordEncoder.matches(password, user.getPassword());
        if (matches){
            //Issue a token
            String value = RandomStringUtils.randomAlphanumeric(128);
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 30);
            Date thirtyDaysLater = c.getTime();

            Token token = new Token();
            token.setUser(user);
            token.setValue(value);
            token.setExpiresAt(thirtyDaysLater);
            return this.tokenRepository.save(token);

        }else{
            throw new PasswordMissMatchException("Password is incorrect");
        }
    }

    @Override
    public Token validateToken(String tokenValue) throws InvalidTokenException, ExpiredTokenException {
        Optional<Token> tokenByValue = this.tokenRepository.findTokenByValue(tokenValue);
        Token token = tokenByValue.orElseThrow(() -> new InvalidTokenException("Please use a valid token"));
        Date expiresAt = token.getExpiresAt();
        Date now = new Date();
        if (now.after(expiresAt) || token.isActive()){
            throw new ExpiredTokenException("The token has expired");
        }
        return token;
    }

    @Override
    public void logout(String tokenValue) throws Exception {
        Optional<Token> tokenByValue = this.tokenRepository.findTokenByValue(tokenValue);
        Token token = tokenByValue.orElseThrow(() -> new InvalidTokenException("Please use a valid token"));

        if(token.isActive()){
            token.setActive(false);
            this.tokenRepository.save(token);
            return;
        }
    }
}
