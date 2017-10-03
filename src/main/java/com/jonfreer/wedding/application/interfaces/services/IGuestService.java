package com.jonfreer.wedding.application.interfaces.services;

import com.jonfreer.wedding.servicemodel.Guest;
import com.jonfreer.wedding.application.exceptions.ResourceNotFoundException;
import com.jonfreer.wedding.infrastructure.queries.GuestSearchQuery;

import java.util.ArrayList;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface IGuestService {

    ArrayList<Guest> getGuests(GuestSearchQuery searchQuery);

    Guest getGuest(int id) throws ResourceNotFoundException;

    void updateGuest(Guest guest) throws ResourceNotFoundException;

    void deleteGuest(int id) throws ResourceNotFoundException;

    int insertGuest(Guest guest);

}
