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

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
     * The final price is computed based on the tariff type hourly, which is not needed to be
     * specified in the body of the API call
     * The reservation cannot be made for more than 24h
     * If the reservation is made from a subscription then the final price will be 0
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

        long time = computeTime(reservationDTO);
        if(time == -1 ){
            LOGGER.error("Reservation cannot  be made for more than 24h");
            throw new IndexOutOfBoundsException(ReservationService.class.getSimpleName());
        }

        Reservation newReservation = ReservationBuilder.toEntity(reservationDTO);
        newReservation.setField(field.get());
        newReservation.setAppUser(appUser.get());
        newReservation.setTariffType(tariff.get().getType());

        if(tariff.get().getType().equals("Hourly")) {
            double finalPrice = tariff.get().getPrice() * time;
            newReservation.setFinalPrice(finalPrice);
        }

        newReservation = reservationRepository.save(newReservation);
        LOGGER.info("New reservation created");
        return  newReservation.getId();
    }

    private long  computeTime(ReservationDTO reservationDTO){
        long time = 0;
        time = ChronoUnit.HOURS.between(reservationDTO.getStartTime(), reservationDTO.getEndTime());
        if(time > 24)
            return -1;
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

        Optional<Tariff> tariff = tariffRepository.findByFieldAndType(field.get(), "Hourly");
        if(!tariff.isPresent()) {
            LOGGER.error("A valid tariff was not found for the specific field");
            throw new ResourceNotFoundException(ReservationService.class.getSimpleName());
        }
        long time = computeTime(reservationDTO);
        if(time == -1 ){
            LOGGER.error("Reservation cannot  be made for more than 24h");
            throw new IndexOutOfBoundsException(ReservationService.class.getSimpleName());
        }
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
    public boolean delete(UUID id){
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if(!reservation.isPresent()){
            LOGGER.error("Cannot find reservation");
            throw  new ResourceNotFoundException(ReservationService.class.getSimpleName());
        }
        LocalDateTime currentTime = LocalDateTime.now();
        long timeLeft = ChronoUnit.HOURS.between(currentTime, reservation.get().getStartTime());
        if(timeLeft < 24)
        {
            LOGGER.info("A reservation can be canceled only with minimum of 24h in advance");
            return false;
        }
        reservationRepository.deleteById(id);
        return true;
    }

    /**
     * Get the intervals of time with no reservations, by field and date
     */
    public List<FreeReservationIntervalsDTO> getVacantIntervalsByFieldAndDate(FieldNameAndDateDTO fieldNameAndDateDTO){
        Field field = this.validateField(fieldNameAndDateDTO.getFieldName());
        LocalDate reservationsDate = fieldNameAndDateDTO.getDate();

        /**
         * Here we retrieve all the reservations from the provided date, starting with
         * 10 AM all the way to 10 PM, as these are the agreed business hours
         */
        List<Reservation> allReservationsFromDay = reservationRepository.
                findByFieldAndStartTimeGreaterThanEqualAndStartTimeLessThanEqual(field,
                        LocalDateTime.of(reservationsDate.getYear(), reservationsDate.getMonth(), reservationsDate.getDayOfMonth(), 10, 0),
                        LocalDateTime.of(reservationsDate.getYear(), reservationsDate.getMonth(), reservationsDate.getDayOfMonth(), 21, 59));


        /**
         * This calculates the offset in hours between the time of the machine that the JVM is running on and the
         * local time of Romania (from where we are running our application).
         */
        // Specify the time zones
        TimeZone timeZone = TimeZone.getDefault();
        ZoneId zone1 = ZoneId.of(timeZone.getID());
        ZoneId zone2 = ZoneId.of("Europe/Bucharest");

        // Get the current time in each time zone
        ZonedDateTime time1 = ZonedDateTime.now(zone1);
        ZonedDateTime time2 = ZonedDateTime.now(zone2);

        // Calculate the hour difference
        int offsetHours1 = time1.getOffset().getTotalSeconds() / 3600;
        int offsetHours2 = time2.getOffset().getTotalSeconds() / 3600;
        int hourDiff = offsetHours2 - offsetHours1;

        LocalDateTime currentDateTime = LocalDateTime.now();
        // the appropriate hour difference is added to the current time as calculated above
        int currentHour = currentDateTime.getHour() + hourDiff;
        int iterator = 10;
        // if the date provided is today and the request is made during business hour the start hour of the search
        // is adjusted according to current time
        if ( currentDateTime.toLocalDate().equals(reservationsDate) && currentHour > 9 ){
            iterator = currentHour + 1;
        }

        /**
         * Here I loop through every hour from 10 to 22 and iterate each step through the entire list of reservations
         * from the specified date. If no reservations are found for an hour then an item is added to the final list
         * specifying the start and end time of each interval which is free.
         * One important aspect is that we consider that all reservations are done at fixed hours (12:00, 13:00, etc)
         * and always last multiples of hours (1, 2, 3 or more hours)
         * This has been modified to verify if the date provided is today, then there will be returned only free time
         * slots after the present time.
         */
        List<FreeReservationIntervalsDTO> freeReservationIntervals = new ArrayList<>();
        while ( iterator < 22 ){
            boolean isFree = true;
            for (Reservation r : allReservationsFromDay ){
                if ( r.getStartTime().getHour() <= iterator && r.getEndTime().getHour() > iterator){
                    isFree = false;
                    break;
                }
            }
            if (isFree){
                LocalDateTime startTime = LocalDateTime.of(reservationsDate.getYear(), reservationsDate.getMonth(), reservationsDate.getDayOfMonth(),
                        iterator, 0);
                LocalDateTime endTime = LocalDateTime.of(reservationsDate.getYear(), reservationsDate.getMonth(), reservationsDate.getDayOfMonth(),
                        iterator+1, 0);
                freeReservationIntervals.add(new FreeReservationIntervalsDTO(startTime, endTime));
            }

            iterator++;
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
