package org.example.controllers;

import org.example.dtos.AppUserDetailsDTO;
import org.example.dtos.LoginDTO;
import org.example.services.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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
        AppUserDetailsDTO user;
        try {
            user = appUserService.findByEmailAndPassword(loginDTO.getEmail(), loginDTO.getPassword());
        }
        catch(ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
