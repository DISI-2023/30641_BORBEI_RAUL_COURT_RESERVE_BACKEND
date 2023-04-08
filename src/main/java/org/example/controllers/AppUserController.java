package org.example.controllers;

import org.example.dtos.AppUserDetailsDTO;
import org.example.dtos.NewPasswordDTO;
import org.example.entities.AppUser;
import org.example.services.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
@CrossOrigin
@RequestMapping(value = "/user")
public class AppUserController {
    private final AppUserService appUserService;

    @Autowired
    public AppUserController(AppUserService appUserService){
        this.appUserService = appUserService;
    }

    /** REGISTER **/
    @PostMapping()
    public ResponseEntity<UUID> insertUser(@Valid @RequestBody AppUser appUser){
        UUID id = appUserService.insert(appUser);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<AppUserDetailsDTO>> getUsers(){
        List<AppUserDetailsDTO> users = appUserService.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AppUserDetailsDTO> getUserById(@PathVariable("id") UUID id){
        AppUserDetailsDTO user = appUserService.findById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<UUID> updateUser(@Valid @RequestBody AppUserDetailsDTO user){
        UUID id = appUserService.update(user);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UUID> deleteUser(@PathVariable("id") UUID id){
        appUserService.delete(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @PutMapping(value = "/editPassword")
    public ResponseEntity<UUID> editPassword(@Valid @RequestBody NewPasswordDTO user){
        UUID id = appUserService.editPassword(user.getEmail(), user.getOldPassword(), user.getNewPassword());
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

}

