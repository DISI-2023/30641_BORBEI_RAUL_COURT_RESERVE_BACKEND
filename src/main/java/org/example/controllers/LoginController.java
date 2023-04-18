package org.example.controllers;

import org.example.dtos.AppUserDetailsDTO;
import org.example.dtos.LoginDTO;
import org.example.services.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@CrossOrigin
@RequestMapping(value = "/login")
public class LoginController {
    private final AppUserService appUserService;

    @Autowired
    public LoginController(AppUserService appUserService){
        this.appUserService = appUserService;
    }

    @PostMapping()
    public ResponseEntity<AppUserDetailsDTO> login(@Valid @RequestBody LoginDTO loginDTO){
        AppUserDetailsDTO user = appUserService.findByEmailAndPassword(loginDTO.getEmail(), loginDTO.getPassword());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
