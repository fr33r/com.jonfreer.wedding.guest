package com.jonfreer.wedding.infrastructure.repositories;

import org.junit.Before;
import org.junit.Test;

import com.jonfreer.wedding.annotations.WhiteBox;
import com.jonfreer.wedding.infrastructure.exceptions.ResourceNotFoundException;
import com.jonfreer.wedding.infrastructure.queries.GuestSearchQuery;
import com.jonfreer.wedding.domain.interfaces.repositories.IGuestRepository;
import com.jonfreer.wedding.domain.interfaces.unitofwork.IDatabaseUnitOfWork;
import com.mysql.jdbc.CallableStatement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class GuestRepository_WhiteBoxTest {

	private IDatabaseUnitOfWork databaseUnitOfWorkMock;

	@Before
	public void setUp() throws Exception {
		this.databaseUnitOfWorkMock = mock(IDatabaseUnitOfWork.class);
	}

	/**
	 * Verifies the code path taken for retrieving a guest that does not have a
	 * reservation associated to it.
	 * 
	 * @throws SQLException
	 *             Fails the test.
	 * @throws ResourceNotFoundException
	 *             Fails the test.
	 */
	@WhiteBox
	@Test
	public void getGuest_verifies_guestFoundWithReservation() throws SQLException, ResourceNotFoundException {

		// constants.
		final int id = 1;

		// create mocks.
		CallableStatement callableStatementMock = mock(CallableStatement.class);
		ResultSet resultSetMock = mock(ResultSet.class);

		// stub mocks.
		when(resultSetMock.next()).thenReturn(true);
		when(resultSetMock.wasNull()).thenReturn(false);
		when(this.databaseUnitOfWorkMock.createCallableStatement("{CALL GetGuest(?)}"))
			.thenReturn(callableStatementMock);
		when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
		when(callableStatementMock.isClosed()).thenReturn(false);

		// action.
		IGuestRepository guestRepository = new GuestRepository(this.databaseUnitOfWorkMock);
		guestRepository.getGuest(id);

		// verify.
		verify(this.databaseUnitOfWorkMock, times(1)).createCallableStatement("{CALL GetGuest(?)}");
		verify(this.databaseUnitOfWorkMock, times(1)).destroyStatement(callableStatementMock);
		verifyNoMoreInteractions(this.databaseUnitOfWorkMock);

		verify(callableStatementMock, times(1)).setInt(1, id);
		verify(callableStatementMock, times(1)).executeQuery();

		// these aren't working anymore due to polymorphic call on
		// destroyStatement.

		// verify(callableStatementMock, times(1)).isClosed();
		// verify(callableStatementMock, times(1)).close();
		verifyNoMoreInteractions(callableStatementMock);

		verify(resultSetMock, times(1)).next();
		verify(resultSetMock, times(1)).getInt("GUEST_ID");
		verify(resultSetMock, times(1)).getString("FIRST_NAME");
		verify(resultSetMock, times(1)).getString("LAST_NAME");
		verify(resultSetMock, times(1)).getString("GUEST_DESCRIPTION");
		verify(resultSetMock, times(1)).getString("GUEST_DIETARY_RESTRICTIONS");
		verify(resultSetMock, times(1)).getString("INVITE_CODE");
		verify(resultSetMock, times(1)).getInt("RESERVATION_ID");
		verify(resultSetMock, times(1)).getBoolean("IS_ATTENDING");
		verify(resultSetMock, times(1)).getTimestamp(eq("DATETIME_SUBMITTED"), any(Calendar.class));
		verify(resultSetMock, times(1)).wasNull();
		verifyNoMoreInteractions(resultSetMock);
	}

	/**
	 * Verifies the code path taken for retrieving a guest that does have a
	 * reservation associated to it.
	 * 
	 * @throws SQLException
	 *             Fails the test.
	 * @throws ResourceNotFoundException
	 *             Fails the test.
	 */
	@WhiteBox
	@Test
	public void getGuest_verifies_guestFoundWithoutReservation() throws SQLException, ResourceNotFoundException {

		// constants.
		final int id = 1;

		// create mocks.
		CallableStatement callableStatementMock = mock(CallableStatement.class);
		ResultSet resultSetMock = mock(ResultSet.class);

		// stub mocks.
		when(resultSetMock.next()).thenReturn(true);
		when(resultSetMock.wasNull()).thenReturn(true);
		when(this.databaseUnitOfWorkMock.createCallableStatement("{CALL GetGuest(?)}"))
			.thenReturn(callableStatementMock);
		when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);

		// action.
		IGuestRepository guestRepository = new GuestRepository(this.databaseUnitOfWorkMock);
		guestRepository.getGuest(id);

		// verify.
		verify(this.databaseUnitOfWorkMock, times(1)).createCallableStatement("{CALL GetGuest(?)}");
		verify(this.databaseUnitOfWorkMock, times(1)).destroyStatement(callableStatementMock);
		verifyNoMoreInteractions(this.databaseUnitOfWorkMock);

		verify(callableStatementMock, times(1)).setInt(1, id);
		verify(callableStatementMock, times(1)).executeQuery();
		// these aren't working anymore due to polymorphic call on
		// destroyStatement.

		// verify(callableStatementMock, times(1)).isClosed();
		// verify(callableStatementMock, times(1)).close();
		verifyNoMoreInteractions(callableStatementMock);

		verify(resultSetMock, times(1)).next();
		verify(resultSetMock, times(1)).getInt("GUEST_ID");
		verify(resultSetMock, times(1)).getString("FIRST_NAME");
		verify(resultSetMock, times(1)).getString("LAST_NAME");
		verify(resultSetMock, times(1)).getString("GUEST_DESCRIPTION");
		verify(resultSetMock, times(1)).getString("GUEST_DIETARY_RESTRICTIONS");
		verify(resultSetMock, times(1)).getString("INVITE_CODE");
		verify(resultSetMock, times(1)).getInt("RESERVATION_ID");
		verify(resultSetMock, times(1)).wasNull();
		verifyNoMoreInteractions(resultSetMock);
	}

	/**
	 * Verifies the code path taken when search criteria is not provided.
	 * 
	 * @throws SQLException
	 *             Fails test.
	 */
	@WhiteBox
	@Test
	public void getGuests_verifies_allGuests() throws SQLException {

		// create mocks.
		CallableStatement callableStatementMock = mock(CallableStatement.class);
		ResultSet resultSetMock = mock(ResultSet.class);

		// stub mocks.
		when(resultSetMock.next()).thenReturn(true).thenReturn(false);
		when(this.databaseUnitOfWorkMock.createCallableStatement("{CALL GetGuests(?, ?, ?)}"))
			.thenReturn(callableStatementMock);

		when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);

		// action.
		IGuestRepository guestRepository = new GuestRepository(this.databaseUnitOfWorkMock);
		guestRepository.getGuests(null);

		// verify.
		verify(this.databaseUnitOfWorkMock, times(1)).createCallableStatement("{CALL GetGuests(?, ?, ?)}");
		verify(this.databaseUnitOfWorkMock, times(1)).destroyStatement(callableStatementMock);
		verifyNoMoreInteractions(this.databaseUnitOfWorkMock);

		verify(callableStatementMock, times(1)).setString(1, null);
		verify(callableStatementMock, times(1)).setString(2, null);
		verify(callableStatementMock, times(1)).setString(3, null);
		verify(callableStatementMock, times(1)).executeQuery();
		// these aren't working anymore due to polymorphic call on
		// destroyStatement.
		
		//verify(callableStatementMock, times(1)).isClosed();
		//verify(callableStatementMock, times(1)).close();
		verifyNoMoreInteractions(callableStatementMock);

		verify(resultSetMock, times(2)).next();
		verify(resultSetMock, times(1)).getInt("GUEST_ID");
		verify(resultSetMock, times(1)).getString("FIRST_NAME");
		verify(resultSetMock, times(1)).getString("LAST_NAME");
		verify(resultSetMock, times(1)).getString("GUEST_DESCRIPTION");
		verify(resultSetMock, times(1)).getString("GUEST_DIETARY_RESTRICTIONS");
		verify(resultSetMock, times(1)).getString("INVITE_CODE");
		verify(resultSetMock, times(1)).getInt("RESERVATION_ID");
		verify(resultSetMock, times(1)).getBoolean("IS_ATTENDING");
		verify(resultSetMock, times(1)).getTimestamp(eq("DATETIME_SUBMITTED"), any(Calendar.class));
		verify(resultSetMock, times(1)).wasNull();
		verifyNoMoreInteractions(resultSetMock);

	}

	/**
	 * Verifies the code path taken when search criteria is provided and there
	 * are guests that match the criteria.
	 * 
	 * @throws SQLException
	 *             Fails test.
	 */
	@WhiteBox
	@Test
	public void getGuests_verifies_matchingGuests() throws SQLException {

		// constants.
		final String firstName = "Jon";
		final String lastName = "Freer";
		final String inviteCode = "PA000";
		final Integer skip = null;
		final Integer take = null;
		final GuestSearchQuery searchQuery = 
			new GuestSearchQuery(firstName, lastName, inviteCode, skip, take);
		
		// create mocks.
		CallableStatement callableStatementMock = mock(CallableStatement.class);
		ResultSet resultSetMock = mock(ResultSet.class);

		when(resultSetMock.next()).thenReturn(true).thenReturn(false);
		when(this.databaseUnitOfWorkMock.createCallableStatement("{CALL GetGuests(?, ?, ?)}"))
			.thenReturn(callableStatementMock);

		when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);

		// action.
		IGuestRepository guestRepository = new GuestRepository(this.databaseUnitOfWorkMock);
		guestRepository.getGuests(searchQuery);

		// verify.
		verify(this.databaseUnitOfWorkMock, times(1)).createCallableStatement("{CALL GetGuests(?, ?, ?)}");
		verify(this.databaseUnitOfWorkMock, times(1)).destroyStatement(callableStatementMock);
		verifyNoMoreInteractions(this.databaseUnitOfWorkMock);

		verify(callableStatementMock, times(1)).setString(1, searchQuery.getInviteCode());
		verify(callableStatementMock, times(1)).setString(2, searchQuery.getGivenName());
		verify(callableStatementMock, times(1)).setString(3, searchQuery.getSurname());
		verify(callableStatementMock, times(1)).setInt(4, searchQuery.getSkip() == null ? 0 : searchQuery.getSkip());
		verify(callableStatementMock, times(1)).setInt(5, searchQuery.getTake() == null ? Integer.MAX_VALUE : searchQuery.getTake());
		verify(callableStatementMock, times(1)).executeQuery();
		// these aren't working anymore due to polymorphic call on
		// destroyStatement.
		
		//verify(callableStatementMock, times(1)).isClosed();
		//verify(callableStatementMock, times(1)).close();
		verifyNoMoreInteractions(callableStatementMock);

		verify(resultSetMock, times(2)).next();
		verify(resultSetMock, times(1)).getInt("GUEST_ID");
		verify(resultSetMock, times(1)).getString("FIRST_NAME");
		verify(resultSetMock, times(1)).getString("LAST_NAME");
		verify(resultSetMock, times(1)).getString("GUEST_DESCRIPTION");
		verify(resultSetMock, times(1)).getString("GUEST_DIETARY_RESTRICTIONS");
		verify(resultSetMock, times(1)).getString("INVITE_CODE");
		verify(resultSetMock, times(1)).getInt("RESERVATION_ID");
		verify(resultSetMock, times(1)).getBoolean("IS_ATTENDING");
		verify(resultSetMock, times(1)).getTimestamp(eq("DATETIME_SUBMITTED"), any(Calendar.class));
		verify(resultSetMock, times(1)).wasNull();
		verifyNoMoreInteractions(resultSetMock);
	}

	/**
	 * Verifies the code path taken when search criteria is provided and there
	 * are no guests that match the criteria.
	 * 
	 * @throws SQLException
	 *             Fails test.
	 */
	@WhiteBox
	@Test
	public void getGuests_verifies_noMatchingGuests() throws SQLException {

		// constants.
		final String firstName = "Jon";
		final String lastName = "Freer";
		final String inviteCode = "PA000";
		final Integer skip = null;
		final Integer take = null;
		final GuestSearchQuery searchQuery = 
			new GuestSearchQuery(firstName, lastName, inviteCode, skip, take);

		// create mocks.
		CallableStatement callableStatementMock = mock(CallableStatement.class);
		ResultSet resultSetMock = mock(ResultSet.class);

		when(resultSetMock.next()).thenReturn(false);
		when(this.databaseUnitOfWorkMock.createCallableStatement("{CALL GetGuests(?, ?, ?)}"))
			.thenReturn(callableStatementMock);

		when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);

		// action.
		IGuestRepository guestRepository = new GuestRepository(this.databaseUnitOfWorkMock);
		guestRepository.getGuests(searchQuery);

		// verify.
		verify(this.databaseUnitOfWorkMock, times(1)).createCallableStatement("{CALL GetGuests(?, ?, ?)}");
		verify(this.databaseUnitOfWorkMock, times(1)).destroyStatement(callableStatementMock);
		verifyNoMoreInteractions(this.databaseUnitOfWorkMock);

		verify(callableStatementMock, times(1)).setString(1, searchQuery.getInviteCode());
		verify(callableStatementMock, times(1)).setString(2, searchQuery.getGivenName());
		verify(callableStatementMock, times(1)).setString(3, searchQuery.getSurname());
		verify(callableStatementMock, times(1)).setInt(4, searchQuery.getSkip() == null ? 0 : searchQuery.getSkip());
		verify(callableStatementMock, times(1)).setInt(5, searchQuery.getTake() == null ? Integer.MAX_VALUE : searchQuery.getTake());
		verify(callableStatementMock, times(1)).executeQuery();
		// these aren't working anymore due to polymorphic call on
		// destroyStatement.
		
		//verify(callableStatementMock, times(1)).isClosed();
		//verify(callableStatementMock, times(1)).close();
		verifyNoMoreInteractions(callableStatementMock);

		verify(resultSetMock, times(1)).next();
		verify(resultSetMock, never()).getInt("GUEST_ID");
		verify(resultSetMock, never()).getString("FIRST_NAME");
		verify(resultSetMock, never()).getString("LAST_NAME");
		verify(resultSetMock, never()).getString("GUEST_DESCRIPTION");
		verify(resultSetMock, never()).getString("GUEST_DIETARY_RESTRICTIONS");
		verify(resultSetMock, never()).getString("INVITE_CODE");
		verify(resultSetMock, never()).getInt("RESERVATION_ID");
		verify(resultSetMock, never()).wasNull();
		verifyNoMoreInteractions(resultSetMock);
	}
}
