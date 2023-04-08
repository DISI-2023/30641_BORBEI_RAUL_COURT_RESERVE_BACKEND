package org.example.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class NewPasswordDTO {
    private String email;

    private String oldPassword;

    private String newPassword;
}
