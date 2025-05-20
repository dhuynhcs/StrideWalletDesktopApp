package dao;

import model.Transaction;
import model.Wallet;
import my_util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class TransactionDAO {
    private final Connection connect;
    private final WalletDAO walletDAO;

    public TransactionDAO() {
        try {
            this.connect = DatabaseUtil.getConnection();
            this.walletDAO = new WalletDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error connecting to database: " + e.getMessage(), e);
        }
    }

    //get all transactions
    public List<Transaction> getAllTransactions() {
        String sql = "SELECT transaction_id, sender_wallet_id, receiver_wallet_id, amount, note, transaction_type, status, created_at, updated_at FROM Transactions";
        List<Transaction> transactions = new ArrayList<>();
        try (Statement stmt = connect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error getting all transactions: " + e.getMessage(), e);
        }
        return transactions;
    }
    
    //get transaction by id
    public Transaction getTransactionById(int transactionId) {
        String sql = "SELECT transaction_id, sender_wallet_id, receiver_wallet_id, amount, note, transaction_type, status, created_at, updated_at FROM Transactions WHERE transaction_id = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTransaction(rs);
                } else {
                    return null;  //no transaction found
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error getting transaction by ID: " + e.getMessage(), e);
        }
    }

    //get transactions for a wallet (both sent and received)
    public List<Transaction> getTransactionsByWalletId(int walletId) {
        String sql = "SELECT transaction_id, sender_wallet_id, receiver_wallet_id, amount, note, transaction_type, status, created_at, updated_at FROM Transactions WHERE sender_wallet_id = ? OR receiver_wallet_id = ?";
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, walletId);
            ps.setInt(2, walletId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRowToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error getting transactions by wallet ID: " + e.getMessage(), e);
        }
        return transactions;
    }

    //get transactions by user id (involving any of the user's wallets)
    public List<Transaction> getTransactionsByUserId(int userId) {
        List<Wallet> userWallets = walletDAO.getWalletsByUserId(userId);
        if (userWallets.isEmpty()) {
            return new ArrayList<>();
        }
        
        StringBuilder walletIdsBuilder = new StringBuilder();
        for (int i = 0; i < userWallets.size(); i++) {
            if (i > 0) {
                walletIdsBuilder.append(",");
            }
            walletIdsBuilder.append(userWallets.get(i).getWalletId());
        }
        String walletIds = walletIdsBuilder.toString();
        
        String sql = "SELECT transaction_id, sender_wallet_id, receiver_wallet_id, amount, note, transaction_type, status, created_at, updated_at FROM Transactions " 
                   + "WHERE sender_wallet_id IN (" + walletIds + ") OR receiver_wallet_id IN (" + walletIds + ") "
                   + "ORDER BY created_at DESC";
        
        List<Transaction> transactions = new ArrayList<>();
        try (Statement stmt = connect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error getting transactions by user ID: " + e.getMessage(), e);
        }
        return transactions;
    }

    //create a money transfer transaction with transaction management
    public boolean createMoneyTransfer(Integer senderWalletId, int receiverWalletId, BigDecimal amount, String note) {
        Connection transactionConnection = null;
        try {
            // Get a connection for the transaction
            transactionConnection = DatabaseUtil.getConnection();
            
            // Start transaction
            transactionConnection.setAutoCommit(false);
            
            // If sender wallet is provided (not a deposit), update sender's balance
            if (senderWalletId != null) {
                Wallet senderWallet = walletDAO.getWalletById(senderWalletId);
                if (senderWallet == null) {
                    throw new SQLException("Sender wallet not found");
                }
                
                // Check if sender has enough balance
                if (senderWallet.getBalance().compareTo(amount) < 0) {
                    throw new SQLException("Insufficient funds");
                }
                
                // Update sender balance
                BigDecimal newSenderBalance = senderWallet.getBalance().subtract(amount);
                if (!walletDAO.updateBalance(transactionConnection, senderWalletId, newSenderBalance)) {
                    throw new SQLException("Failed to update sender balance");
                }
            }
            
            // Update receiver balance
            Wallet receiverWallet = walletDAO.getWalletById(receiverWalletId);
            if (receiverWallet == null) {
                throw new SQLException("Receiver wallet not found");
            }
            
            BigDecimal newReceiverBalance = receiverWallet.getBalance().add(amount);
            if (!walletDAO.updateBalance(transactionConnection, receiverWalletId, newReceiverBalance)) {
                throw new SQLException("Failed to update receiver balance");
            }
            
            // Create transaction record
            String transactionType = (senderWalletId == null) ? "deposit" : "send";
            String status = "completed";
            
            String sql = "INSERT INTO Transactions (sender_wallet_id, receiver_wallet_id, amount, note, transaction_type, status) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = transactionConnection.prepareStatement(sql)) {
                if (senderWalletId == null) {
                    ps.setNull(1, Types.INTEGER);
                } else {
                    ps.setInt(1, senderWalletId);
                }
                ps.setInt(2, receiverWalletId);
                ps.setBigDecimal(3, amount);
                ps.setString(4, note);
                ps.setString(5, transactionType);
                ps.setString(6, status);
                
                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating transaction failed, no rows affected.");
                }
            }
            
            // Commit the transaction
            transactionConnection.commit();
            return true;
            
        } catch (SQLException e) {
            // Roll back transaction in case of error
            if (transactionConnection != null) {
                try {
                    transactionConnection.rollback();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "SQL Error during rollback: " + ex.getMessage());
                }
            }
            JOptionPane.showMessageDialog(null, "Transaction failed: " + e.getMessage());
            return false;
        } finally {
            // Restore auto-commit and close connection
            if (transactionConnection != null) {
                try {
                    transactionConnection.setAutoCommit(true);
                    transactionConnection.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "SQL Error closing connection: " + ex.getMessage());
                }
            }
        }
    }

    //create a payment request
    public boolean createPaymentRequest(int requestorWalletId, int requesteeWalletId, BigDecimal amount, String note) {
        String sql = "INSERT INTO Transactions (sender_wallet_id, receiver_wallet_id, amount, note, transaction_type, status) VALUES (?, ?, ?, ?, 'request', 'requested')";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, requesteeWalletId); // The person who will pay
            ps.setInt(2, requestorWalletId); // The person requesting the money
            ps.setBigDecimal(3, amount);
            ps.setString(4, note);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error creating payment request: " + e.getMessage(), e);
        }
    }

    //respond to a payment request
    public boolean respondToRequest(int transactionId, boolean accept) {
        Connection transactionConnection = null;
        try {
            // Get transaction details
            Transaction request = getTransactionById(transactionId);
            if (request == null) {
                throw new SQLException("Request not found");
            }
            
            // Check if it's a request and in requested status
            if (!"request".equals(request.getTransactionType()) || !"requested".equals(request.getStatus())) {
                throw new SQLException("Invalid transaction type or status");
            }
            
            // Get a connection for the transaction
            transactionConnection = DatabaseUtil.getConnection();
            transactionConnection.setAutoCommit(false);
            
            if (accept) {
                // Process the payment
                // Check if sender has enough balance
                Wallet senderWallet = walletDAO.getWalletById(request.getSenderWalletId());
                if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
                    throw new SQLException("Insufficient funds");
                }
                
                // Update sender balance
                BigDecimal newSenderBalance = senderWallet.getBalance().subtract(request.getAmount());
                if (!walletDAO.updateBalance(transactionConnection, request.getSenderWalletId(), newSenderBalance)) {
                    throw new SQLException("Failed to update sender balance");
                }
                
                // Update receiver balance
                Wallet receiverWallet = walletDAO.getWalletById(request.getReceiverWalletId());
                BigDecimal newReceiverBalance = receiverWallet.getBalance().add(request.getAmount());
                if (!walletDAO.updateBalance(transactionConnection, request.getReceiverWalletId(), newReceiverBalance)) {
                    throw new SQLException("Failed to update receiver balance");
                }
                
                // Update request status
                String sql = "UPDATE Transactions SET status = 'completed' WHERE transaction_id = ?";
                try (PreparedStatement ps = transactionConnection.prepareStatement(sql)) {
                    ps.setInt(1, transactionId);
                    ps.executeUpdate();
                }
            } else {
                // Decline the request
                String sql = "UPDATE Transactions SET status = 'declined' WHERE transaction_id = ?";
                try (PreparedStatement ps = transactionConnection.prepareStatement(sql)) {
                    ps.setInt(1, transactionId);
                    ps.executeUpdate();
                }
            }
            
            // Commit the transaction
            transactionConnection.commit();
            return true;
            
        } catch (SQLException e) {
            // Roll back transaction in case of error
            if (transactionConnection != null) {
                try {
                    transactionConnection.rollback();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "SQL Error during rollback: " + ex.getMessage());
                }
            }
            JOptionPane.showMessageDialog(null, "Request response failed: " + e.getMessage());
            return false;
        } finally {
            // Restore auto-commit and close connection
            if (transactionConnection != null) {
                try {
                    transactionConnection.setAutoCommit(true);
                    transactionConnection.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "SQL Error closing connection: " + ex.getMessage());
                }
            }
        }
    }

    //map resultset row to transaction object
    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        
        int senderWalletId = rs.getInt("sender_wallet_id");
        if (rs.wasNull()) {
            transaction.setSenderWalletId(null);
        } else {
            transaction.setSenderWalletId(senderWalletId);
        }
        
        transaction.setReceiverWalletId(rs.getInt("receiver_wallet_id"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setNote(rs.getString("note"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setStatus(rs.getString("status"));
        transaction.setCreatedAt(rs.getTimestamp("created_at"));
        transaction.setUpdatedAt(rs.getTimestamp("updated_at"));
        return transaction;
    }
}