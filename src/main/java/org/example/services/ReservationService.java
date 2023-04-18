package org.example.services;

import org.example.repositories.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ReservationService {
    public static final Logger LOGGER = LoggerFactory.getLogger(ReservationService.class);

    public final ReservationRepository reservationRepository;

    @Autowired
    ReservationService(ReservationRepository reservationRepository){
        this.reservationRepository = reservationRepository;
    }

    /**
     * CREATE
     */

}
