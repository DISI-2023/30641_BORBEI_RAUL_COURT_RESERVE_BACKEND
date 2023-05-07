package org.example.services;

import org.example.builders.SubscriptionBuilder;
import org.example.dtos.SubscriptionDTO;
import org.example.entities.*;
import org.example.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {
    public static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionService.class);

    public final SubscriptionRepository subscriptionRepository;

    public final ReservationRepository reservationRepository;

    public final FieldRepository fieldRepository;

    public final AppUserRepository appUserRepository;

    public final TariffRepository tariffRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, ReservationRepository reservationRepository, FieldRepository fieldRepository, AppUserRepository appUserRepository, TariffRepository tariffRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.reservationRepository = reservationRepository;
        this.fieldRepository = fieldRepository;
        this.appUserRepository = appUserRepository;
        this.tariffRepository = tariffRepository;
    }

    /**
     * CREATE
     */
    public UUID insert(SubscriptionDTO subscriptionDTO){
        if(subscriptionDTO.getStartHour().getMinute() != 0 || subscriptionDTO.getStartHour().getMinute() !=0
        || subscriptionDTO.getStartHour().getSecond() != 0 || subscriptionDTO.getEndHour().getSecond() != 0){
            LOGGER.error("The start ane end hour need to be at fixed hour");
            throw new IllegalArgumentException(SubscriptionService.class.getSimpleName());
        }
        Optional<Field> field = fieldRepository.findByName(subscriptionDTO.getFieldName());
        if(!field.isPresent()) {
            LOGGER.error("Field with name {} not found", subscriptionDTO.getFieldName());
            throw new ResourceNotFoundException(SubscriptionService.class.getSimpleName());
        }
        Optional<AppUser> appUser = appUserRepository.findByEmail(subscriptionDTO.getUserEmail());
        if(!appUser.isPresent()) {
            LOGGER.error("User with email {} not found", subscriptionDTO.getUserEmail());
            throw new ResourceNotFoundException(SubscriptionService.class.getSimpleName());
        }
        Optional<Tariff> tariff = tariffRepository.findByFieldAndType(field.get(), subscriptionDTO.getType());
        if(!tariff.isPresent()) {
            LOGGER.error("A valid tariff was not found for the specific field");
            throw new ResourceNotFoundException(SubscriptionService.class.getSimpleName());
        }
        double price = computePrice(tariff.get().getPrice(), tariff.get().getType(), subscriptionDTO.getStartTime(), subscriptionDTO.getEndTime());

        Subscription subscription = SubscriptionBuilder.toEntity(subscriptionDTO);
        subscription.setFinalPrice(price);
        subscription.setField(field.get());
        subscription.setAppUser(appUser.get());
        subscription = subscriptionRepository.save(subscription);
        LOGGER.info("Subscription made");
        makeReservations(subscription);
        return subscription.getId();
    }

    /**
     * Compute the total price for the subscription
     * @param tariff the tariff value
     * @param type the tariff type
     * @param start start date
     * @param end end date
     * @return final price
     */
    private double computePrice(double tariff, String type, LocalDate start, LocalDate end){
        if(type.equals("Weekly"))
            return tariff * ChronoUnit.WEEKS.between(start,end);
        if(type.equals("Monthly"))
            return tariff * ChronoUnit.MONTHS.between(start,end);
        if(type.equals("Annually"))
            return tariff * ChronoUnit.YEARS.between(start, end);
        return -1;
    }

    /**
     * Make a reservation for the specified subscription recursively
     * @param subscription
     */
    private void makeReservations(Subscription subscription){
       for(LocalDate date = subscription.getStartTime(); date.isBefore(subscription.getEndTime()); date = date.plusDays(1)){
           if(date.getDayOfWeek().equals(subscription.getDayOfWeek())){
               LocalDateTime start = date.atTime(subscription.getStartHour());
               LocalDateTime end = date.atTime(subscription.getEndHour());
               Reservation reservation = new Reservation();
               reservation.setTariffType(subscription.getType());
               reservation.setField(subscription.getField());
               reservation.setAppUser(subscription.getAppUser());
               reservation.setStartTime(start);
               reservation.setEndTime(end);
               reservationRepository.save(reservation);
           }
       }
    }

    public List<SubscriptionDTO> getAll(){
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        return subscriptions.stream().map(SubscriptionBuilder::toSubscriptionDTO).collect(Collectors.toList());
    }

    public List<SubscriptionDTO> getSubscriptionOfUser(UUID userId){
        Optional<AppUser> appUser = appUserRepository.findById(userId);
        List<Subscription> subscriptions = subscriptionRepository.findByAppUser(appUser.get());
        return subscriptions.stream().map(SubscriptionBuilder::toSubscriptionDTO).collect(Collectors.toList());
    }
}
