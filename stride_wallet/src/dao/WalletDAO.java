package dao;

import model.Wallet;
import my_util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class WalletDAO {
    private final Connection connect;

    public WalletDAO() {
        //try connection to database
        try {
            this.connect = DatabaseUtil.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error connecting to database: " + e.getMessage(), e);
        }
    }

    //get all wallets
    public List<Wallet> getAllWallets() {
        String sql = "SELECT wallet_id, user_id, balance, currency, wallet_type, is_default, created_at, updated_at FROM Wallets";
        List<Wallet> wallets = new ArrayList<>();
        try (Statement stmt = connect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Wallet wallet = mapRowToWallet(rs);
                wallets.add(wallet);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error getting all wallets: " + e.getMessage(), e);
        }
        return wallets;
    }
    
    //get wallet by id
    public Wallet getWalletById(int walletId) {
        String sql = "SELECT wallet_id, user_id, balance, currency, wallet_type, is_default, created_at, updated_at FROM Wallets WHERE wallet_id = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, walletId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToWallet(rs);
                } else {
                    return null;  //no wallet found
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error getting wallet by ID: " + e.getMessage(), e);
        }
    }

    //get user's wallets
    public List<Wallet> getWalletsByUserId(int userId) {
        String sql = "SELECT wallet_id, user_id, balance, currency, wallet_type, is_default, created_at, updated_at FROM Wallets WHERE user_id = ?";
        List<Wallet> wallets = new ArrayList<>();
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    wallets.add(mapRowToWallet(rs));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error getting wallets by user ID: " + e.getMessage(), e);
        }
        return wallets;
    }

    //get default wallet for user
    public Wallet getDefaultWallet(int userId) {
        String sql = "SELECT wallet_id, user_id, balance, currency, wallet_type, is_default, created_at, updated_at FROM Wallets WHERE user_id = ? AND is_default = TRUE";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToWallet(rs);
                } else {
                    return null;  //no default wallet found
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error getting default wallet: " + e.getMessage(), e);
        }
    }

    //create a new wallet
    public boolean createWallet(Wallet wallet) {
        String sql = "INSERT INTO Wallets (user_id, balance, currency, wallet_type, is_default) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, wallet.getUserId());
            ps.setBigDecimal(2, wallet.getBalance());
            ps.setString(3, wallet.getCurrency());
            ps.setString(4, wallet.getWalletType());
            ps.setBoolean(5, wallet.isDefault());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        wallet.setWalletId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error creating wallet: " + e.getMessage(), e);
        }
    }

    //update wallet info
    public boolean updateWallet(Wallet wallet) {
        String sql = "UPDATE Wallets SET balance = ?, currency = ?, wallet_type = ?, is_default = ? WHERE wallet_id = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setBigDecimal(1, wallet.getBalance());
            ps.setString(2, wallet.getCurrency());
            ps.setString(3, wallet.getWalletType());
            ps.setBoolean(4, wallet.isDefault());
            ps.setInt(5, wallet.getWalletId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error updating wallet: " + e.getMessage(), e);
        }
    }

    //delete wallet
    public boolean deleteWallet(int walletId) {
        String sql = "DELETE FROM Wallets WHERE wallet_id = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, walletId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error deleting wallet: " + e.getMessage(), e);
        }
    }

    //update wallet balance within a transaction context
    public boolean updateBalance(Connection transactionConnection, int walletId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE Wallets SET balance = ? WHERE wallet_id = ?";
        try (PreparedStatement ps = transactionConnection.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, walletId);
            return ps.executeUpdate() > 0;
        }
    }
    
    //update wallet balance (standalone)
    public boolean updateBalance(int walletId, BigDecimal newBalance) {
        String sql = "UPDATE Wallets SET balance = ? WHERE wallet_id = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, walletId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error updating wallet balance: " + e.getMessage(), e);
        }
    }

    //map resultset row to wallet object
    private Wallet mapRowToWallet(ResultSet rs) throws SQLException {
        Wallet wallet = new Wallet();
        wallet.setWalletId(rs.getInt("wallet_id"));
        wallet.setUserId(rs.getInt("user_id"));
        wallet.setBalance(rs.getBigDecimal("balance"));
        wallet.setCurrency(rs.getString("currency"));
        wallet.setWalletType(rs.getString("wallet_type"));
        wallet.setDefault(rs.getBoolean("is_default"));
        wallet.setUpdatedAt(rs.getTimestamp("updated_at"));
        return wallet;
    }
}