package org.example.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TariffUpdateDTO {

    /**
        Attribute type must only have the following values: Hourly, Daily, Weekly, Monthly
     */
    private String type;

    private String fieldName;

    private double oldPrice;

    private double newPrice;
}
