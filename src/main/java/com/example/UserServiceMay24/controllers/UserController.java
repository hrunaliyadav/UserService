package com.example.UserServiceMay24.controllers;

import com.example.UserServiceMay24.dtos.LogInRequestDto;
import com.example.UserServiceMay24.dtos.LogOutRequestDto;
import com.example.UserServiceMay24.dtos.SignUpRequestDto;
import com.example.UserServiceMay24.dtos.ValidateTokenRequestDto;
import com.example.UserServiceMay24.exceptions.ExpiredTokenException;
import com.example.UserServiceMay24.exceptions.InvalidTokenException;
import com.example.UserServiceMay24.models.Token;
import com.example.UserServiceMay24.models.User;
import com.example.UserServiceMay24.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> sighUp(@RequestBody SignUpRequestDto requestDto){
        try{
            if(requestDto.getName() == null || requestDto.getEmail() ==null || requestDto.getPassword()==null){
                throw new Exception("Name, Email, Password Cannot be Null");
                //or return ResponseEntity.badRequest().build(); // client receives a 400 Bad Request response
            }
            User user = userService.signup(requestDto.getName(), requestDto.getEmail(), requestDto.getPassword());
            return new ResponseEntity<>(user, HttpStatusCode.valueOf(201));
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));

        }
    }
    @PostMapping("/login")
    public ResponseEntity<Token> logIn(@RequestBody LogInRequestDto requestDto){
        try{
            if(requestDto.getEmail()==null || requestDto.getPassword()==null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Token token = this.userService.login(requestDto.getEmail(), requestDto.getPassword());
            return new ResponseEntity<>(token, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));

        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logOut(@RequestBody LogOutRequestDto requestDto){
        try {
            if (requestDto.getToken() == null || requestDto.getToken().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            this.userService.logout(requestDto.getToken());
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<Token> validateToken(@RequestBody ValidateTokenRequestDto requestDto){
        try{
            if (requestDto.getToken() == null || requestDto.getToken().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Token token = this.userService.validateToken(requestDto.getToken());
            return new ResponseEntity<>(token,HttpStatusCode.valueOf(200));
        }catch (ExpiredTokenException ete){
            return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        }catch (InvalidTokenException ite){
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
    }
}
