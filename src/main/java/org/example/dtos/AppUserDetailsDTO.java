package org.example.dtos;

import lombok.*;

import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDetailsDTO {
    private UUID id;

    private String username;

    private String email;

    private Boolean isAdmin;
}
