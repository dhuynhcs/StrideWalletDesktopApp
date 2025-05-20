package my_util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import controller.CommonController;
import dao.TransactionDAO;
import dao.WalletDAO;
import dao.UserDAO;
import dao.ContactDAO;
import model.User;
import model.Wallet;
import model.Transaction;
import view.ProfileView;
import view.TransferView;


//Utility class for refreshing application panels
//This class provides centralized methods to refresh different views

public class RefreshUtility {
    
    // DAO objects for database access
    private static final TransactionDAO transactionDAO = new TransactionDAO();
    private static final WalletDAO walletDAO = new WalletDAO();
    private static final UserDAO userDAO = new UserDAO();
    private static final ContactDAO contactDAO = new ContactDAO();
    
    // Formatting constants
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("0.00");
    
  
    public static void refreshAllViews(CommonController commonController, User currentUser) {
        if (commonController == null || currentUser == null) {
            logWarning("Cannot refresh views: controller or user is null");
            return;
        }
        
        // Get current wallet data
        Wallet userWallet = loadUserWallet(currentUser.getUserId());
        if (userWallet == null) return;
        
        // Update common controller balance (this updates the top bar too)
        commonController.updateBalance(userWallet.getBalance());
        
        // Get the main content panel and its layout
        JPanel mainContentPanel = commonController.getView().getMainContentPanel();
        CardLayout cardLayout = (CardLayout) mainContentPanel.getLayout();
        
        // Preserve current view state
        String currentPanelName = getCurrentPanelName(mainContentPanel);
        
        // Refresh individual views
        refreshViewIfExists(mainContentPanel, "profilePanel", ProfileView.class, 
                            view -> refreshProfileView(view, currentUser, userWallet));
        
        refreshViewIfExists(mainContentPanel, "transferPanel", TransferView.class, 
                            view -> refreshTransferView(view, currentUser, userWallet));
        
        // Restore the original view
        if (currentPanelName != null) {
            cardLayout.show(mainContentPanel, currentPanelName);
        }
        
        // Force UI updates
        commonController.getView().getNameLabel().getParent().revalidate();
        commonController.getView().getNameLabel().getParent().repaint();
        mainContentPanel.repaint();
        mainContentPanel.revalidate();
    }
    
    public static void refreshProfileView(ProfileView profileView, User currentUser, Wallet userWallet) {
        if (profileView == null || currentUser == null) return;
        
        // If wallet was not provided, try to load it
        Wallet wallet = ensureWalletLoaded(userWallet, currentUser.getUserId());
        if (wallet == null) return;
        
        // Get latest transactions
        List<Transaction> transactions = transactionDAO.getTransactionsByUserId(currentUser.getUserId());
        
        // Update the view
        profileView.updateData(wallet, transactions);
    }
    
    public static void refreshTransferView(TransferView transferView, User currentUser, Wallet userWallet) {
        if (transferView == null || currentUser == null) return;
        
        // If wallet was not provided, try to load it
        Wallet wallet = ensureWalletLoaded(userWallet, currentUser.getUserId());
        if (wallet == null) return;
        
        // Update the different sections of the transfer view
        updateContactsList(transferView, currentUser);
        updateUserDirectory(transferView, currentUser);
        updatePendingRequests(transferView, currentUser, wallet);
        updateTransferViewBalance(transferView, wallet);
    }
    
    //Update the contacts list in the transfer view
    private static void updateContactsList(TransferView view, User currentUser) {
        List<User> contacts = contactDAO.getUserContactDetails(currentUser.getUserId());
        DefaultTableModel contactsModel = view.getContactsTableModel();
        
        updateTableModel(contactsModel, contacts, user -> {
            String name = user.getFirstName() + " " + user.getLastName();
            String cashtag = user.getCashtag();
            return new Object[]{name, cashtag};
        });
    }
    
    //Update the user directory in the transfer view
    private static void updateUserDirectory(TransferView view, User currentUser) {
        List<User> allUsers = userDAO.getAllUsers();
        DefaultTableModel directoryModel = view.getDirectoryTableModel();
        
        updateTableModel(directoryModel, allUsers, user -> {
            // Skip current user in directory
            if (user.getUserId() == currentUser.getUserId()) {
                return null;
            }
            
            String name = user.getFirstName() + " " + user.getLastName();
            String cashtag = user.getCashtag();
            return new Object[]{name, cashtag};
        });
    }
    
