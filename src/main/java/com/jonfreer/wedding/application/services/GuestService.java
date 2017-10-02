package com.jonfreer.wedding.application.services;

import java.util.ArrayList;

import com.jonfreer.wedding.infrastructure.interfaces.services.LogService;
import com.jonfreer.wedding.infrastructure.queries.GuestSearchQuery;
import org.dozer.Mapper;
import com.jonfreer.wedding.application.interfaces.services.IGuestService;
import com.jonfreer.wedding.domain.interfaces.repositories.IGuestRepository;
import com.jonfreer.wedding.infrastructure.exceptions.ResourceNotFoundException;
import com.jonfreer.wedding.infrastructure.interfaces.factories.IGuestRepositoryFactory;
import com.jonfreer.wedding.domain.interfaces.unitofwork.IDatabaseUnitOfWork;
import com.jonfreer.wedding.infrastructure.interfaces.factories.IDatabaseUnitOfWorkFactory;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Named;
import javax.inject.Inject;

@Service
@Named
public class GuestService implements IGuestService {

    private final IGuestRepositoryFactory guestRepositoryFactory;
    private final IDatabaseUnitOfWorkFactory databaseUnitOfWorkFactory;
    private final LogService logService;
    private final Mapper mapper;

    @Inject
    public GuestService(
            IGuestRepositoryFactory guestRepositoryFactory,
            IDatabaseUnitOfWorkFactory databaseUnitOfWorkFactory,
            LogService logService,
            Mapper mapper) {

        this.guestRepositoryFactory = guestRepositoryFactory;
        this.databaseUnitOfWorkFactory = databaseUnitOfWorkFactory;
        this.logService = logService;
        this.mapper = mapper;
    }

    public com.jonfreer.wedding.servicemodel.Guest getGuest(int id)
            throws com.jonfreer.wedding.application.exceptions.ResourceNotFoundException {

        IDatabaseUnitOfWork unitOfWork =
                this.databaseUnitOfWorkFactory.create();
        IGuestRepository guestRepository =
                this.guestRepositoryFactory.create(unitOfWork);

        try {

            com.jonfreer.wedding.domain.Guest guest = guestRepository.getGuest(id);

            unitOfWork.Save();

            return this.mapper.map(guest, com.jonfreer.wedding.servicemodel.Guest.class);

        } catch (ResourceNotFoundException resourceNotFoundEx) {
            unitOfWork.Undo();
            this.logService.info(resourceNotFoundEx.getLocalizedMessage());
            throw new com.jonfreer.wedding.application.exceptions.ResourceNotFoundException(
                    resourceNotFoundEx.getMessage(),
                    resourceNotFoundEx, resourceNotFoundEx.getResourceId());
        } catch (Exception ex) {
            unitOfWork.Undo();
            this.logService.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public void updateGuest(com.jonfreer.wedding.servicemodel.Guest guest)
            throws com.jonfreer.wedding.application.exceptions.ResourceNotFoundException {

        IDatabaseUnitOfWork unitOfWork =
                this.databaseUnitOfWorkFactory.create();
        IGuestRepository guestRepository =
                this.guestRepositoryFactory.create(unitOfWork);

        try {

            com.jonfreer.wedding.domain.Guest guestDomain =
                    this.mapper.map(guest, com.jonfreer.wedding.domain.Guest.class);

            guestRepository.updateGuest(guestDomain);

            unitOfWork.Save();

        } catch (ResourceNotFoundException resourceNotFoundEx) {
            unitOfWork.Undo();
            this.logService.info(resourceNotFoundEx.getLocalizedMessage());
            throw new com.jonfreer.wedding.application.exceptions.ResourceNotFoundException(
                    resourceNotFoundEx.getMessage(),
                    resourceNotFoundEx, resourceNotFoundEx.getResourceId());
        } catch (Exception ex) {
            unitOfWork.Undo();
            this.logService.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public void deleteGuest(int id)
            throws com.jonfreer.wedding.application.exceptions.ResourceNotFoundException {

        IDatabaseUnitOfWork unitOfWork =
                this.databaseUnitOfWorkFactory.create();
        IGuestRepository guestRepository =
                this.guestRepositoryFactory.create(unitOfWork);

        try {

            guestRepository.deleteGuest(id);

            unitOfWork.Save();

        } catch (ResourceNotFoundException resourceNotFoundEx) {
            unitOfWork.Undo();
            this.logService.info(resourceNotFoundEx.getLocalizedMessage());
            throw new com.jonfreer.wedding.application.exceptions.ResourceNotFoundException(
                    resourceNotFoundEx.getMessage(),
                    resourceNotFoundEx, resourceNotFoundEx.getResourceId());
        } catch (Exception ex) {
            unitOfWork.Undo();
            this.logService.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public int insertGuest(com.jonfreer.wedding.servicemodel.Guest guest) {

        IDatabaseUnitOfWork unitOfWork =
                this.databaseUnitOfWorkFactory.create();
        IGuestRepository guestRepository =
                this.guestRepositoryFactory.create(unitOfWork);

        try {
            com.jonfreer.wedding.domain.Guest guestDomain =
                this.mapper.map(guest, com.jonfreer.wedding.domain.Guest.class);
            
            int guestId = guestRepository.insertGuest(guestDomain);

            unitOfWork.Save();

            return guestId;
        } catch (Exception ex) {
            unitOfWork.Undo();
            this.logService.error(ex);
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public ArrayList<com.jonfreer.wedding.servicemodel.Guest> getGuests(GuestSearchQuery searchQuery) {

        IDatabaseUnitOfWork unitOfWork =
            this.databaseUnitOfWorkFactory.create();
        IGuestRepository guestRepository =
            this.guestRepositoryFactory.create(unitOfWork);

        try {
            ArrayList<com.jonfreer.wedding.domain.Guest> guests =
                guestRepository.getGuests(searchQuery);

            unitOfWork.Save();

            ArrayList<com.jonfreer.wedding.servicemodel.Guest> guestsServiceModel = 
            		new ArrayList<com.jonfreer.wedding.servicemodel.Guest>();
            for(com.jonfreer.wedding.domain.Guest guest : guests){
            		guestsServiceModel.add(this.mapper.map(guest, com.jonfreer.wedding.servicemodel.Guest.class));
            }
            
            return guestsServiceModel;
        } catch (Exception ex) {
            unitOfWork.Undo();
            this.logService.error(ex);
            throw new RuntimeException(ex);
        }
    }
}
