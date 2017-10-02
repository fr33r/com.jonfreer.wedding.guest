package com.jonfreer.wedding.infrastructure.repositories;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import javax.inject.Named;

import org.jvnet.hk2.annotations.Service;

import com.jonfreer.wedding.domain.Guest;
import com.jonfreer.wedding.domain.Reservation;
import com.jonfreer.wedding.domain.interfaces.repositories.IGuestRepository;
import com.jonfreer.wedding.domain.interfaces.unitofwork.IDatabaseUnitOfWork;
import com.jonfreer.wedding.infrastructure.exceptions.ResourceNotFoundException;
import com.jonfreer.wedding.infrastructure.queries.GuestSearchQuery;

/**
 * A database repository that directly interacts with the database to manage
 * guest entities.
 */
@Service
@Named
public class GuestRepository extends DatabaseRepository implements IGuestRepository {

	/**
	 * Constructs a new instance provided an instance of a class that implements
	 * the IDatabaseUnitOfWork interface. It is recommended that instead of
	 * invoking this constructor, instead use the GuestRepositoryFactory class
	 * to create an instance.
	 *
	 * @param unitOfWork
	 *            An instance of a class that implements the IDatabaseUnitOfWork
	 *            interface. All methods invoked on the GuestRepository instance
	 *            being created will utilize this unit of work.
	 */
	public GuestRepository(IDatabaseUnitOfWork unitOfWork) {
		super(unitOfWork);
	}

	/**
	 * Retrieves a guest that is identified by the identifier provided.
	 *
	 * @param id
	 *            The identifier of the guest to be retrieved.
	 * @return An instance of Guest that has the identifier specified.
	 * @throws ResourceNotFoundException
	 *             Thrown when a guest with the identifier provided cannot be
	 *             found.
	 */
	public Guest getGuest(int id) throws ResourceNotFoundException {

		Guest guest = null;
		CallableStatement cStatement = null;
		ResultSet result = null;

		try {
			cStatement = this.getUnitOfWork().createCallableStatement("{CALL GetGuest(?)}");

			cStatement.setInt(1, id);
			result = cStatement.executeQuery();

			if (result.next()) {
				guest = new Guest();
				guest.setId(result.getInt("GUEST_ID"));
				guest.setGivenName(result.getString("FIRST_NAME"));
				guest.setSurName(result.getString("LAST_NAME"));
				guest.setDescription(result.getString("GUEST_DESCRIPTION"));
				guest.setDietaryRestrictions(result.getString("GUEST_DIETARY_RESTRICTIONS"));
				guest.setInviteCode(result.getString("INVITE_CODE"));

				result.getInt("RESERVATION_ID");
				if (!result.wasNull()) {
					Reservation reservation = new Reservation();
					reservation.setIsAttending(result.getBoolean("IS_ATTENDING"));
					reservation.setSubmittedDateTime(
						result.getTimestamp("DATETIME_SUBMITTED", Calendar.getInstance(TimeZone.getTimeZone("UTC"))));
					guest.setReservation(reservation);
				}
			}

			if (guest == null) {
				throw new ResourceNotFoundException("A guest with an ID of '" + id + "' could not be found.", id);
			}

			return guest;

		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new RuntimeException(sqlEx);
		} finally {
			// release resources needed.
			this.getUnitOfWork().destroyStatement(cStatement);
		}
	}

