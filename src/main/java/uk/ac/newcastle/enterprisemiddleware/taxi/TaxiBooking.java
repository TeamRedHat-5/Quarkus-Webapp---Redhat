package uk.ac.newcastle.enterprisemiddleware.taxi;

import uk.ac.newcastle.enterprisemiddleware.customer.Customer;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * <p>Simple POJO representing TaxiBooking objects</p>
 *
 * @author Ronil
 */

public class TaxiBooking implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private int taxiId;
    private int customerId;
    private Date bookingDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaxiId() {
        return taxiId;
    }

    public void setTaxiId(int taxiId) {
        this.taxiId = taxiId;
    }

    public int getCustomerId() {
        return customerId;
    }
//
////    @Override
////    public String toString() {
////        return "TaxiBooking{" +
////                "id=" + id +
////                ", taxiId='" + taxiId + '\'' +
////                ", customerId=" + customerId +
////                ", bookingDate=" + bookingDate +
////                '}';
////    }
//
//    private Long customerId;

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }


}
