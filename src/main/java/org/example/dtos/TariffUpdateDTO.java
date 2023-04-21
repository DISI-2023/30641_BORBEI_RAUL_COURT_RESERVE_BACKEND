package org.example.dtos;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TariffUpdateDTO {

    private String type;

    private String fieldName;

    private double oldPrice;

    private double newPrice;
}
