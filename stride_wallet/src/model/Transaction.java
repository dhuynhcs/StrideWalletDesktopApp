package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
	// fields
	private int transactionId;
	private Integer senderWalletId;
	private int receiverWalletId;
	private BigDecimal amount;
	private String note;
	private String transactionType; // ('send', 'request', 'reward', 'deposit', 'withdrawal')
	private String status; // ('pending', 'completed', 'failed', 'cancelled', 'requested', 'declined')
	private Timestamp createdAt;
	private Timestamp updatedAt;

	// constructor
	public Transaction() {
	}

	public Transaction(int transactionId, Integer senderWalletId, int receiverWalletId, BigDecimal amount, String note,
			String transactionType, String status, Timestamp createdAt, Timestamp updatedAt) {
		this.transactionId = transactionId;
		this.senderWalletId = senderWalletId;
		this.receiverWalletId = receiverWalletId;
		this.amount = amount;
		this.note = note;
		this.transactionType = transactionType;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	// getters and setters
	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public Integer getSenderWalletId() {
		return senderWalletId;
	}

	public void setSenderWalletId(Integer senderWalletId) {
		this.senderWalletId = senderWalletId;
	}

	public int getReceiverWalletId() {
		return receiverWalletId;
	}

	public void setReceiverWalletId(int receiverWalletId) {
		this.receiverWalletId = receiverWalletId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
		return "Transaction [transactionId=" + transactionId + ", senderWalletId=" + senderWalletId
				+ ", receiverWalletId=" + receiverWalletId + ", amount=" + amount + ", note=" + note
				+ ", transactionType=" + transactionType + ", status=" + status + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + "]";
	}
}