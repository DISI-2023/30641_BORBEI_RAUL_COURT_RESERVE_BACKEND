package org.example.dtos;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    private String email;

    private String password;
}
