package org.example.builders;

import org.example.dtos.TariffDTO;
import org.example.entities.Tariff;

public class TariffBuilder {
    public static TariffDTO toTariffDTO(Tariff tariff){
        return TariffDTO.builder().price(tariff.getPrice())
                .type(tariff.getType())
                .fieldName(tariff.getField().getName()).build();
    }

    public static Tariff toEntity(TariffDTO tariffDTO){
        return Tariff.builder().type(tariffDTO.getType())
                .price(tariffDTO.getPrice())
                .build();
    }
}
