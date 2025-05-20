package controller;

import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import dao.UserDAO;
import dao.WalletDAO;
import dao.TransactionDAO;
import dao.ContactDAO;
import model.User;
import model.Wallet;
import model.Transaction;
import view.TransferView;
import my_util.RefreshUtility;

import java.awt.Component;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

//handle money transfer features
public class TransferController {
    // Constants
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("0.00");
    
    // UI Components and Data
    private final TransferView view;
    private final User currentUser;
    private Wallet userWallet;
    private CommonController parentController;
    
    // DAOs
    private final UserDAO userDAO;
    private final WalletDAO walletDAO;
    private final TransactionDAO transactionDAO;
    private final ContactDAO contactDAO;
    
    // Data
    private List<User> contacts;
    private List<User> allUsers;
    private List<Transaction> pendingRequests;
    
    // UI Controls
    private TableRowSorter<DefaultTableModel> contactSorter;
    private TableRowSorter<DefaultTableModel> directorySorter;
    
    public TransferController(TransferView view, User currentUser, Wallet userWallet) {
        this(view, currentUser, userWallet, null);
    }
    
    public TransferController(TransferView view, User currentUser, Wallet userWallet, CommonController parentController) {
        this.view = view;
        this.currentUser = currentUser;
        this.userWallet = userWallet;
        this.parentController = parentController;
        
        // Initialize DAOs
        this.userDAO = new UserDAO();
        this.walletDAO = new WalletDAO();
        this.transactionDAO = new TransactionDAO();
        this.contactDAO = new ContactDAO();
        
        // Setup UI
        updateBalanceDisplay();
        loadData();
        setupSearchFilters();
        initializeEventListeners();
    }
    
    private void loadData() {
        loadContacts();
        loadAllUsers();
        loadPendingRequests();
    }
    
    private void initializeEventListeners() {
        view.getSendButton().addActionListener(e -> handleMoneyAction(false));
        view.getRequestButton().addActionListener(e -> handleMoneyAction(true));
        view.getApproveButton().addActionListener(e -> handleApproveRequest());
        view.getDeclineButton().addActionListener(e -> handleDeclineRequest());
        view.getAddContactButton().addActionListener(e -> handleAddContact());
        view.getRemoveContactButton().addActionListener(e -> handleRemoveContact());
    }
    
    private void updateBalanceDisplay() {
        String balanceText = "Current Balance: $" + CURRENCY_FORMAT.format(userWallet.getBalance());
        
        for (Component comp : view.getComponents()) {
            if (comp instanceof JLabel && ((JLabel)comp).getText().startsWith("Current Balance:")) {
                ((JLabel)comp).setText(balanceText);
                break;
            }
        }
    }
    
    private void loadContacts() {
        contacts = contactDAO.getUserContactDetails(currentUser.getUserId());
        updateTableWithItems(view.getContactsTableModel(), contacts, this::mapContactToRow);
    }
    
    private void loadAllUsers() {
        allUsers = userDAO.getAllUsers();
        updateTableWithItems(view.getDirectoryTableModel(), allUsers, this::mapUserToDirectoryRow);
    }
    
    private void loadPendingRequests() {
        List<Transaction> transactions = transactionDAO.getTransactionsByUserId(currentUser.getUserId());
        pendingRequests = filterPendingRequests(transactions);
        updateTableWithItems(view.getRequestsTableModel(), pendingRequests, this::mapRequestToRow);
    }
    
    private Object[] mapContactToRow(User contact) {
        String name = contact.getFirstName() + " " + contact.getLastName();
        String cashtag = contact.getCashtag();
        return new Object[] {name, cashtag};
    }
    
    private Object[] mapUserToDirectoryRow(User user) {
        // Skip current user
        if (user.getUserId() == currentUser.getUserId()) {
            return null;
        }
        
        String name = user.getFirstName() + " " + user.getLastName();
        String cashtag = user.getCashtag();
        return new Object[] {name, cashtag};
    }
    
    private Object[] mapRequestToRow(Transaction request) {
        String requesterName = lookupRequesterName(request);
        String date = request.getCreatedAt().toString().substring(0, 10);
        String amount = "$" + request.getAmount().toString();
        String note = request.getNote();
        String status = request.getStatus();
        
        return new Object[] {requesterName, date, amount, note, status};
    }
    
    private String lookupRequesterName(Transaction request) {
        if (request.getReceiverWalletId() <= 0) {
            return "Unknown";
        }
        
        Wallet receiverWallet = walletDAO.getWalletById(request.getReceiverWalletId());
        if (receiverWallet == null) {
            return "Unknown";
        }
        
        User requester = userDAO.getUserById(receiverWallet.getUserId());
        return requester != null ? requester.getFirstName() + " " + requester.getLastName() : "Unknown";
    }
    
    private <T> void updateTableWithItems(DefaultTableModel model, List<T> items, Function<T, Object[]> rowMapper) {
        model.setRowCount(0);
        
        if (items == null || items.isEmpty()) {
            return;
        }
        
        for (T item : items) {
            Object[] rowData = rowMapper.apply(item);
            if (rowData != null) {
                model.addRow(rowData);
            }
        }
    }
    
