package org.example.builders;

import org.example.dtos.AppUserDetailsDTO;
import org.example.dtos.RequestDTO;
import org.example.dtos.RequestDetailsDTO;
import org.example.entities.AppUser;
import org.example.entities.Request;
import org.example.entities.Reservation;

public class RequestBuilder {

    public static RequestDTO toRequestDTO(Request request){
        return RequestDTO.builder()
                .id(request.getId())
                .take_over(request.isTake_over())
                .postedByUserId(request.getPostedBy().getId())
                .takenByUserId(request.getTakenBy() == null ? null : request.getTakenBy().getId())
                .reservationId(request.getReservation().getId())
                .build();
    }

    public static Request toEntity(RequestDTO dto, AppUser postedByUser, AppUser takenByUser, Reservation reservation){
        return Request.builder()
                .id(dto.getId())
                .take_over(dto.isTake_over())
                .postedBy(postedByUser)
                .takenBy(takenByUser)
                .reservation(reservation)
                .build();
    }

    public static RequestDetailsDTO toRequestDetailsDTO (Request request){
        return RequestDetailsDTO.builder()
                .id(request.getId())
                .take_over(request.isTake_over())
                .postedByUser(AppUserBuilder.toAppUserDTO(request.getPostedBy()))
                .takenByUser(request.getTakenBy() == null ? null : AppUserBuilder.toAppUserDTO(request.getTakenBy()))
                .reservation(ReservationBuilder.toReservationDTO(request.getReservation()))
                .build();
    }

}
