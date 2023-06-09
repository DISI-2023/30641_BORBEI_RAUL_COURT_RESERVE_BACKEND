package org.example.services;

import org.example.builders.RequestBuilder;
import org.example.dtos.RequestDTO;
import org.example.dtos.RequestDetailsDTO;
import org.example.entities.AppUser;
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

import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        Reservation reservation = this.validateReservationId(dto.getReservationId());

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

        // The take over is defaulted to false. If it is specified in the JSON as true it will be set as true
        // otherwise it will remain false, as when it's null it is actually set as false by default,
        // so it can't have a null value
        dto.setTake_over(dto.isTake_over());

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
        // clean all the requests for reservations from the past
        this.deleteRequestsFromPast();

        // get a list of all the cleaned requests
        List<Request> requests = requestRepository.findAll();
        return requests.stream().map(RequestBuilder::toRequestDetailsDTO).collect(Collectors.toList());
    }

    /**
     * SELECT all requests besides one specified user
     */
    public List<RequestDetailsDTO> findAllExceptUser(UUID userId){

        // validate user
        AppUser user = this.validateAppUserId(userId);

        // clean all the requests for reservations from the past
        this.deleteRequestsFromPast();

        List<Request> requests = requestRepository.findRequestByPostedByNot(user);

        return requests.stream().map(RequestBuilder::toRequestDetailsDTO).collect(Collectors.toList());
    }

    /**
     * UPDATE taken by user
     * @param dto
     * @return
     */
    public UUID updateTakenByUser(RequestDTO dto){

        UUID takenByUserId = dto.getTakenByUserId();

        // validate the taken by user ID
        AppUser takenByUser = this.validateAppUserId(takenByUserId);

        // validate the request ID sent from FE
        Request originalRequest = this.validateRequestId(dto.getId());

        originalRequest.setTakenBy(takenByUser);

        Request newRequest = requestRepository.save(originalRequest);

        return newRequest.getId();
    }

    /**
     * UPDATE the taken by user as well as the user from the reservation
     * @param dto
     * @return
     */
    public UUID acceptTakeOverReservation(RequestDTO dto){
        // validate the request ID
        Request originalRequest = this.validateRequestId(dto.getId());

        // validate if this is indeed a takeover request
        if (!originalRequest.isTake_over()){
            LOGGER.error("Request with id {} does NOT support takeover", dto.getId());
            throw new ResourceNotFoundException(RequestService.class.getSimpleName());
        }

        // validate the taken by user ID
        AppUser takenByUser = this.validateAppUserId(dto.getTakenByUserId());

        // verify to not have the same posted by and taken by user
        if (takenByUser.getId().equals(originalRequest.getPostedBy().getId())){
            LOGGER.error("Request with id {} cannot be taken over by the same user that posted it!", dto.getId());
            throw new ResourceNotFoundException(RequestService.class.getSimpleName());
        }

        // Update the taken by user
        UUID reqId = this.updateTakenByUser(dto);

        //the reservation is validated
        Reservation reservation = this.validateReservationId(dto.getReservationId());

        // set the new user for reservation
        reservation.setAppUser(takenByUser);

        // save the updated reservation
        reservationRepository.save(reservation);

        return reqId;
    }

    /**
     * DELETE by ID
     **/
    public void deleteById(UUID id){
        this.validateRequestId(id);
        requestRepository.deleteById(id);
        LOGGER.debug("Request with id {} was deleted", id);
    }

    /**
     * DELETE past requests
     **/
    public void deleteRequestsFromPast(){

        // we get a list with all the reservations
        List<Request> allRequests = requestRepository.findAll();

        // we loop through every request and if the start time date is before the current time it will be deleted
        for (Request r : allRequests ){
            if ( r.getReservation().getStartTime().isBefore(LocalDateTime.now(ZoneId.of("Europe/Bucharest")))){
                requestRepository.delete(r);
            }
        }

    }

    private AppUser validateAppUserId(UUID userId){
        Optional<AppUser> user = appUserRepository.findById(userId);
        if (!user.isPresent()) {
            LOGGER.error("User with id {} was not found in db", userId);
            throw new ResourceNotFoundException(Request.class.getSimpleName());
        }
        return user.get();
    }

    private AppUser validateAppUser(AppUser user){

        if ( user == null ){
            LOGGER.error("User with id null was not found in db");
            throw new ResourceNotFoundException(AppUser.class.getSimpleName());
        }

        Optional<AppUser> searchedUser = appUserRepository.findById(user.getId());
        if (!searchedUser.isPresent()) {
            LOGGER.error("User with id {} was not found in db", user.getId());
            throw new ResourceNotFoundException(AppUser.class.getSimpleName());
        }
        return searchedUser.get();
    }

    private Reservation validateReservationId(UUID reservationId){
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);
        if (!reservation.isPresent()) {
            LOGGER.error("Reservation with id {} was not found in db", reservationId);
            throw new ResourceNotFoundException(Reservation.class.getSimpleName());
        }
        return reservation.get();
    }

    private Request validateRequestId(UUID requestId){
        Optional<Request> request = requestRepository.findById(requestId);
        if (!request.isPresent()) {
            LOGGER.error("Reservation with id {} was not found in db", requestId);
            throw new ResourceNotFoundException(Request.class.getSimpleName());
        }
        return request.get();
    }
}
