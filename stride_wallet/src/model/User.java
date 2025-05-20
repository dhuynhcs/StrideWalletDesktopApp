package model;

import java.sql.Timestamp;

public class User {
	// fields
	private int userId;
	private String phoneNumber;
	private String email;
	private String firstName;
	private String lastName;
	private String cashtag;
	private String passwordHash;
	private Timestamp createdAt;
	private Timestamp updatedAt;

	// constructor
	public User() {
	}

	public User(int userId, String phoneNumber, String email, String firstName, String lastName, String cashtag,
			String passwordHash, Timestamp createdAt, Timestamp updatedAt) {
		this.userId = userId;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.cashtag = cashtag;
		this.passwordHash = passwordHash;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	// getters and setters
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCashtag() {
		return cashtag;
	}

	public void setCashtag(String cashtag) {
		this.cashtag = cashtag;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", phoneNumber=" + phoneNumber + ", email=" + email + ", firstName="
				+ firstName + ", lastName=" + lastName + ", cashtag=" + cashtag + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + "]";
	}
}