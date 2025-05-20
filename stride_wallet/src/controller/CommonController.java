package controller;

import java.awt.CardLayout;
import java.awt.Component;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import dao.TransactionDAO;
import dao.UserDAO;
import dao.WalletDAO;
import dao.ContactDAO;
import model.User;
import model.Wallet;
import model.Transaction;

import view.*;
import my_util.RefreshUtility;

public class CommonController {
    // Constants
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("0.00");
    
    // Core components and data
    private final CommonView view;
    private final User model;
    private Wallet userWallet;
    
    // DAOs
    private final WalletDAO walletDAO;
    private final TransactionDAO transactionDAO;
    private final UserDAO userDAO;
    private final ContactDAO contactDAO;
    
    // Child controllers
    private ProfileController profileController;
    private TransferController transferController;

    //Controller constructor
    public CommonController(CommonView view, User model) {
        this.view = view;
        this.model = model;
        
        // Initialize DAOs
        this.walletDAO = new WalletDAO();
        this.transactionDAO = new TransactionDAO();
        this.userDAO = new UserDAO();
        this.contactDAO = new ContactDAO();
        
        // Get user's wallet
        this.userWallet = walletDAO.getDefaultWallet(model.getUserId());
        
        // Initialize UI
        initializeEventListeners();
        updateUserInfo();
        setupInitialView();
    }

    //Initialize all event listeners
    private void initializeEventListeners() {
        setupTransferButtonListener();
        setupProfileButtonListener();
        setupLogoutButtonListener();
    }
    
