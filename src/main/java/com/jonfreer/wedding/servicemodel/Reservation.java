package com.jonfreer.wedding.servicemodel;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Represents a reservation made for the wedding.
 */
@XmlRootElement(name = "reservation")
public class Reservation implements Cloneable {

    private Integer id;
    private Boolean isAttending;
    private Date submittedDateTime;

    /**
     * Default constructor for the Reservation class. Creates an empty Reservation object.
     */
    public Reservation() {
        this.id = null;
        this.isAttending = false;
        this.submittedDateTime = null;
    }

    /**
     * Constructs a Reservation instance.
     *
     * @param id                The identifier of the reservation.
     * @param isAttending       The Boolean value indicating true when attending, false otherwise.
     * @param submittedDateTime The date and time that the reservation was submitted.
     */
    public Reservation(Integer id, Boolean isAttending, Date submittedDateTime) {
        this.id = id;
        this.isAttending = isAttending;
        this.submittedDateTime = (Date) submittedDateTime.clone();
    }

    /**
     * Determines whether the calling Reservation instance is equal to the provided Object instance.
     *
     * @param obj The Reservation object (represented as Object) to be compared against.
     * @return true if the provided Object is of the Reservation class, and all property values match;
     * otherwise returns false.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        Reservation reservationObj = (Reservation) obj;
        
        boolean hasSameReservationId = 
        	(this.id == null && reservationObj.id == null) ||
        	(
        		this.id != null && reservationObj.id != null &&
        		this.id.intValue() == reservationObj.id.intValue()
    		);
        
        boolean hasSameIsAttending = 
        	(this.isAttending == null && reservationObj.isAttending == null) ||
        	(
        		this.isAttending != null && reservationObj.isAttending != null &&
        		this.isAttending.booleanValue() == reservationObj.isAttending.booleanValue()
    		);
        
        boolean hasSameSubmittedDateTime = 
        	(this.submittedDateTime == null && reservationObj.submittedDateTime == null) ||
        	(
    			this.submittedDateTime != null && reservationObj.submittedDateTime != null &&
    			this.submittedDateTime.equals(reservationObj.submittedDateTime)
    		);
        
        if (
        		hasSameReservationId && hasSameIsAttending && hasSameSubmittedDateTime
            ) {
            return true;
        }
        return false;
    }
    
    /**
	 * Generates an integer representation of this Reservation instance.
	 */
    @Override
    public int hashCode(){
    	
    	final int prime = 17;
    	int hashCode = 1;
    	
    	if(this.id != null){
    		hashCode = hashCode * prime + this.id.hashCode();
    	}
    	
    	if(this.isAttending != null){
    		hashCode = hashCode * prime + this.isAttending.hashCode();
    	}
    	
    	if(this.submittedDateTime != null){
    		hashCode = hashCode * prime + this.submittedDateTime.hashCode();
    	}
    	
    	return hashCode;
    }

    /**
     * Creates a deep copy of the calling Reservation instance.
     *
     * @return The deep copy of the Reservation instance represented as an Object.
     */
    @Override
    public Object clone() {

        Reservation reservationObj = null;
        try {
            reservationObj = (Reservation) super.clone();
            reservationObj.submittedDateTime =
                    (Date) reservationObj.submittedDateTime.clone();
        } catch (CloneNotSupportedException e) { /* not possible. */ }

        return reservationObj;
    }

    /**
     * Retrieves the identifier for the reservation.
     *
     * @return The identifier for the reservation.
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Alters the identifier for the reservation.
     *
     * @param id The desired identifier for the reservation.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Retrieves the Boolean value indicating whether
     * the reservation owner is attending.
     *
     * @return true if attending; false otherwise.
     */
    public boolean getIsAttending() {
        return this.isAttending;
    }

    /**
     * Alters the Boolean values indicating whether the reservation
     * owner is attending.
     *
     * @param isAttending The desired Boolean value. Should be true if
     *                    attending; false otherwise.
     */
    public void setIsAttending(boolean isAttending) {
        this.isAttending = isAttending;
    }

    /**
     * Retrieves the date and time that the reservation was submitted.
     *
     * @return The date and time that the reservation was submitted.
     */
    public Date getSubmittedDateTime() {
        return this.submittedDateTime;
    }

    /**
     * Alters the date and time that the reservation was submitted.
     *
     * @param submittedDateTime The desired date and time that the reservation was submitted.
     */
    public void setSubmittedDateTime(Date submittedDateTime) {
        this.submittedDateTime = submittedDateTime;
    }
}

