package model;

import java.sql.Timestamp;

public class Contact {
	// fields
	private int userId;
	private String contactCashtag;
	private Timestamp createdAt;

	// constructor
	public Contact() {
	}

	public Contact(int userId, String contactCashtag, Timestamp createdAt) {
		this.userId = userId;
		this.contactCashtag = contactCashtag;
		this.createdAt = createdAt;
	}

	// getters and setters
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getContactCashtag() {
		return contactCashtag;
	}

	public void setContactCashtag(String contactCashtag) {
		this.contactCashtag = contactCashtag;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "Contact [userId=" + userId + ", contactCashtag=" + contactCashtag + ", createdAt=" + createdAt + "]";
	}
}