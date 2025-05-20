package controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JOptionPane;

import dao.TransactionDAO;
import dao.WalletDAO;
import model.Transaction;
import model.User;
import model.Wallet;
import view.ProfileView;
import my_util.RefreshUtility;

public class ProfileController {

	// Constants
	private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("0.00");
	private static final String DEFAULT_DEPOSIT_NOTE = "Deposit from bank";

	// Components and data
	private final ProfileView view;
	private final User model;
	private Wallet userWallet;
	private final CommonController parentController;

	// DAOs
	private final WalletDAO walletDAO;
	private final TransactionDAO transactionDAO;

	// Controller constructor for standalone usage
	public ProfileController(ProfileView view, User model, Wallet userWallet, WalletDAO walletDAO,
			TransactionDAO transactionDAO) {
		this(view, model, userWallet, walletDAO, transactionDAO, null);
	}

	// Constructor with parent controller reference
	public ProfileController(ProfileView view, User model, Wallet userWallet, WalletDAO walletDAO,
			TransactionDAO transactionDAO, CommonController parentController) {
		this.view = view;
		this.model = model;
		this.userWallet = userWallet;
		this.walletDAO = walletDAO;
		this.transactionDAO = transactionDAO;
		this.parentController = parentController;

		initializeEventListeners();
	}

	private void initializeEventListeners() {
		view.getDepositButton().addActionListener(e -> handleDeposit());
	}

	private void handleDeposit() {
		String amountText = view.getDepositAmountField().getText().trim();

		// Validate input
		if (amountText.isEmpty()) {
			showError("Please enter an amount to deposit", "Input Error");
			return;
		}

		// Parse and validate amount
		BigDecimal amount = parseAmount(amountText);
		if (amount == null)
			return;

		// Process deposit
		boolean success = processDeposit(amount);

		if (success) {
			// Show success message
			JOptionPane.showMessageDialog(view, "Successfully deposited $" + amount, "Deposit Successful",
					JOptionPane.INFORMATION_MESSAGE);

			// Clear input field
			view.getDepositAmountField().setText("");

			// Use RefreshUtility to refresh all views
			RefreshUtility.refreshAllViews(parentController, model);
		} else {
			showError("Failed to process deposit. Please try again.", "Deposit Failed");
		}
	}

	private BigDecimal parseAmount(String amountText) {
		try {
			BigDecimal amount = new BigDecimal(amountText);

			// Check if amount is positive
			if (amount.compareTo(BigDecimal.ZERO) <= 0) {
				showError("Deposit amount must be greater than zero", "Invalid Amount");
				return null;
			}
			return amount;
		} catch (NumberFormatException e) {
			showError("Please enter a valid number for the deposit amount", "Invalid Input");
			return null;
		}
	}

	private boolean processDeposit(BigDecimal amount) {
		// Use TransactionDAO to create a deposit transaction
		// For deposits, sender wallet ID is null (money coming from outside the system)
		return transactionDAO.createMoneyTransfer(null, // No sender wallet for deposits
				userWallet.getWalletId(), // User's wallet as receiver
				amount, // Deposit amount
				DEFAULT_DEPOSIT_NOTE // Default note for bank deposits
		);
	}

	private void showError(String message, String title) {
		JOptionPane.showMessageDialog(view, message, title, JOptionPane.ERROR_MESSAGE);
	}

	// Update the balance display with proper formatting
	private void updateBalanceDisplay() {
		view.getBalanceLabel().setText("$" + CURRENCY_FORMAT.format(userWallet.getBalance()));
	}

	// Method to update the wallet balance
	public void updateWallet(Wallet updatedWallet) {
		this.userWallet = updatedWallet;
		updateBalanceDisplay();
	}

	// Method to refresh all data
	public void refreshData() {
		// Reload wallet from database
		Wallet freshWallet = walletDAO.getDefaultWallet(model.getUserId());
		if (freshWallet != null) {
			userWallet = freshWallet;

			// Only update parent if there's an actual change
			if (parentController != null
					&& userWallet.getBalance().compareTo(parentController.getCurrentWalletBalance()) != 0) {
				parentController.updateBalance(userWallet.getBalance());
			}
		}

		// Reload transactions
		List<Transaction> transactions = transactionDAO.getTransactionsByUserId(model.getUserId());

		// Update the balance display
		updateBalanceDisplay();

		// Update the view
		view.updateData(userWallet, transactions);
	}

}