	/**
	 * Replaces the state of an existing guest with the state of the guest
	 * provided.
	 *
	 * @param guest
	 *            The desired state of the guest to update.
	 * @throws ResourceNotFoundException
	 *             Thrown when a guest with the identifier provided in the
	 *             desired state cannot be found.
	 */
	public void updateGuest(Guest guest) throws ResourceNotFoundException {

		CallableStatement cStatement = null;
		CallableStatement getGuest = null;

		try {

			cStatement = this.getUnitOfWork().createCallableStatement("{CALL UpdateGuest(?, ?, ?, ?, ?, ?, ?)}");
			cStatement.setInt(1, guest.getId());
			cStatement.setString(2, guest.getGivenName());
			cStatement.setString(3, guest.getSurName());
			cStatement.setString(4, guest.getDescription());
			cStatement.setString(5, guest.getDietaryRestrictions());
			cStatement.setString(6, guest.getInviteCode());

			getGuest = this.getUnitOfWork().createCallableStatement("{CALL GetGuest(?)}");
			getGuest.setInt(1, guest.getId());
			ResultSet result = getGuest.executeQuery();

			if (!result.next()) {
				throw new ResourceNotFoundException("A guest with an ID of '" + guest.getId() + "' could not be found.",
					guest.getId());
			}

			int reservationId = result.getInt("RESERVATION_ID");
			boolean hasReservation = !result.wasNull();
			boolean addingReservation = !hasReservation && guest.getReservation() != null;
			boolean updatingReservation = hasReservation && guest.getReservation() != null;
			boolean deletingReservation = hasReservation && guest.getReservation() == null;

			if (addingReservation) {
				reservationId = this.createReservation(guest.getReservation());
				cStatement.setInt(7, reservationId);
				cStatement.executeUpdate();
			} else if (updatingReservation) {
				this.updateReservation(reservationId, guest.getReservation());
				cStatement.setInt(7, reservationId);
				cStatement.executeUpdate();
			} else if (deletingReservation) {
				cStatement.setNull(7, java.sql.Types.INTEGER);
				cStatement.executeUpdate();
				this.deleteReservation(reservationId);
			} else {
				cStatement.setNull(7, java.sql.Types.INTEGER);
				cStatement.executeUpdate();
			}

		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new RuntimeException(sqlEx);
		} finally {
			// release resources needed.
			this.getUnitOfWork().destroyStatements(cStatement, getGuest);
		}
	}

	/**
	 * Deletes a guest that is identified by the identifier provided.
	 *
	 * @param id
	 *            The identifier of the guest to be deleted.
	 * @throws ResourceNotFoundException
	 *             Thrown when a guest with the identifier provided cannot be
	 *             found.
	 */
	public void deleteGuest(int id) throws ResourceNotFoundException {

		CallableStatement cStatement = null;
		CallableStatement getGuest = null;

		try {
			getGuest = this.getUnitOfWork().createCallableStatement("{CALL GetGuest(?)}");
			getGuest.setInt(1, id); //
			ResultSet result = getGuest.executeQuery();

			if (!result.next()) {
				throw new ResourceNotFoundException("A guest with an ID of '" + id + "' could not be found.", id);
			}

			int reservationId = result.getInt("RESERVATION_ID");
			boolean deleteReservation = !result.wasNull();

			cStatement = this.getUnitOfWork().createCallableStatement("{CALL DeleteGuest(?)}");
			cStatement.setInt(1, id);

			cStatement.executeUpdate();

			if (deleteReservation) {
				this.deleteReservation(reservationId);
			}

		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new RuntimeException(sqlEx);
		} finally {
			// release resources needed.
			this.getUnitOfWork().destroyStatements(cStatement, getGuest);
		}
	}

	/**
	 * Creates a new guest with the state provided.
	 *
	 * @param guest
	 *            The desired state of the guest to create.
	 * @return The identifier of the newly created guest.
	 */
	public int insertGuest(Guest guest) {

		CallableStatement cStatement = null;

		try {
			cStatement = this.getUnitOfWork().createCallableStatement("{CALL CreateGuest(?, ?, ?, ?, ?, ?, ?)}");
			cStatement.setString(1, guest.getGivenName());
			cStatement.setString(2, guest.getSurName());
			cStatement.setString(3, guest.getDescription());
			cStatement.setString(4, guest.getDietaryRestrictions());
			cStatement.setString(5, guest.getInviteCode());

			if (guest.getReservation() == null) {
				cStatement.setNull(6, java.sql.Types.INTEGER);
			} else { // create a reservation.
				int reservationId = this.createReservation(guest.getReservation());
				cStatement.setInt(6, reservationId);
			}

			cStatement.registerOutParameter("Id", Types.INTEGER);

			cStatement.executeUpdate();

			return cStatement.getInt("Id");

		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new RuntimeException(sqlEx);
		} finally {
			// release resources needed.
			this.getUnitOfWork().destroyStatement(cStatement);
		}
	}

