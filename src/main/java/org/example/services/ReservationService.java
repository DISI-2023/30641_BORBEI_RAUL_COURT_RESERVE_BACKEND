package org.example.services;

import net.bytebuddy.implementation.bytecode.Throw;
import org.example.builders.ReservationBuilder;
import org.example.dtos.FieldNameAndDateDTO;
import org.example.dtos.FreeReservationIntervalsDTO;
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
import java.util.ArrayList;
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

    public List<ReservationDTO> getUserReservations(UUID id){
        Optional<AppUser> appUser = appUserRepository.findById(id);
        if(!appUser.isPresent()) {
            LOGGER.error("User with id {} not found", id);
            throw new ResourceNotFoundException(ReservationService.class.getSimpleName());
        }
        List<Reservation> reservations = reservationRepository.findByAppUser(appUser.get());
        return reservations.stream().map(ReservationBuilder::toReservationDTO).collect(Collectors.toList());
    }

    /**
     * UPDATE
     */
    public UUID update(ReservationDTO reservationDTO){
        Optional<Reservation> reservation = reservationRepository.findById(reservationDTO.getId());
        if(!reservation.isPresent()){
            LOGGER.error("Reservation not found");
            throw new ResourceNotFoundException(ReservationService.class.getSimpleName());
        }

        Optional<Field> field = fieldRepository.findByName(reservationDTO.getFieldName());
        if(!field.isPresent()) {
            LOGGER.error("Field with name {} not found", reservationDTO.getFieldName());
            throw new ResourceNotFoundException(ReservationService.class.getSimpleName());
        }

        Optional<Reservation> checkReservation = reservationRepository.findByFieldAndStartTimeAndEndTime(field.get(), reservationDTO.getStartTime(), reservationDTO.getEndTime());
        if(checkReservation.isPresent()) {
            LOGGER.error("In the time slot specified already exists an reservation");
            throw new ResourceNotFoundException(ReservationService.class.getSimpleName());
        }

        Optional<Tariff> tariff = tariffRepository.findByFieldAndType(field.get(), reservationDTO.getType());
        if(!tariff.isPresent()) {
            LOGGER.error("A valid tariff was not found for the specific field");
            throw new ResourceNotFoundException(ReservationService.class.getSimpleName());
        }
        long time = computeTime(tariff.get(), reservationDTO);
        double finalPrice = tariff.get().getPrice() * time;
        reservation.get().setStartTime(reservationDTO.getStartTime());
        reservation.get().setEndTime(reservationDTO.getEndTime());
        reservation.get().setFinalPrice(finalPrice);
        reservationRepository.save(reservation.get());
        LOGGER.info("Reservation updated");
        return reservationDTO.getId();
    }

    /**
     * DELETE
     */
    public UUID delete(UUID id){
        reservationRepository.deleteById(id);
        return id;
    }

    /**
     * Get the intervals of time with no reservations, by field and date
     */
    public List<FreeReservationIntervalsDTO> getVacantIntervalsByFieldAndDate(FieldNameAndDateDTO fieldNameAndDateDTO){
        Field field = this.validateField(fieldNameAndDateDTO.getFieldName());
        LocalDateTime date = fieldNameAndDateDTO.getDate();

        /**
         * Here we retrieve all the reservations from the provided date, starting with
         * 10 AM all the way to 10 PM, as these are the agreed business hours
         */
        List<Reservation> allReservationsFromDay = reservationRepository.
                findByFieldAndStartTimeGreaterThanEqualAndStartTimeLessThanEqual(field,
                        LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 10, 00),
                        LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 21, 59));

        List<FreeReservationIntervalsDTO> freeReservationIntervals = new ArrayList<FreeReservationIntervalsDTO>();

        /**
         * Here I loop through every hour from 10 to 22 and iterate each step through the entire list of reservations
         * from the specified date. If no reservations are found for an hour then an item is added to the final list
         * specifying the start and end time of each interval which is free.
         * One important aspect is that we consider that all reservations are done at fixed hours (12:00, 13:00, etc)
         * and always last multiples of hours (1, 2, 3 or more hours)
         */
        for (int i = 10; i < 22; i++){
            boolean isFree = true;
            for (Reservation r : allReservationsFromDay ){
                if ( r.getStartTime().getHour() <= i && r.getEndTime().getHour() > i){
                    isFree = false;
                    break;
                }
            }
            if (isFree){
                LocalDateTime startTime = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(),
                        i, 0);
                LocalDateTime endTime = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(),
                        i+1, 0);
                freeReservationIntervals.add(new FreeReservationIntervalsDTO(startTime, endTime));
            }
        }

        return freeReservationIntervals;
    }


    private Field validateField(String fieldName){
        Optional<Field> field = fieldRepository.findByName(fieldName);
        if(!field.isPresent()) {
            LOGGER.error("Field with name {} not found", fieldName);
            throw new ResourceNotFoundException(ReservationService.class.getSimpleName());
        }
        return field.get();
    }

}