    //Update the pending requests in the transfer view
    private static void updatePendingRequests(TransferView view, User currentUser, Wallet userWallet) {
        List<Transaction> transactions = transactionDAO.getTransactionsByUserId(currentUser.getUserId());
        List<Transaction> pendingRequests = filterPendingRequests(transactions, userWallet);
        DefaultTableModel requestsModel = view.getRequestsTableModel();
        
        updateTableModel(requestsModel, pendingRequests, request -> {
            // Look up requester name
            String requesterName = lookupRequesterName(request);
            
            String date = request.getCreatedAt().toString().substring(0, 10);
            String amount = "$" + request.getAmount().toString();
            String note = request.getNote();
            String status = request.getStatus();
            
            return new Object[]{requesterName, date, amount, note, status};
        });
    }
    
    //Helper method to look up a requester's name from a transaction
    private static String lookupRequesterName(Transaction request) {
        if (request.getReceiverWalletId() <= 0) {
            return "Unknown";
        }
        
        // Look up wallet
        Wallet receiverWallet = walletDAO.getWalletById(request.getReceiverWalletId());
        if (receiverWallet == null) {
            return "Unknown";
        }
        
        // Look up user
        User requester = userDAO.getUserById(receiverWallet.getUserId());
        if (requester == null) {
            return "Unknown";
        }
        
        return requester.getFirstName() + " " + requester.getLastName();
    }
   
    private static <T> void updateTableModel(DefaultTableModel model, List<T> data, Function<T, Object[]> rowMapper) {
        model.setRowCount(0);
        
        if (data == null || data.isEmpty()) {
            return;
        }
        
        for (T item : data) {
            Object[] rowData = rowMapper.apply(item);
            if (rowData != null) {
                model.addRow(rowData);
            }
        }
    }
    
    //Helper method to filter pending requests
    private static List<Transaction> filterPendingRequests(List<Transaction> transactions, Wallet userWallet) {
        List<Transaction> pendingRequests = new ArrayList<>();
        
        for (Transaction txn : transactions) {
            // Find requests where:
            //This wallet is the sender (you need to pay)
            //Type is "request"
            //Status is "requested" (pending)
            if (txn.getSenderWalletId() != null && 
                txn.getSenderWalletId() == userWallet.getWalletId() &&
                "request".equals(txn.getTransactionType()) &&
                "requested".equals(txn.getStatus())) {
                
                pendingRequests.add(txn);
            }
        }
        
        return pendingRequests;
    }
    
    //Helper method to update balance display in transfer view
    private static void updateTransferViewBalance(TransferView view, Wallet wallet) {
        String balanceText = "Current Balance: $" + CURRENCY_FORMAT.format(wallet.getBalance());
        
        // Find the balance label in the transfer panel and update it
        for (Component comp : view.getComponents()) {
            if (comp instanceof JLabel && ((JLabel)comp).getText().startsWith("Current Balance:")) {
                ((JLabel)comp).setText(balanceText);
                break;
            }
        }
    }
    
    //Helper method to find a component by name in a container
    private static Component findComponentByName(Container container, String name) {
        for (Component comp : container.getComponents()) {
            if (name.equals(comp.getName())) {
                return comp;
            }
        }
        return null;
    }
    
    //Helper method to determine the current panel name
    private static String getCurrentPanelName(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp.isVisible()) {
                return comp.getName();
            }
        }
        return null;
    }
    
    //Helper method to refresh a specific view if it exists
    private static <T extends JComponent> void refreshViewIfExists(
            Container container, String panelName, Class<T> viewClass,
            Consumer<T> refreshAction) {
        
        Component comp = findComponentByName(container, panelName);
        if (viewClass.isInstance(comp)) {
            refreshAction.accept(viewClass.cast(comp));
        }
    }
    
    //Helper method to load a user's wallet
    private static Wallet loadUserWallet(int userId) {
        Wallet wallet = walletDAO.getDefaultWallet(userId);
        if (wallet == null) {
            logWarning("User wallet is null for user ID: " + userId);
        }
        return wallet;
    }
    
    //Helper method to ensure a wallet is loaded
    private static Wallet ensureWalletLoaded(Wallet existingWallet, int userId) {
        if (existingWallet != null) {
            return existingWallet;
        }
        return loadUserWallet(userId);
    }
    
    //Helper method for logging warnings
    private static void logWarning(String message) {
        System.out.println("WARNING: " + message);
    }
}