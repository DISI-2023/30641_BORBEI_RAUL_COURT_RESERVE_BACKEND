package org.example.services;

import org.example.builders.RequestBuilder;
import org.example.dtos.RequestDTO;
import org.example.dtos.RequestDetailsDTO;
import org.example.entities.AppUser;
import org.example.entities.Field;
import org.example.entities.Request;
import org.example.entities.Reservation;
import org.example.repositories.AppUserRepository;
import org.example.repositories.RequestRepository;
import org.example.repositories.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RequestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestService.class);
    private final RequestRepository requestRepository;
    private final AppUserRepository appUserRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository, AppUserRepository appUserRepository,
                          ReservationRepository reservationRepository){
        this.requestRepository = requestRepository;
        this.appUserRepository = appUserRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Create
     */
    public UUID insert(RequestDTO dto){

        //the reservation is validated
        Reservation reservation = this.validateReservation(dto.getReservationId());

        // here we verify if a request for the reservation taken as input has already been made
        // if this is a case we won't proceed with adding another one, for practical reasons and also
        // because there will be error when trying to retrieve the data from the DB
        Request req = requestRepository.findFirstByReservation(reservation);
        if (req != null){
            LOGGER.error("A request for this reservation was already made!");
            throw new ResourceNotFoundException(Request.class.getSimpleName());
        }

        // the posted by user is validated
        AppUser postedByUser = this.validateAppUser(reservation.getAppUser());

        // the take over can't be True at insertion in the DB. This will be modified later to True
        dto.setTake_over(false);

        // also, the taken by user can't be assigned to the request at the insertion.
        // This will happen later, when someone accepts this request
        Request request = RequestBuilder.toEntity(dto, postedByUser, null, reservation);

        request = requestRepository.save(request);
        LOGGER.debug("Field with id {} was inserted in db", request.getId());

        return request.getId();
    }

    /**
     * SELECT
     **/
    public List<RequestDetailsDTO> findAll(){
        List<Request> requests = requestRepository.findAll();
        return requests.stream().map(RequestBuilder::toRequestDetailsDTO).collect(Collectors.toList());
    }



    private AppUser validateAppUser(AppUser user){
        if ( user == null ){
            LOGGER.error("User with id null was not found in db");
            throw new ResourceNotFoundException(Request.class.getSimpleName());
        }

        Optional<AppUser> searchedUser = appUserRepository.findById(user.getId());
        if (!searchedUser.isPresent()) {
            LOGGER.error("User with id {} was not found in db", user.getId());
            throw new ResourceNotFoundException(Request.class.getSimpleName());
        }
        return searchedUser.get();
    }

    private Reservation validateReservation(UUID reservationId){
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);
        if (!reservation.isPresent()) {
            LOGGER.error("Reservation with id {} was not found in db", reservationId);
            throw new ResourceNotFoundException(Field.class.getSimpleName());
        }
        return reservation.get();
    }
}
