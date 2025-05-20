package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Wallet {
	// fields
	private int walletId;
	private int userId;
	private BigDecimal balance;
	private String currency;
	private String walletType;
	private boolean isDefault;
	private Timestamp createdAt;
	private Timestamp updatedAt;

	// constructor
	public Wallet() {
	}

	public Wallet(int walletId, int userId, BigDecimal balance, String currency, String walletType, boolean isDefault,
			Timestamp createdAt, Timestamp updatedAt) {
		this.walletId = walletId;
		this.userId = userId;
		this.balance = balance;
		this.currency = currency;
		this.walletType = walletType;
		this.isDefault = isDefault;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	// getters and setters
	public int getWalletId() {
		return walletId;
	}

	public void setWalletId(int walletId) {
		this.walletId = walletId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getWalletType() {
		return walletType;
	}

	public void setWalletType(String walletType) {
		this.walletType = walletType;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
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
		return "Wallet [walletId=" + walletId + ", userId=" + userId + ", balance=" + balance + ", currency=" + currency
				+ ", walletType=" + walletType + ", isDefault=" + isDefault + ", createdAt=" + createdAt + ", updatedAt="
				+ updatedAt + "]";
	}
}