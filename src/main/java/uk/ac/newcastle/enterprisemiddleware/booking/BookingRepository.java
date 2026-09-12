package uk.ac.newcastle.enterprisemiddleware.booking;

import uk.ac.newcastle.enterprisemiddleware.contact.Contact;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@RequestScoped
public class BookingRepository {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    EntityManager em;

    /**
     * <p>Returns a List of all persisted {@link Booking} objects.</p>
     *
     * @return List of Booking objects
     */
    List<Booking> findAll() {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_ALL, Booking.class);
        return query.getResultList();
    }
    /**
     * <p>Returns Booking object with specified id<p/>
     *
     * @param id id of booking
     * @return The Booking with the specified id
     */
    Booking findById(Long id) {
        return em.find(Booking.class, id);
    }

//   List <Booking> findByCustomerId(Long customerId){
//        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BY_CUSTOMER_ID, Booking.class)
//                .setParameter("customerId",customerId);
//        List<Booking> resultList=query.getResultList();
//        return resultList;
//
//    }

    /**
     * <p>Returns Booking object with specified id<p/>
     *
     * @param flightId bookingDate
     * @return booking object
     */

      Booking findByDateAndFlightId(Long flightId, Date date) {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BY_DATE_AND_FLIGHT_ID, Booking.class)
                .setParameter("flightId", flightId)
                .setParameter("bookingDate", date)
                .setMaxResults(1);
        List<Booking> resultList = query.getResultList();
        return resultList.isEmpty() ? null: resultList.get(0);
    }

    /**
     * <p>Persists the provided Booking object to the application database using the EntityManager.</p>
     *
     * <p>{@link javax.persistence.EntityManager#persist(Object) persist(Object)} takes an entity instance, adds it to the
     * context and makes that instance managed (ie future updates to the entity will be tracked)</p>
     *
     * <p>persist(Object) will set the @GeneratedValue @Id for an object.</p>
     *
     * @param booking The Booking object to be persisted
     * @return The Booking object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Booking create(Booking booking) throws Exception {
        log.info("BookingRepository.create() - Creating " + booking.getFlight());

        // Write the booking to the database.
        em.persist(booking);

        return booking;
    }

    /**
     * <p>Deletes the provided Booking object from the application database if found there</p>
     *
     * @param booking The Booking object to be removed from the application database
     * @return The Booking object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Booking delete(Booking booking) throws Exception {
        log.info("BookingRepository.delete() - Deleting " + booking.getId());

        if (booking.getId() != null) {

            em.remove(em.merge(booking));

        } else {
            log.info("BookingRepository.delete() - No ID was found so can't Delete.");
        }

        return booking;
    }



}
