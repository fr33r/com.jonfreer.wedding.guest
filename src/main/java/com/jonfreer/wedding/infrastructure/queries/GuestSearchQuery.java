package com.jonfreer.wedding.infrastructure.queries;

/**
 * Represents query that is used when searching through
 * the Guest resources.
 * @author jonfreer
 */
public class GuestSearchQuery {

	private String givenName;
	private String surname;
	private String inviteCode;
	private Integer skip;
	private Integer take;
	
	public GuestSearchQuery(String givenName, String surname, String inviteCode, Integer skip, Integer take) {
		this.givenName = givenName;
		this.surname = surname;
		this.inviteCode = inviteCode;
		this.skip = skip;
		this.take = take;
	}
	
	/**
     * Retrieves the given name for the GuestSearchQuery.
     * @return The given name for the GuestSearchQuery.
     */
	public String getGivenName() {
		return givenName;
	}
	
	/**
     * Alters the given name (first name) of the search query to match
     * the given name provided.
     * @param givenName The desired given name for the search query.
     */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	
	/**
     * Retrieves the surname for the GuestSearchQuery.
     * @return The surname for the GuestSearchQuery.
     */
	public String getSurname() {
		return surname;
	}
	
	/**
     * Alters the surname (last name) of the search query to match
     * the surname provided.
     * @param surname The desired surname for the search query.
     */
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	/**
     * Retrieves the invite code for the GuestSearchQuery.
     * @return The invite code for the GuestSearchQuery.
     */
	public String getInviteCode() {
		return inviteCode;
	}
	
	/**
     * Alters the invite code of the search query to match the
     * invite code provided.
     * @param inviteCode The desired invite code for the search query.
     */
	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}
	
	/**
	 * Retrieves the amount of results to skip (or offset). Commonly utilized
	 * to paginate the results.
	 * 
	 * @return The number of results to skip (or offset).
	 */
	public Integer getSkip() {
		return skip;
	}
	
	/**
	 * Alters the amount of results to skip (or offset). Commonly utilized
	 * to paginate the results.
	 * @param skip The number of results to skip (or offset).
	 */
	public void setSkip(Integer skip) {
		this.skip = skip;
	}
	
	/**
	 * Retrieves the number of results to take (or limit). Commonly utilized
	 * to paginate the results.
	 * @return The number of results to take (or limit).
	 */
	public Integer getTake() {
		return take;
	}
	
	/**
	 * Alters the number of results to take (or limit). Commonly utilized
	 * to paginate the results.
	 * @param take The number of results to take (or limit).
	 */
	public void setTake(Integer take) {
		this.take = take;
	}
}