	/**
	 * Retrieves all of the guests matching the provided search criteria. The
	 * search criteria is optional, and when omitted, all guests are returned.
	 * 
	 * @param searchCriteria
	 *            The search criteria that is used to filter the guests in the
	 *            repository.
	 * @return A collection of guests that match the search criteria if
	 *         provided; otherwise, a collection of all the guests in the
	 *         repository.
	 */
	public ArrayList<Guest> getGuests(GuestSearchQuery searchQuery) {

		ArrayList<Guest> guests = new ArrayList<Guest>();
		CallableStatement cStatement = null;
		ResultSet result = null;

		try {
			cStatement = this.getUnitOfWork().createCallableStatement("{CALL GetGuests(?, ?, ?, ?, ?)}");

			if (searchQuery != null) { 
				cStatement.setString(1, searchQuery.getInviteCode());
				cStatement.setString(2, searchQuery.getGivenName());
				cStatement.setString(3, searchQuery.getSurname());
				cStatement.setInt(4, searchQuery.getSkip() == null ? 0 : searchQuery.getSkip());
				cStatement.setInt(5, searchQuery.getTake() == null ? Integer.MAX_VALUE : searchQuery.getTake());
			} else {
				cStatement.setString(1, null);
				cStatement.setString(2, null);
				cStatement.setString(3, null);
				cStatement.setInt(4, 0);
				cStatement.setInt(5, Integer.MAX_VALUE);
			}

			result = cStatement.executeQuery();

			while (result.next()) {
				Guest guest = new Guest();
				guest.setId(result.getInt("GUEST_ID"));
				guest.setGivenName(result.getString("FIRST_NAME"));
				guest.setSurName(result.getString("LAST_NAME"));
				guest.setDescription(result.getString("GUEST_DESCRIPTION"));
				guest.setDietaryRestrictions(result.getString("GUEST_DIETARY_RESTRICTIONS"));
				guest.setInviteCode(result.getString("INVITE_CODE"));

				result.getInt("RESERVATION_ID");
				if (!result.wasNull()) {
					Reservation reservation = new Reservation();
					reservation.setIsAttending(result.getBoolean("IS_ATTENDING"));
					reservation.setSubmittedDateTime(
						result.getTimestamp("DATETIME_SUBMITTED", Calendar.getInstance(TimeZone.getTimeZone("UTC"))));
					guest.setReservation(reservation);
				}

				guests.add(guest);
			}

			return guests;
		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new RuntimeException(sqlEx);
		} finally {
			// release resources needed.
			this.getUnitOfWork().destroyStatement(cStatement);
		}
	}

	private int createReservation(Reservation reservation) {
		CallableStatement createReservation = null;
		try {
			createReservation = 
				this.getUnitOfWork().createCallableStatement("{CALL CreateReservation(?, ?)}");
			createReservation.setBoolean(1, reservation.getIsAttending());
			createReservation.registerOutParameter("Id", Types.INTEGER);
			createReservation.executeUpdate();
			return createReservation.getInt("Id");
		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new RuntimeException(sqlEx);
		} finally {
			// release resources needed.
			this.getUnitOfWork().destroyStatement(createReservation);
		}
	}

	private void updateReservation(int reservationId, Reservation reservation) {
		CallableStatement updateReservation = null;
		try {
			updateReservation = 
				this.getUnitOfWork().createCallableStatement("{CALL UpdateReservation(?, ?, ?)}");
			updateReservation.setInt(1, reservationId);
			updateReservation.setTimestamp(2, new Timestamp(reservation.getSubmittedDateTime().getTime()),
				Calendar.getInstance(TimeZone.getTimeZone("UTC")));
			updateReservation.setBoolean(3, reservation.getIsAttending());
			updateReservation.executeUpdate();
		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new RuntimeException(sqlEx);
		} finally {
			// release resources needed.
			this.getUnitOfWork().destroyStatement(updateReservation);
		}
	}

	// potentially create QueryBuilder class?
	// class Query
	// abstract class Clause
	// class SelectClause extends Clause
	// class FromClause extends Clause
	// --> innerJoin()
	// --> leftOuterJoin()
	// --> rightOuterJoin()
	// class WhereClause extends Clause
	// --> and()
	// --> or()
	// --> between()
	// class GroupByClause extends Clause
	// class HavingClause extends Clause
	// class LimitClause extends Clause

	private void deleteReservation(int reservationId) {
		CallableStatement deleteReservation = null;
		try {
			deleteReservation = 
				this.getUnitOfWork().createCallableStatement("{CALL DeleteReservation(?)}");
			deleteReservation.setInt(1, reservationId);
			deleteReservation.executeUpdate();
		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new RuntimeException(sqlEx);
		} finally {
			// release resources needed.
			this.getUnitOfWork().destroyStatement(deleteReservation);
		}
	}
}
