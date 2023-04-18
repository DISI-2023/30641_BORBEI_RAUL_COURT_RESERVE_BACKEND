package org.example.dtos;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TariffDTO {


    private String type;

    private double price;

    private String fieldName;
}
