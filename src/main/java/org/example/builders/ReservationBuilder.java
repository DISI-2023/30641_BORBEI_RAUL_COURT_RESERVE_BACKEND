package org.example.builders;

import org.example.dtos.ReservationDTO;
import org.example.entities.Reservation;

public class ReservationBuilder {

    public static ReservationDTO toReservationDTO(Reservation reservation){
        return ReservationDTO.builder().id(reservation.getId())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .finalPrice(reservation.getFinalPrice())
                .fieldName(reservation.getField().getName())
                .userEmail(reservation.getAppUser().getEmail())
                .type(reservation.getTariffType())
                .build();
    }

    public static Reservation toEntity(ReservationDTO reservationDTO){
        return Reservation.builder().id(reservationDTO.getId())
                .startTime(reservationDTO.getStartTime())
                .endTime(reservationDTO.getEndTime())
                .finalPrice(reservationDTO.getFinalPrice())
                .tariffType(reservationDTO.getType())
                .build();
    }
}
