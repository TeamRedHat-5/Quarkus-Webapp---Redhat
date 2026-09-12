package uk.ac.newcastle.enterprisemiddleware.booking;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import uk.ac.newcastle.enterprisemiddleware.area.InvalidAreaCodeException;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerValidator;
import uk.ac.newcastle.enterprisemiddleware.flight.Flight;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Dependent
public class BookingService {
    @Inject
    @Named("logger")
    Logger log;

//    @Inject
//    ContactValidator validator;
    @Inject
    BookingValidator validator;

    @Inject
    BookingRepository crud;


    /**
     * <p>Returns a List of all persisted {@link Booking} objects<p/>
     *
     * @return List of Booking objects
     */
    List<Booking> findAll() {
        return crud.findAll();
    }

    /**
     * <p>Returns a single Booking object, specified by a Long id.<p/>
     *
     * @param id The id field of the Booking to be returned
     * @return The Booking with the specified id
     */
    public Booking findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single Booking object.<p/>
     * @param date
     * @param flightId
     * @return The booking object with the specified fightID
     */
     public Booking findByDateAndFlightId(Long flightId, Date date) {
        return crud.findByDateAndFlightId(flightId, date);
    }

//    List<Booking> findByCustomerId(Long customerId){
//        return crud.findByCustomerId(customerId);
//    }

//    public Booking findByDateAndFlightIdAndCustomerId(Long flightId, Date date,Long customerId){
//        return findByDateAndFlightIdAndCustomerId(flightId,date,customerId);
//    }

    /**
     * <p>Writes the provided Booking object to the application database.<p/>
     *
     * <p>Validates the data in the provided Booking object using a {@link BookingValidator} object.<p/>
     *
     * @param booking The Booking object to be written to the database using a {@link BookingRepository} object
     * @return The Booking object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    public Booking create(Booking booking) throws Exception {
        validator.validateBooking(booking);
    return crud.create(booking);
    }

    /**
     * <p>Deletes the provided Booking object from the application database if found there.<p/>
     *
     * @param booking The Booking object to be removed from the application database
     * @return The Booking object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    public Booking delete(Booking booking) throws Exception {
        log.info("delete() - Deleting " + booking.toString());

        Booking deletedBooking = null;

        if (booking.getId() != null) {
            deletedBooking = crud.delete(booking);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }

        return deletedBooking;
    }

}
