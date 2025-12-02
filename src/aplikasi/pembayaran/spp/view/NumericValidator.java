package aplikasi.pembayaran.spp.view;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * NumericValidator - Utility class for validating numeric input
 * Provides methods to ensure only numeric values can be entered in text fields
 */
public class NumericValidator {
    
    /**
     * Makes a JTextField accept only numeric input
     * @param textField The JTextField to validate
     */
    public static void makeNumericOnly(JTextField textField) {
        // Set document filter to allow only numeric characters
        textField.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (isNumeric(str)) {
                    super.insertString(offs, str, a);
                }
            }
        });
        
        // Add key listener to prevent paste of non-numeric content
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // Ignore the key press
                }
            }
        });
    }
    
    /**
     * Makes a JTextField accept only numeric input with decimal support
     * @param textField The JTextField to validate
     */
    public static void makeNumericWithDecimalOnly(JTextField textField) {
        // Set document filter to allow only numeric and decimal characters
        textField.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (isNumericWithDecimal(str, textField.getText(), offs)) {
                    super.insertString(offs, str, a);
                }
            }
        });
        
        // Add key listener to prevent paste of non-numeric content
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = textField.getText();
                
                // Check if it's a digit, backspace, delete, or decimal point
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && 
                    c != KeyEvent.VK_DELETE && c != '.') {
                    e.consume(); // Ignore the key press
                    return;
                }
                
                // Prevent multiple decimal points
                if (c == '.' && currentText.contains(".")) {
                    e.consume(); // Ignore the key press
                }
            }
        });
    }
    
    /**
     * Checks if a string contains only numeric characters
     * @param str The string to check
     * @return true if the string contains only digits, false otherwise
     */
    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return true; // Allow empty strings
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks if a string contains only numeric and decimal characters
     * @param str The string to check
     * @param currentText The current text in the field
     * @param insertPos The position where the string will be inserted
     * @return true if the string contains valid numeric characters including decimal, false otherwise
     */
    private static boolean isNumericWithDecimal(String str, String currentText, int insertPos) {
        if (str == null || str.isEmpty()) {
            return true; // Allow empty strings
        }
        
        // Check each character in the string to be inserted
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c) && c != '.') {
                return false;
            }
        }
        
        // Check if adding decimal would create invalid format (multiple decimals)
        String proposedText = currentText.substring(0, insertPos) + str + currentText.substring(insertPos);
        long decimalCount = proposedText.chars().filter(ch -> ch == '.').count();
        return decimalCount <= 1;
    }
    
    /**
     * Validates if a string is numeric
     * @param str The string to validate
     * @return true if the string is numeric, false otherwise
     */
    public static boolean isValidNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return true; // Allow empty/whitespace
        }
        try {
            Long.parseLong(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validates if a string is numeric with decimal support
     * @param str The string to validate
     * @return true if the string is numeric with optional decimal, false otherwise
     */
    public static boolean isValidNumericWithDecimal(String str) {
        if (str == null || str.trim().isEmpty()) {
            return true; // Allow empty/whitespace
        }
        try {
            Double.parseDouble(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}