    private List<Transaction> filterPendingRequests(List<Transaction> transactions) {
        List<Transaction> pendingRequests = new ArrayList<>();
        
        for (Transaction txn : transactions) {
            if (txn.getSenderWalletId() != null && 
                txn.getSenderWalletId() == userWallet.getWalletId() &&
                "request".equals(txn.getTransactionType()) &&
                "requested".equals(txn.getStatus())) {
                
                pendingRequests.add(txn);
            }
        }
        
        return pendingRequests;
    }
    
    private void setupSearchFilters() {
        // Setup table sorters
        contactSorter = new TableRowSorter<>(view.getContactsTableModel());
        view.getContactsTable().setRowSorter(contactSorter);
        
        directorySorter = new TableRowSorter<>(view.getDirectoryTableModel());
        view.getDirectoryTable().setRowSorter(directorySorter);
        
        // Setup search listeners
        setupSearchListener(view.getContactsSearchField(), this::filterContacts);
        setupSearchListener(view.getDirectorySearchField(), this::filterDirectory);
    }
    
    private void setupSearchListener(JTextField field, Runnable filterAction) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterAction.run(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterAction.run(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterAction.run(); }
        });
    }
    
    private void filterContacts() {
        applyFilter(view.getContactsSearchField().getText().trim(), contactSorter);
    }
    
    private void filterDirectory() {
        applyFilter(view.getDirectorySearchField().getText().trim(), directorySorter);
    }
    
    private void applyFilter(String searchText, TableRowSorter<DefaultTableModel> sorter) {
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(searchText)));
        }
    }
    
   
    
    private void handleMoneyAction(boolean isRequest) {
        // Get input values
        String targetCashtag = view.getRecipientField().getText().trim();
        String amountText = view.getAmountField().getText().trim();
        String note = view.getNoteField().getText().trim();
        
        // Validate inputs
        if (targetCashtag.isEmpty()) {
            showError("Please enter a " + (isRequest ? "user" : "recipient") + " cashtag");
            return;
        }
        
        if (amountText.isEmpty()) {
            showError("Please enter an amount");
            return;
        }
        
        // Parse amount
        BigDecimal amount = parseAmount(amountText);
        if (amount == null) return;
        
        // Check balance for sending (not needed for requesting)
        if (!isRequest && amount.compareTo(userWallet.getBalance()) > 0) {
            showError("Insufficient funds");
            return;
        }
        
        // Find target user
        User targetUser = userDAO.getUserByCashtag(targetCashtag);
        if (targetUser == null) {
            showError("User not found");
            return;
        }
        
        // Prevent sending/requesting to/from self
        if (targetUser.getUserId() == currentUser.getUserId()) {
            showError("Cannot " + (isRequest ? "request money from" : "send money to") + " yourself");
            return;
        }
        
        // Get target user's wallet
        Wallet targetWallet = walletDAO.getDefaultWallet(targetUser.getUserId());
        if (targetWallet == null) {
            showError("User has no wallet");
            return;
        }
        
        // Process transaction or request
        if (isRequest) {
            // Create request
            boolean success = transactionDAO.createPaymentRequest(
                userWallet.getWalletId(),   // Requestor's wallet
                targetWallet.getWalletId(),  // Requestee's wallet
                amount,
                note
            );
            
            handleTransactionResult(success, 
                "Successfully requested $" + amount + " from " + targetCashtag,
                "Request Sent", 
                "Failed to send request", 
                targetCashtag);
        } else {
            // Send money
            processTransaction(
                userWallet.getWalletId(),
                targetWallet.getWalletId(),
                amount, 
                note,
                "Successfully sent $" + amount + " to " + targetCashtag, 
                targetCashtag
            );
        }
    }
    
    private void processTransaction(Integer senderWalletId, int receiverWalletId, BigDecimal amount, 
                                   String note, String successMessage, String recipientCashtag) {
        boolean success = transactionDAO.createMoneyTransfer(senderWalletId, receiverWalletId, amount, note);
        handleTransactionResult(success, successMessage, "Transfer Complete", "Failed to complete transaction", recipientCashtag);
    }
    
    private BigDecimal parseAmount(String amountText) {
        try {
            BigDecimal amount = new BigDecimal(amountText);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Amount must be positive");
                return null;
            }
            return amount;
        } catch (NumberFormatException ex) {
            showError("Invalid amount format");
            return null;
        }
    }
    
    private void handleTransactionResult(boolean success, String successMessage, String successTitle, 
                                        String failureMessage, String contactCashtag) {
        if (success) {
            JOptionPane.showMessageDialog(view, successMessage, successTitle, JOptionPane.INFORMATION_MESSAGE);
            
            // Clear form fields
            view.getAmountField().setText("");
            view.getNoteField().setText("");
            
            // Add contact if not exists
            maybeAddContact(contactCashtag);
            
            // Refresh views
            RefreshUtility.refreshAllViews(parentController, currentUser);
        } else {
            showError(failureMessage + ". Please try again.");
        }
    }
    
    private void maybeAddContact(String cashtag) {
        if (!contactDAO.contactExists(currentUser.getUserId(), cashtag)) {
            contactDAO.addContact(currentUser.getUserId(), cashtag);
        }
    }
    
    private void handleApproveRequest() {
        Transaction request = getSelectedRequest("Please select a request to approve");
        if (request == null) return;
        
        // Get display info
        String amountString = (String) view.getRequestsTable().getValueAt(
            view.getRequestsTable().getSelectedRow(), 2);
        String requesterName = (String) view.getRequestsTable().getValueAt(
            view.getRequestsTable().getSelectedRow(), 0);
        
        // Confirm
        if (!confirmAction("Are you sure you want to approve payment of " + amountString + 
                          " to " + requesterName + "?", "Confirm Payment")) {
            return;
        }
        
        // Process
        boolean success = transactionDAO.respondToRequest(request.getTransactionId(), true);
        
        if (success) {
            JOptionPane.showMessageDialog(view, "Payment approved successfully", 
                                         "Payment Complete", JOptionPane.INFORMATION_MESSAGE);
            RefreshUtility.refreshAllViews(parentController, currentUser);
        } else {
            showError("Failed to process payment. Please try again.");
        }
    }
    
    private void handleDeclineRequest() {
        Transaction request = getSelectedRequest("Please select a request to decline");
        if (request == null) return;
        
        // Get requester name
        String requesterName = (String) view.getRequestsTable().getValueAt(
            view.getRequestsTable().getSelectedRow(), 0);
        
        // Confirm
        if (!confirmAction("Are you sure you want to decline this request from " + 
                          requesterName + "?", "Confirm Decline")) {
            return;
        }
        
        // Process
        boolean success = transactionDAO.respondToRequest(request.getTransactionId(), false);
        
        if (success) {
            JOptionPane.showMessageDialog(view, "Request declined", 
                                         "Request Declined", JOptionPane.INFORMATION_MESSAGE);
            RefreshUtility.refreshAllViews(parentController, currentUser);
        } else {
            showError("Failed to decline request. Please try again.");
        }
    }
    
    private Transaction getSelectedRequest(String errorMessage) {
        int selectedRow = view.getRequestsTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, errorMessage, "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
        // Convert to model index
        int modelRow = view.getRequestsTable().convertRowIndexToModel(selectedRow);
        
        // Validate index
        if (modelRow >= pendingRequests.size()) {
            showError("Invalid selection");
            return null;
        }
        
        return pendingRequests.get(modelRow);
    }
    
    private void handleAddContact() {
        int selectedRow = view.getDirectoryTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Please select a user to add as contact", 
                                         "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get selection
        int modelRow = view.getDirectoryTable().convertRowIndexToModel(selectedRow);
        String name = (String) view.getDirectoryTableModel().getValueAt(modelRow, 0);
        String cashtag = (String) view.getDirectoryTableModel().getValueAt(modelRow, 1);
        
        // Check if exists
        if (contactDAO.contactExists(currentUser.getUserId(), cashtag)) {
            JOptionPane.showMessageDialog(view, name + " is already in your contacts", 
                                         "Contact Exists", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Add contact
        boolean success = contactDAO.addContact(currentUser.getUserId(), cashtag);
        
        if (success) {
            JOptionPane.showMessageDialog(view, name + " has been added to your contacts", 
                                         "Contact Added", JOptionPane.INFORMATION_MESSAGE);
            RefreshUtility.refreshAllViews(parentController, currentUser);
        } else {
            showError("Failed to add contact. Please try again.");
        }
    }
    
    private void handleRemoveContact() {
        int selectedRow = view.getContactsTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Please select a contact to remove", 
                                         "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get selection
        int modelRow = view.getContactsTable().convertRowIndexToModel(selectedRow);
        String name = (String) view.getContactsTableModel().getValueAt(modelRow, 0);
        String cashtag = (String) view.getContactsTableModel().getValueAt(modelRow, 1);
        
        // Confirm
        if (!confirmAction("Are you sure you want to remove " + name + 
                          " from your contacts?", "Confirm Removal")) {
            return;
        }
        
        // Remove
        boolean success = contactDAO.removeContact(currentUser.getUserId(), cashtag);
        
        if (success) {
            JOptionPane.showMessageDialog(view, name + " has been removed from your contacts", 
                                         "Contact Removed", JOptionPane.INFORMATION_MESSAGE);
            RefreshUtility.refreshAllViews(parentController, currentUser);
        } else {
            showError("Failed to remove contact. Please try again.");
        }
    }
    
    private boolean confirmAction(String message, String title) {
        int choice = JOptionPane.showConfirmDialog(view, message, title, JOptionPane.YES_NO_OPTION);
        return choice == JOptionPane.YES_OPTION;
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void updateWallet(Wallet updatedWallet) {
        this.userWallet = updatedWallet;
        updateBalanceDisplay();
    }
    
    public void refreshData() {
        userWallet = walletDAO.getDefaultWallet(currentUser.getUserId());
        updateBalanceDisplay();
        loadData();
    }
}