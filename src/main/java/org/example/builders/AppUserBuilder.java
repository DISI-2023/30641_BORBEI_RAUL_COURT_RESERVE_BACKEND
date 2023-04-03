package org.example.builders;

import org.example.dtos.AppUserDetailsDTO;
import org.example.entities.AppUser;

public class AppUserBuilder {

    public static AppUserDetailsDTO toAppUserDTO(AppUser appUser){
        return AppUserDetailsDTO.builder().id(appUser.getId()).
                username(appUser.getUsername()).
                email(appUser.getEmail()).
                isAdmin(appUser.getIsAdmin()).build();
    }

    public static AppUser toEntity(AppUserDetailsDTO appUserDetailsDTO){
        return AppUser.builder().id(appUserDetailsDTO.getId()).
                username(appUserDetailsDTO.getUsername()).
                email(appUserDetailsDTO.getEmail()).
                isAdmin(appUserDetailsDTO.getIsAdmin()).build();
    }
}
