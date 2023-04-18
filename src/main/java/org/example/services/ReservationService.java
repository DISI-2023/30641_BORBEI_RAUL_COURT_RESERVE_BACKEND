package org.example.services;

import org.example.builders.ReservationBuilder;
import org.example.dtos.ReservationDTO;
import org.example.dtos.TariffDTO;
import org.example.entities.AppUser;
import org.example.entities.Field;
import org.example.entities.Reservation;
import org.example.entities.Tariff;
import org.example.repositories.AppUserRepository;
import org.example.repositories.FieldRepository;
import org.example.repositories.ReservationRepository;
import org.example.repositories.TariffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ReservationService {
    public static final Logger LOGGER = LoggerFactory.getLogger(ReservationService.class);

    public final ReservationRepository reservationRepository;

    public final FieldRepository fieldRepository;

    public final TariffRepository tariffRepository;

    public final AppUserRepository appUserRepository;

    @Autowired
    ReservationService(ReservationRepository reservationRepository, FieldRepository fieldRepository, TariffRepository tariffRepository, AppUserRepository appUserRepository){
        this.reservationRepository = reservationRepository;
        this.fieldRepository = fieldRepository;
        this.tariffRepository = tariffRepository;
        this.appUserRepository = appUserRepository;
    }

    /**
     * CREATE
     * A reservation can be made only if the time slot is available.
     * The final price is computed based on the tariff type which can be: hourly, daily, weekly, monthly
     */
    public UUID insert(ReservationDTO reservationDTO){
        Optional<Field> field = fieldRepository.findByName(reservationDTO.getFieldName());
        if(!field.isPresent()) {
            LOGGER.error("Field with name {} not found", reservationDTO.getFieldName());
            throw new ResourceNotFoundException(ReservationService.class.getSimpleName());
        }
        Optional<AppUser> appUser = appUserRepository.findByEmail(reservationDTO.getUserEmail());
        if(!appUser.isPresent()) {
            LOGGER.error("User with email {} not found", reservationDTO.getUserEmail());
            throw new ResourceNotFoundException(ReservationService.class.getSimpleName());
        }
        Optional<Tariff> tariff = tariffRepository.findByFieldAndType(field.get(), reservationDTO.getType());
        if(!tariff.isPresent()) {
            LOGGER.error("A valid tariff was not found for the specific field");
            throw new ResourceNotFoundException(ReservationService.class.getSimpleName());
        }
        Optional<Reservation> reservation = reservationRepository.findByFieldAndStartTimeAndEndTime(field.get(), reservationDTO.getStartTime(), reservationDTO.getEndTime());
        if(reservation.isPresent()) {
            LOGGER.error("In the time slot specified already exists an reservation");
            throw new ResourceNotFoundException(ReservationService.class.getSimpleName());
        }

        long time = computeTime(tariff.get(), reservationDTO);

        double finalPrice = tariff.get().getPrice() * time;

        Reservation newReservation = ReservationBuilder.toEntity(reservationDTO);
        newReservation.setField(field.get());
        newReservation.setAppUser(appUser.get());
        newReservation.setFinalPrice(finalPrice);
        newReservation.setTariffType(tariff.get().getType());
        newReservation = reservationRepository.save(newReservation);
        LOGGER.info("New reservation created");
        return  newReservation.getId();
    }

    private long  computeTime(Tariff tariff, ReservationDTO reservationDTO){
        long time = 0;

        if(tariff.getType().equals("Hourly"))
            time = ChronoUnit.HOURS.between(reservationDTO.getStartTime(), reservationDTO.getEndTime());
        if(tariff.getType().equals("Daily"))
            time = ChronoUnit.DAYS.between(reservationDTO.getStartTime(), reservationDTO.getEndTime());
        if(tariff.getType().equals("Weekly"))
            time = ChronoUnit.WEEKS.between(reservationDTO.getStartTime(), reservationDTO.getEndTime());
        if(tariff.getType().equals("Monthly"))
            time = ChronoUnit.MONTHS.between(reservationDTO.getStartTime(), reservationDTO.getEndTime());
        return time;
    }

    /**
     * READ
     */
    public List<ReservationDTO> getAll(){
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream().map(ReservationBuilder::toReservationDTO).collect(Collectors.toList());
    }

}
