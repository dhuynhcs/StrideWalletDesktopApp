package controller;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import dao.UserDAO;
import dao.WalletDAO;
import dao.TransactionDAO;
import dao.ContactDAO;
import model.User;
import model.Wallet;
import model.Transaction;
import view.AuthView;
import view.CommonView;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.List;

// Controller for AuthView: handles login and clear functionality
public class AuthController {
    // Constants
    private static final String DEFAULT_CURRENCY = "USD";
    private static final String DEFAULT_WALLET_TYPE = "primary";
    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal("0.00");
    
    // Core components
    private final AuthView view;
    
    // DAOs
    private final UserDAO userDAO;
    private final WalletDAO walletDAO;
    private final TransactionDAO transactionDAO;
    private final ContactDAO contactDAO;

    //Iniitalize controller
    public AuthController(AuthView view) {
        this.view = view;
        this.userDAO = new UserDAO();
        this.walletDAO = new WalletDAO();
        this.transactionDAO = new TransactionDAO();
        this.contactDAO = new ContactDAO();

        initializeEventListeners();
    }

    //Setup listeners for each button
    private void initializeEventListeners() {
        setupLoginButtonListener();
        setupClearButtonListener();
        setupWindowClosingListener();
    }

    // Setup window closing listener
    private void setupWindowClosingListener() {
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    // Clear cashtag and password field
    private void setupClearButtonListener() {
        view.getBtnClear().addActionListener(event -> {
            view.getTxtCashtagField().setText("");
            view.getTxtPasswordField().setText("");
        });
    }

    //Authenticate using user credentials and move to dashboard
    private void setupLoginButtonListener() {
        view.getBtnLogin().addActionListener(event -> handleLogin());
    }
    
    // Handle login process
    private void handleLogin() {
        // Get cashtag and password from input fields
        String cashtag = view.getTxtCashtagField().getText().trim();
        String password = new String(view.getTxtPasswordField().getPassword());

        // Verify fields are not empty
        if (cashtag.isEmpty() || password.isEmpty()) {
            showMessage("Please enter both cashtag and password", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Authenticate user using UserDAO
        User authenticatedUser = userDAO.authenticateUser(cashtag, password);
        
        // Upon successful login, move to main application
        if (authenticatedUser != null) {
            showMessage("Welcome back, " + authenticatedUser.getFirstName() + "!", 
                      "Login Successful", JOptionPane.INFORMATION_MESSAGE);

            // Get or create user's wallet
            Wallet userWallet = getOrCreateUserWallet(authenticatedUser);
            
            // Navigate to the main application dashboard
            navigateToMainApplication(authenticatedUser, userWallet);
        } else {
            showMessage("Invalid cashtag or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
	// Get existing wallet or create a new one if none exists
    private Wallet getOrCreateUserWallet(User user) {
        Wallet userWallet = walletDAO.getDefaultWallet(user.getUserId());
        
        // If user doesn't have a default wallet, create one
        if (userWallet == null) {
            userWallet = createDefaultWallet(user.getUserId());
            walletDAO.createWallet(userWallet);
            
            // Get the newly created wallet with its ID
            userWallet = walletDAO.getDefaultWallet(user.getUserId());
        }
        
        return userWallet;
    }
    
    //Create a default wallet for a user
    private Wallet createDefaultWallet(int userId) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(DEFAULT_BALANCE);
        wallet.setCurrency(DEFAULT_CURRENCY);
        wallet.setWalletType(DEFAULT_WALLET_TYPE);
        wallet.setDefault(true);
        return wallet;
    }
    
    // Helper method to show message dialogs
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(view, message, title, messageType);
    }

    // Move to the main application after successful authentication
    private void navigateToMainApplication(User user, Wallet wallet) {
        view.dispose();

        // Create main application frame
        JFrame mainFrame = new JFrame("StrideWallet - Dashboard");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create the common view
        CommonView commonView = new CommonView(mainFrame);
        
        // Create and initialize the common controller
        CommonController commonController = new CommonController(commonView, user);
        
        // Set the content pane and display
        mainFrame.setContentPane(commonView);
        mainFrame.setSize(800, 700);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
    
    //Main method to start the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AuthView authView = new AuthView();
            new AuthController(authView);
        });
    }
}