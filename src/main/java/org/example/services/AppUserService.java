package org.example.services;

import org.example.builders.AppUserBuilder;
import org.example.dtos.AppUserDetailsDTO;
import org.example.entities.AppUser;
import org.example.repositories.AppUserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppUserService.class);
    private final AppUserRepository appUserRepository;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository){
        this.appUserRepository = appUserRepository;
    }


    /** CREATE **/
    public UUID insert(AppUser user){
        user.setIsAdmin(false);
        user = appUserRepository.save(user);
        LOGGER.debug("Person with id {} was inserted in db", user.getId());
        return user.getId();
    }

    /** READ **/
    public List<AppUserDetailsDTO> findAll(){
        List<AppUser> appUsers = appUserRepository.findAll();
        return appUsers.stream().map(AppUserBuilder::toAppUserDTO).collect(Collectors.toList());
    }

    public AppUserDetailsDTO findById(UUID id){
        Optional<AppUser> user = appUserRepository.findById(id);
        if(!user.isPresent()){
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(AppUser.class.getSimpleName());
        }
        return AppUserBuilder.toAppUserDTO(user.get());
    }

    /** UPDATE **/
    public UUID update(AppUserDetailsDTO appUserDetailsDTO){
        Optional<AppUser> user = appUserRepository.findById(appUserDetailsDTO.getId());
        if(!user.isPresent()){
            LOGGER.error("Person with id {} was not found in db", appUserDetailsDTO.getId());
            throw new ResourceNotFoundException(AppUser.class.getSimpleName());
        }
        user.get().setUsername(appUserDetailsDTO.getUsername());
        user.get().setEmail(appUserDetailsDTO.getEmail());
        AppUser appUser = appUserRepository.save(user.get());
        LOGGER.debug("Person with id {} was updated in db", appUser.getId());
        return appUser.getId();
    }

    /** DELETE **/
    public void delete(UUID id){
        Optional<AppUser> user = appUserRepository.findById(id);
        if(!user.isPresent()){
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(AppUser.class.getSimpleName());
        }
        appUserRepository.deleteById(user.get().getId());
        LOGGER.debug("Person with id {} was deleted", id);
    }

    /** LOGIN **/
    public AppUserDetailsDTO findByEmailAndPassword(String email, String password){
        Optional<AppUser> user = appUserRepository.findByEmailAndPassword(email,password);
        if(!user.isPresent()){
            LOGGER.error("Person with was not found in db");
            throw new ResourceNotFoundException(AppUser.class.getSimpleName());
        }
        return AppUserBuilder.toAppUserDTO(user.get());
    }

    /**EDIT PASSWORD**/
    public UUID editPassword(String email, String oldPassword, String newPassword){
        Optional<AppUser> user = appUserRepository.findByEmailAndPassword(email, oldPassword);
        if(!user.isPresent()){
            LOGGER.error("The password is incorrect");
            throw new ResourceNotFoundException(AppUser.class.getSimpleName());
        }

        user.get().setPassword(newPassword);
        AppUser appUser = appUserRepository.save(user.get());
        LOGGER.debug("Password updated for user with id {}", appUser.getId());

        return appUser.getId();
    }

}
