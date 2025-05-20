package dao;

import model.User;
import my_util.DatabaseUtil;
import my_util.Security;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class UserDAO {
    private final Connection connect;

    public UserDAO() {
        //try connection to database
        try {
            this.connect = DatabaseUtil.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error connecting to database: " + e.getMessage(), e);
        }
    }

    //get all users in the database
    public List<User> getAllUsers() {
        String sql = "SELECT user_id, phone_number, email, first_name, last_name, cashtag, password_hash, created_at, updated_at FROM Users";
        List<User> users = new ArrayList<>();
        try (Statement stmt = connect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User u = mapRowToUser(rs);
                users.add(u);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error getting all users: " + e.getMessage(), e);
        }
        return users;
    }
    
    //get a specific user by userID
    public User getUserById(int userId) {
        String sql = "SELECT user_id, phone_number, email, first_name, last_name, cashtag, password_hash, created_at, updated_at FROM Users WHERE user_id = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                } else {
                    return null;  //no user found
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error getting user by ID: " + e.getMessage(), e);
        }
    }

    //get user by cashtag
    public User getUserByCashtag(String cashtag) {
        String sql = "SELECT user_id, phone_number, email, first_name, last_name, cashtag, password_hash, created_at, updated_at FROM Users WHERE cashtag = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setString(1, cashtag);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                } else {
                    return null;  //no user found
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error getting user by cashtag: " + e.getMessage(), e);
        }
    }

    //get user by email
    public User getUserByEmail(String email) {
        String sql = "SELECT user_id, phone_number, email, first_name, last_name, cashtag, password_hash, created_at, updated_at FROM Users WHERE email = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                } else {
                    return null;  //no user found
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error getting user by email: " + e.getMessage(), e);
        }
    }

    //create a new user
    public boolean createUser(User user, String plainPassword) {
        String sql = "INSERT INTO Users (phone_number, email, first_name, last_name, cashtag, password_hash) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getPhoneNumber());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getFirstName());
            ps.setString(4, user.getLastName());
            ps.setString(5, user.getCashtag());
            ps.setString(6, Security.hashPassword(plainPassword)); //we hash our password using the util
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error creating user: " + e.getMessage(), e);
        }
    }

    //update user info
    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET phone_number = ?, email = ?, first_name = ?, last_name = ?, cashtag = ? WHERE user_id = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setString(1, user.getPhoneNumber());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getFirstName());
            ps.setString(4, user.getLastName());
            ps.setString(5, user.getCashtag());
            ps.setInt(6, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }
    }

    //delete user
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
        }
    }

    //authenticate user login
    public User authenticateUser(String cashtag, String plainPassword) {
        String query = "SELECT * FROM Users WHERE cashtag = ?";
        ResultSet rs = null;
        try (PreparedStatement stm = connect.prepareStatement(query)) {
            stm.setString(1, cashtag);
            rs = stm.executeQuery();
            if (rs.next()) {
                String storedHashedPassword = rs.getString("password_hash");
                //compare the provided password with stored hash
                String hashedPassword = Security.hashPassword(plainPassword);
                System.out.println(storedHashedPassword);
                System.out.println(hashedPassword);
                if (hashedPassword.equals(storedHashedPassword)) {
                    //if passwords match, return User object with data from database
                    return mapRowToUser(rs);
                }
            }
            //authentication failed
            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Database error during authentication: " + e.getMessage(), e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
                    System.err.println("Error closing ResultSet: " + e.getMessage());
                }
            }
        }
    }
    
    //change the password functionality (to be implemented)
//    public boolean changePassword(int userId, String currentPass, String newPass) {
//        //get the stored password
//        String sql = "SELECT password_hash FROM Users WHERE user_id = ?";
//        try (PreparedStatement ps = connect.prepareStatement(sql)) {
//            ps.setInt(1, userId);
//            try (ResultSet rs = ps.executeQuery()) {
//                //no such user check
//                if (!rs.next()) {
//                    return false;
//                }
//                String storedHash = rs.getString("password_hash");
//                //verify our old password
//                String currentHash = Security.hashPassword(currentPass);
//                
//                if (!currentHash.equals(storedHash)) {
//                    return false; //old password didnt match
//                }
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
//            throw new RuntimeException("Error verifying current password: " + e.getMessage(), e);
//        }
//
//        //update the old password to the new password
//        String updateSql = "UPDATE Users SET password_hash = ? WHERE user_id = ?";
//        try (PreparedStatement ps = connect.prepareStatement(updateSql)) {
//            ps.setString(1, Security.hashPassword(newPass));
//            ps.setInt(2, userId);
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
//            throw new RuntimeException("Error updating password: " + e.getMessage(), e);
//        }
//    }

    //map our resultset object to the user object
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User userMapped = new User();
        userMapped.setUserId(rs.getInt("user_id"));
        userMapped.setPhoneNumber(rs.getString("phone_number"));
        userMapped.setEmail(rs.getString("email"));
        userMapped.setFirstName(rs.getString("first_name"));
        userMapped.setLastName(rs.getString("last_name"));
        userMapped.setCashtag(rs.getString("cashtag"));
        userMapped.setPasswordHash(rs.getString("password_hash"));
        userMapped.setCreatedAt(rs.getTimestamp("created_at"));
        userMapped.setUpdatedAt(rs.getTimestamp("updated_at"));
        return userMapped;
    }
}