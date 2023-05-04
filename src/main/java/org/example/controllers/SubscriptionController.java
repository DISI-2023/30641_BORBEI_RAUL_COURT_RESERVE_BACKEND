package org.example.controllers;

import org.example.dtos.SubscriptionDTO;
import org.example.services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.UUID;

@Controller
@CrossOrigin
@RequestMapping(value = "/subscription")
public class SubscriptionController {
    public final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping()
    public ResponseEntity<UUID> createSubscription(@Valid @RequestBody SubscriptionDTO subscriptionDTO){
       UUID id ;
        try{
           id = subscriptionService.insert(subscriptionDTO);
        }catch(ResourceNotFoundException e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }
}