    // Set up the initial views and panels
    private void setupInitialView() {
        // Get main content panel
        JPanel mainContentPanel = view.getMainContentPanel();
        CardLayout cardLayout = (CardLayout) mainContentPanel.getLayout();
        
        // Create and add profile view
        ProfileView profileView = createProfileView();
        profileView.setName("profilePanel");
        mainContentPanel.add(profileView, "profile");
        
        // Create and add transfer view
        TransferView transferView = createTransferView();
        transferView.setName("transferPanel");
        mainContentPanel.add(transferView, "transfer");
        
        // Show the profile view by default
        cardLayout.show(mainContentPanel, "profile");
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
    
    // Create and initialize profile view
    private ProfileView createProfileView() {
        // Get user's transactions
        List<Transaction> transactions = transactionDAO.getTransactionsByUserId(model.getUserId());
        
        // Create profile view
        ProfileView profileView = new ProfileView(model, userWallet, transactions);
        
        // Create and link profile controller with reference to this controller
        profileController = new ProfileController(profileView, model, userWallet, walletDAO, transactionDAO, this);
        
        return profileView;
    }
    
    //Create and initialize transfer view
    private TransferView createTransferView() {
        // Create the basic view
        TransferView transferView = new TransferView();
        
        // Populate contacts table
        populateTable(
            transferView.getContactsTableModel(),
            contactDAO.getUserContactDetails(model.getUserId()),
            this::mapUserToRow
        );
        
        // Populate directory table (exclude current user)
        populateTable(
            transferView.getDirectoryTableModel(),
            userDAO.getAllUsers(),
            user -> user.getUserId() != model.getUserId() ? mapUserToRow(user) : null
        );
        
        // Get user's transactions and filter for pending requests
        List<Transaction> transactions = transactionDAO.getTransactionsByUserId(model.getUserId());
        List<Transaction> pendingRequests = filterPendingRequests(transactions);
        
        // Populate pending requests table
        populateTable(
            transferView.getRequestsTableModel(),
            pendingRequests,
            this::mapRequestToRow
        );
        
        // Create and link transfer controller with reference to this controller
        transferController = new TransferController(transferView, model, userWallet, this);
        
        return transferView;
    }
    
    // Helper method to map a User to a table row
    private Object[] mapUserToRow(User user) {
        String name = user.getFirstName() + " " + user.getLastName();
        String cashtag = user.getCashtag();
        return new Object[]{name, cashtag};
    }
    
    // Helper method to map a Request to a table row
    private Object[] mapRequestToRow(Transaction request) {
        // Look up requester name
        String requesterName = lookupRequesterName(request);
        
        String date = request.getCreatedAt().toString().substring(0, 10);
        String amount = "$" + request.getAmount().toString();
        String note = request.getNote();
        String status = request.getStatus();
        
        return new Object[]{requesterName, date, amount, note, status};
    }
    
    // Helper method to look up a requester's name from a transaction
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
    
    // Helper method to populate a table with data
    private <T> void populateTable(DefaultTableModel model, List<T> items, Function<T, Object[]> rowMapper) {
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
    
    // Helper method to filter pending requests
    private List<Transaction> filterPendingRequests(List<Transaction> transactions) {
        List<Transaction> pendingRequests = new ArrayList<>();
        
        for (Transaction txn : transactions) {
            // Find requests where:
            // 1. This wallet is the sender (you need to pay)
            // 2. Type is "request"
            // 3. Status is "requested" (pending)
            if (txn.getSenderWalletId() != null && 
                txn.getSenderWalletId() == userWallet.getWalletId() &&
                "request".equals(txn.getTransactionType()) &&
                "requested".equals(txn.getStatus())) {
                
                pendingRequests.add(txn);
            }
        }
        
        return pendingRequests;
    }

    // Set up transfer button listener
    public void setupTransferButtonListener() {
        view.getBtnTransfer().addActionListener(e -> {
            System.out.println("Transfer button clicked");
            switchToPanel("transfer");
        });
    }

    // Set up profile button listener
    public void setupProfileButtonListener() {
        view.getBtnProfile().addActionListener(e -> {
            System.out.println("Profile button clicked");
            switchToPanel("profile");
        });
    }
    
    // Helper method to switch panels
    private void switchToPanel(String panelName) {
        JPanel mainContentPanel = view.getMainContentPanel();
        CardLayout cardLayout = (CardLayout) mainContentPanel.getLayout();
        
        cardLayout.show(mainContentPanel, panelName);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
    
    //Set up logout button listener
    public void setupLogoutButtonListener() {
        view.getBtnLogout().addActionListener(e -> {
            System.out.println("Logout clicked");

            // Show confirmation dialog
            if (confirmAction("Are you sure you want to logout?", "Confirm Logout")) {
                handleLogout();
            }
        });
    }
    
    // Helper method to confirm an action
    private boolean confirmAction(String message, String title) {
        int response = JOptionPane.showConfirmDialog(view, 
            message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return response == JOptionPane.YES_OPTION;
    }
    
    // Handle logout process
    private void handleLogout() {
        // Logout and send the user back to the login screen
        SwingUtilities.getWindowAncestor(view).dispose();

        // Create a new login view
        AuthView authView = new AuthView();
        
        // Create and start the controller
        AuthController controller = new AuthController(authView);

        // Display the login view
        authView.setVisible(true);
    }

    //Update dashboard label information based on user info
    private void updateUserInfo() {
        view.getNameLabel().setText(model.getFirstName());
        view.getLastNameLabel().setText(model.getLastName());
        updateBalanceDisplay();
    }
    
    // Update the balance display with proper formatting
    private void updateBalanceDisplay() {
        if (userWallet != null) {
            String balanceText = "$" + CURRENCY_FORMAT.format(userWallet.getBalance());
            view.getBalanceLabel().setText(balanceText);
            System.out.println("Updating top bar balance to: " + balanceText); // Debug log
        } else {
            view.getBalanceLabel().setText("$0.00");
            System.out.println("Warning: userWallet is null in updateBalanceDisplay");
        }
    }
    
    // Method to update wallet balance from other controllers
    public void updateBalance(BigDecimal newBalance) {
        if (userWallet != null) {
            userWallet.setBalance(newBalance);
            updateBalanceDisplay(); // This updates the top bar balance label
            
            // Update the balance in child controllers
            if (profileController != null) {
                profileController.updateWallet(userWallet);
            }
            
            if (transferController != null) {
                transferController.updateWallet(userWallet);
            }
            
            // Persist the updated balance to the database
            walletDAO.updateBalance(userWallet.getWalletId(), newBalance);
            
            // Force the topbar panel to repaint
            view.getBalanceLabel().getParent().revalidate();
            view.getBalanceLabel().getParent().repaint();
        }
    }
    
    //Get current wallet balance (for child controllers to check)
    public BigDecimal getCurrentWalletBalance() {
        return userWallet != null ? userWallet.getBalance() : BigDecimal.ZERO;
    }
    
    // Method to refresh wallet data from database
    public void refreshWallet() {
        // Reload wallet from database
        userWallet = walletDAO.getDefaultWallet(model.getUserId());
        updateBalanceDisplay();
        
        // Update child controllers
        if (profileController != null) {
            profileController.updateWallet(userWallet);
        }
        
        if (transferController != null) {
            transferController.updateWallet(userWallet);
        }
    }
    
    //Method to refresh all content
    public void refreshContent() {
        // Store current view state
        String currentPanelName = getCurrentPanelName();
        
        // Reload wallet information
        userWallet = walletDAO.getDefaultWallet(model.getUserId());
        
        // Update UI with latest information
        updateUserInfo();
        
        // Refresh sub-controllers if they exist
        if (profileController != null) {
            profileController.refreshData();
        }
        
        if (transferController != null) {
            transferController.refreshData();
        }
        
        // Restore the current view
        if (currentPanelName != null) {
            switchToPanel(currentPanelName);
        }
    }
    
    // Helper method to get the current panel name
    private String getCurrentPanelName() {
        JPanel mainContentPanel = view.getMainContentPanel();
        
        // Find the currently displayed component
        for (Component comp : mainContentPanel.getComponents()) {
            if (comp.isVisible()) {
                return comp.getName();
            }
        }
        
        return null;
    }
    
    // Getter for the view - needed for RefreshUtility
    public CommonView getView() {
        return view;
    }
}