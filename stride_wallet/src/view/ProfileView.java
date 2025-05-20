package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import model.Transaction;
import model.Wallet;
import model.User;

import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class ProfileView extends JPanel {
    
    private User currentUser;
    private Wallet userWallet;
    private List<Transaction> transactions;
    
    private JPanel transactionsPanel, walletPanel, depositPanel;
    private JTable transactionsTable;
    private DefaultTableModel transactionsTableModel;
    private JTextField depositAmountField;
    private JButton depositButton;
    private JLabel balanceLabel;
    
    // Color constants
    private static final Color COLOR_INCOMING = new Color(30, 180, 30);
    private static final Color COLOR_OUTGOING = new Color(220, 50, 50);
    private static final Color COLOR_PENDING = new Color(80, 80, 80);
    
    public ProfileView(User user, Wallet wallet, List<Transaction> transactions) {
        this.currentUser = user;
        this.userWallet = wallet;
        this.transactions = transactions;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Create top panel to hold wallet and deposit side by side
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        
        // Create wallet panel with balance
        walletPanel = createWalletPanel();
        topPanel.add(walletPanel);
        
        // Create deposit panel
        depositPanel = createDepositPanel();
        topPanel.add(depositPanel);
        
        // Add top panel to content panel
        contentPanel.add(topPanel);
        
        // Add some spacing
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Create transactions panel
        transactionsPanel = createTransactionsPanel();
        contentPanel.add(transactionsPanel);
        
        // Add scrollable content panel
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createWalletPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "My Wallet",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14)
        ));
        
        // Center-align the content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        // Balance header label
        JLabel balanceHeaderLabel = new JLabel("CURRENT BALANCE");
        balanceHeaderLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        balanceHeaderLabel.setForeground(new Color(81, 43, 219));
        balanceHeaderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Balance value label
        DecimalFormat df = new DecimalFormat("0.00");
        balanceLabel = new JLabel("$" + df.format(userWallet.getBalance()));
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        centerPanel.add(balanceHeaderLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(balanceLabel);
        
        panel.add(centerPanel);
        
        return panel;
    }
    
    private JPanel createDepositPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Center-align the content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Deposit header label
        JLabel depositHeaderLabel = new JLabel("DEPOSIT FUNDS");
        depositHeaderLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        depositHeaderLabel.setForeground(new Color(81, 43, 219));
        depositHeaderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Input field and button panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        // Amount input
        depositAmountField = new JTextField(15);
        depositAmountField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        // Deposit button
        depositButton = new JButton("Deposit");
        depositButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        inputPanel.add(depositAmountField);
        inputPanel.add(depositButton);
        
        centerPanel.add(depositHeaderLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(inputPanel);
        
        panel.add(centerPanel);
        
        return panel;
    }
    
    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Transaction History",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14)
        ));
        
        // Create table model with columns
        String[] columnNames = {"Date", "Type", "Amount", "Status", "Note"};
        transactionsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        // Create table
        transactionsTable = new JTable(transactionsTableModel);
        transactionsTable.setRowHeight(25);
        transactionsTable.setShowGrid(false);
        transactionsTable.setIntercellSpacing(new Dimension(0, 0));
        transactionsTable.getTableHeader().setReorderingAllowed(false);
        
        // Add custom renderers for Type, Amount, and Status columns
        DefaultTableCellRenderer typeRenderer = new TransactionCellRenderer(1);
        DefaultTableCellRenderer amountRenderer = new TransactionCellRenderer(2);
        DefaultTableCellRenderer statusRenderer = new TransactionCellRenderer(3);
        
        transactionsTable.getColumnModel().getColumn(1).setCellRenderer(typeRenderer);   // Type column
        transactionsTable.getColumnModel().getColumn(2).setCellRenderer(amountRenderer); // Amount column
        transactionsTable.getColumnModel().getColumn(3).setCellRenderer(statusRenderer); // Status column
        
        // Fill in transaction data
        populateTransactionsTable();
        
        // Add table to a scroll pane
        JScrollPane tableScrollPane = new JScrollPane(transactionsTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(tableScrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    //Custom renderer class for transaction table cells with consistent coloring logic
    private class TransactionCellRenderer extends DefaultTableCellRenderer {
        private int columnIndex;
        
        public TransactionCellRenderer(int columnIndex) {
            this.columnIndex = columnIndex;
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            
            // Get transaction data
            String type = (String) table.getModel().getValueAt(row, 1);
            String status = (String) table.getModel().getValueAt(row, 3);
            int modelRow = table.convertRowIndexToModel(row);
            Transaction txn = transactions.get(modelRow);
            
            // Apply the appropriate color
            Color textColor = determineTransactionColor(txn, type, status);
            c.setForeground(textColor);
            
            // Special case for type column display
            if (columnIndex == 1 && type.equals("send") && 
                txn.getReceiverWalletId() == userWallet.getWalletId()) {
                // If user is receiver, update display text to "deposit" for clarity
                setText("deposit");
            }
            
            if (isSelected) {
                c.setBackground(new Color(240, 240, 255));
            } else {
                c.setBackground(Color.WHITE);
            }
            
            return c;
        }
    }
    
   
    private Color determineTransactionColor(Transaction txn, String type, String status) {
        // Incomplete requests are dark grey
        if ("requested".equals(status)) {
            return COLOR_PENDING;
        }
        
        // For completed requests, check if user is sender or receiver
        if (type.equals("request")) {
            if (txn.getSenderWalletId() == userWallet.getWalletId()) {
                // User is the one who paid (sender) - outgoing money
                return COLOR_OUTGOING;
            } else if (txn.getReceiverWalletId() == userWallet.getWalletId()) {
                // User is the one who requested (receiver) - incoming money
                return COLOR_INCOMING;
            }
        } 
        // For regular transactions
        else if (type.equals("send")) {
            // If this wallet is the receiver, it's incoming money (green)
            if (txn.getReceiverWalletId() == userWallet.getWalletId()) {
                return COLOR_INCOMING;
            } else {
                return COLOR_OUTGOING;
            }
        } else if (type.equals("withdrawal")) {
            return COLOR_OUTGOING;
        } else {
            // deposit and other types are green
            return COLOR_INCOMING;
        }
        
        // Default color (should never reach here)
        return Color.BLACK;
    }
    
    private void populateTransactionsTable() {
        // Clear existing rows
        while (transactionsTableModel.getRowCount() > 0) {
            transactionsTableModel.removeRow(0);
        }
        
        // Date formatter
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        
        // Add transaction rows
        for (Transaction transaction : transactions) {
            String date = dateFormat.format(transaction.getCreatedAt());
            
            // Determine transaction type to display
            String displayType = transaction.getTransactionType();
            
            String amount = "$" + transaction.getAmount().toString();
            String status = transaction.getStatus();
            String note = transaction.getNote();
            
            // Add row to table
            transactionsTableModel.addRow(new Object[]{date, displayType, amount, status, note});
        }
    }
    
    // Method to update the view with new data
    public void updateData(Wallet wallet, List<Transaction> transactions) {
        this.userWallet = wallet;
        this.transactions = transactions;
        
        // Update wallet balance
        DecimalFormat df = new DecimalFormat("0.00");
        balanceLabel.setText("$" + df.format(userWallet.getBalance()));
        
        // Update transactions table
        populateTransactionsTable();
    }
    
    // Getters for controller access
    public JTextField getDepositAmountField() {
        return depositAmountField;
    }
    
    public JButton getDepositButton() {
        return depositButton;
    }
    
    public JLabel getBalanceLabel() {
        return balanceLabel;
    }
}