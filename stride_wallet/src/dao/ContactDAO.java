package dao;

import model.Contact;
import model.User;
import my_util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class ContactDAO {
    private final Connection connect;
    private final UserDAO userDAO;

    public ContactDAO() {
        //try connection to database
        try {
            this.connect = DatabaseUtil.getConnection();
            this.userDAO = new UserDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error connecting to database: " + e.getMessage(), e);
        }
    }

    //get all contacts for a user
    public List<Contact> getUserContacts(int userId) {
        String sql = "SELECT user_id, contact_cashtag, created_at FROM Contacts WHERE user_id = ?";
        List<Contact> contacts = new ArrayList<>();
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contacts.add(mapRowToContact(rs));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error getting user contacts: " + e.getMessage(), e);
        }
        return contacts;
    }
    
    //get user contacts with their user details
    public List<User> getUserContactDetails(int userId) {
        List<Contact> contacts = getUserContacts(userId);
        List<User> contactUsers = new ArrayList<>();
        
        for (Contact contact : contacts) {
            User user = userDAO.getUserByCashtag(contact.getContactCashtag());
            if (user != null) {
                contactUsers.add(user);
            }
        }
        
        return contactUsers;
    }

    //add a contact
    public boolean addContact(int userId, String contactCashtag) {
        // Verify the cashtag exists
        User contactUser = userDAO.getUserByCashtag(contactCashtag);
        if (contactUser == null) {
            JOptionPane.showMessageDialog(null, "User with cashtag " + contactCashtag + " not found",
                    "Invalid Cashtag", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Don't allow adding yourself as a contact
        if (contactUser.getUserId() == userId) {
            JOptionPane.showMessageDialog(null, "You cannot add yourself as a contact",
                    "Invalid Contact", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        String sql = "INSERT INTO Contacts (user_id, contact_cashtag) VALUES (?, ?)";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, contactCashtag);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Check for duplicate key error
            if (e.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(null, "This contact is already in your list",
                        "Duplicate Contact", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            }
            return false;
        }
    }

    //remove a contact
    public boolean removeContact(int userId, String contactCashtag) {
        String sql = "DELETE FROM Contacts WHERE user_id = ? AND contact_cashtag = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, contactCashtag);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error removing contact: " + e.getMessage(), e);
        }
    }

    //check if a contact exists
    public boolean contactExists(int userId, String contactCashtag) {
        String sql = "SELECT 1 FROM Contacts WHERE user_id = ? AND contact_cashtag = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, contactCashtag);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Returns true if found, false if not
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            throw new RuntimeException("Error checking contact: " + e.getMessage(), e);
        }
    }

    //map resultset row to contact object
    private Contact mapRowToContact(ResultSet rs) throws SQLException {
        Contact contact = new Contact();
        contact.setUserId(rs.getInt("user_id"));
        contact.setContactCashtag(rs.getString("contact_cashtag"));
        contact.setCreatedAt(rs.getTimestamp("created_at"));
        return contact;
    }
}