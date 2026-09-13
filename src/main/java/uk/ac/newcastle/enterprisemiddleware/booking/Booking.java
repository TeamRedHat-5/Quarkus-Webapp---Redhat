package uk.ac.newcastle.enterprisemiddleware.booking;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Future;
import javax.xml.bind.annotation.XmlRootElement;

import org.acme.domain.model.BaseEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.smallrye.common.constraint.NotNull;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.flight.Flight;

/**
 * <p>This is a the Domain object. The Booking class represents how booking resources are represented in the application
 * database.</p>
 *
 * <p>The class also specifies how a contacts are retrieved from the database (with @NamedQueries), and acceptable values
 * for Contact fields (with @NotNull, @Pattern etc...)<p/>
 *
 * @author Ronil
 */

@Entity
@NamedQueries({
        @NamedQuery(name = Booking.FIND_ALL, query = "SELECT c FROM Booking c ORDER BY c.id ASC"),
        @NamedQuery(name = Booking.FIND_BY_ID, query = "SELECT c FROM Booking c WHERE c.id = :id"),
//        @NamedQuery(name = Booking.FIND_BY_CUSTOMER_ID, query = "SELECT c FROM Booking c WHERE c.customerId = :customerId"),
        @NamedQuery(name = Booking.FIND_BY_DATE_AND_FLIGHT_ID, query = "SELECT c FROM Booking c WHERE c.flightId = :flightId AND c.bookingDate = :bookingDate")
//        @NamedQuery(name = Booking.FIND_BY_DATE_AND_FLIGHT_ID_AND_CUSTOMER_ID, query = "SELECT c FROM Booking c WHERE c.flightId = :flightId AND c.bookingDate = :bookingDate AND c.customerId=:customerID")

})
@XmlRootElement
@Table(name = "booking", uniqueConstraints = @UniqueConstraint(columnNames = {"flightId", "bookingDate" }))
public class Booking extends BaseEntity implements Serializable {


    private static final long serialVersionUID = 1L;

    public static final String FIND_ALL = "Booking.findAll";
    public static final String FIND_BY_ID = "Booking.findById";
    public static final String FIND_BY_DATE_AND_FLIGHT_ID ="Booking.findByDateAndFlightId" ;

//    public static final String FIND_BY_CUSTOMER_ID = "Booking.findByCustomerId";
//
//    public static final String FIND_BY_DATE_AND_FLIGHT_ID_AND_CUSTOMER_ID ="Booking.findByDateAndFlightIdAndCustomerId" ;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name="flightId")
    private Long flightId;

    @NotNull
    @Column(name="customerId")
    private Long customerId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    @Column(name = "bookingdate")
    @Future(message ="Date Of Booking can not be in the past")
    @Temporal(TemporalType.DATE)
    private Date bookingDate;

    public Date getBookingDate() {
        return bookingDate;
    }
    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }



    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "customerId", insertable = false, updatable = false)
    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "flightId ", insertable = false, updatable = false)
    private Flight flight;

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id) && Objects.equals(flightId, booking.flightId) && Objects.equals(customerId, booking.customerId) && Objects.equals(bookingDate, booking.bookingDate) && Objects.equals(customer, booking.customer) && Objects.equals(flight, booking.flight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, flightId, customerId, bookingDate, customer, flight);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", flightId=" + flightId +
                ", customerId=" + customerId +
                ", bookingDate=" + bookingDate +
                ", customer=" + customer +
                ", flight=" + flight +
                '}';
    }
}