package my_util;

import javax.swing.JOptionPane;

public class Verification {

    //validate login input fields
    public static boolean validateLoginFields(String cashtag, String password) {
        // Check for empty fields
        if (cashtag.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Error! There is an empty field", 
                    "Empty Field", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    
    public static boolean validateTransferFields(String amount, String recipientCashtag) {
        // Check for empty fields
        if (amount.isEmpty() || recipientCashtag.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Error! Amount and recipient cannot be empty",
                    "Empty Field", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate amount is a positive number
        try {
            double amountValue = Double.parseDouble(amount);
            if (amountValue <= 0) {
                JOptionPane.showMessageDialog(null, "Error! Amount must be greater than zero",
                        "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Check for maximum of 2 decimal places
            String[] parts = amount.split("\\.");
            if (parts.length > 1 && parts[1].length() > 2) {
                JOptionPane.showMessageDialog(null, "Error! Amount can have at most 2 decimal places",
                        "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error! Amount must be a valid number",
                    "Invalid Amount", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
//    //validate email format
//    public static boolean isValidEmail(String email) {
//        return email != null && email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
//    }
//    
//    //validate phone number format
//    public static boolean isValidPhoneNumber(String phoneNumber) {
//        return phoneNumber != null && phoneNumber.matches("\\d{10}|\\d{3}[-\\s]\\d{3}[-\\s]\\d{4}|\\(\\d{3}\\)[-\\s]?\\d{3}[-\\s]?\\d{4}");
//    }
}