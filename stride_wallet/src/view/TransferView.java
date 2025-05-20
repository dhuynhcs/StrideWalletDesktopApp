package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.User;
import model.Wallet;
import model.Transaction;

import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class TransferView extends JPanel {
    
    // UI Components
    private JSplitPane mainSplitPane, contactsDirectorySplitPane;
    private JPanel contactsPanel, directoryPanel, transferPanel, requestsPanel;
    
    // Contacts table
    private JTextField contactsSearchField;
    private JTable contactsTable;
    private DefaultTableModel contactsTableModel;
    private JButton removeContactButton;
    
    // Directory table (all users)
    private JTextField directorySearchField;
    private JTable directoryTable;
    private DefaultTableModel directoryTableModel;
    private JButton addContactButton;
    
    // Pending requests table
    private JTable requestsTable;
    private DefaultTableModel requestsTableModel;
    private JButton approveButton, declineButton;
    
    // Transfer form components
    private JTextField recipientField;
    private JTextField amountField;
    private JTextField noteField;
    private JButton sendButton;
    private JButton requestButton;
    private JLabel selectedContactLabel;
    
    public TransferView() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create main vertical split pane
        JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        verticalSplitPane.setDividerLocation(400);
        
        // Create panels for the top section
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Create horizontal split pane for contacts/directory and transfer form
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(500);
        
        // Create split pane for contacts and directory
        contactsDirectorySplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        contactsDirectorySplitPane.setDividerLocation(250);
        
        // Build panels
        buildContactsPanel();
        buildDirectoryPanel();
        buildTransferPanel();
        buildRequestsPanel();
        
        // Add contacts and directory to their split pane
        contactsDirectorySplitPane.setLeftComponent(contactsPanel);
        contactsDirectorySplitPane.setRightComponent(directoryPanel);
        
        // Add panels to main split pane
        mainSplitPane.setLeftComponent(contactsDirectorySplitPane);
        mainSplitPane.setRightComponent(transferPanel);
        
        // Add to vertical split pane
        verticalSplitPane.setTopComponent(mainSplitPane);
        verticalSplitPane.setBottomComponent(requestsPanel);
        
        // Add to main panel
        add(verticalSplitPane, BorderLayout.CENTER);
    }
    
    private void buildContactsPanel() {
        contactsPanel = new JPanel(new BorderLayout(0, 10));
        contactsPanel.setBorder(BorderFactory.createTitledBorder("My Contacts"));
        
        // Search field
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        contactsSearchField = new JTextField();
        searchPanel.add(new JLabel("Search Contacts:"), BorderLayout.NORTH);
        searchPanel.add(contactsSearchField, BorderLayout.CENTER);
        
        // Contacts table
        String[] contactColumns = {"Name", "Cashtag"};
        contactsTableModel = new DefaultTableModel(contactColumns, 0);
        contactsTable = new JTable(contactsTableModel);
        contactsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactsTable.setAutoCreateRowSorter(true);
        
        // Add selection listener to contacts table
        contactsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = contactsTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        String name = (String) contactsTable.getValueAt(selectedRow, 0);
                        String cashtag = (String) contactsTable.getValueAt(selectedRow, 1);
                        
                        recipientField.setText(cashtag);
                        selectedContactLabel.setText("Selected: " + name + " (" + cashtag + ")");
                        
                        // Enable remove button when a contact is selected
                        removeContactButton.setEnabled(true);
                    } else {
                        removeContactButton.setEnabled(false);
                    }
                }
            }
        });
        
        // Button panel for "Remove Contact"
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        removeContactButton = new JButton("Remove Contact");
        removeContactButton.setEnabled(false);
        buttonPanel.add(removeContactButton);
        
        // Add components to panel
        contactsPanel.add(searchPanel, BorderLayout.NORTH);
        contactsPanel.add(new JScrollPane(contactsTable), BorderLayout.CENTER);
        contactsPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void buildDirectoryPanel() {
        directoryPanel = new JPanel(new BorderLayout(0, 10));
        directoryPanel.setBorder(BorderFactory.createTitledBorder("User Directory"));
        
        // Search field
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        directorySearchField = new JTextField();
        searchPanel.add(new JLabel("Search Directory:"), BorderLayout.NORTH);
        searchPanel.add(directorySearchField, BorderLayout.CENTER);
        
        // Directory table
        String[] directoryColumns = {"Name", "Cashtag"};
        directoryTableModel = new DefaultTableModel(directoryColumns, 0);
        directoryTable = new JTable(directoryTableModel);
        directoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        directoryTable.setAutoCreateRowSorter(true);
        
        // Add selection listener to directory table
        directoryTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = directoryTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        String name = (String) directoryTable.getValueAt(selectedRow, 0);
                        String cashtag = (String) directoryTable.getValueAt(selectedRow, 1);
                        
                        recipientField.setText(cashtag);
                        selectedContactLabel.setText("Selected: " + name + " (" + cashtag + ")");
                        
                        // Enable add button when a directory user is selected
                        addContactButton.setEnabled(true);
                    } else {
                        addContactButton.setEnabled(false);
                    }
                }
            }
        });
        
        // Button panel for "Add Contact"
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addContactButton = new JButton("Add Contact");
        addContactButton.setEnabled(false);
        buttonPanel.add(addContactButton);
        
        // Add components to panel
        directoryPanel.add(searchPanel, BorderLayout.NORTH);
        directoryPanel.add(new JScrollPane(directoryTable), BorderLayout.CENTER);
        directoryPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void buildTransferPanel() {
        transferPanel = new JPanel();
        transferPanel.setLayout(new BoxLayout(transferPanel, BoxLayout.Y_AXIS));
        transferPanel.setBorder(BorderFactory.createTitledBorder("Money Transfer"));
        
        // Create form panel with padding
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Selected contact info
        JPanel selectedContactPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectedContactLabel = new JLabel("No contact selected");
        selectedContactLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        selectedContactPanel.add(selectedContactLabel);
        
        // Recipient field
        JPanel recipientPanel = new JPanel(new BorderLayout(5, 5));
        recipientPanel.add(new JLabel("Recipient Cashtag:"), BorderLayout.NORTH);
        recipientField = new JTextField();
        recipientPanel.add(recipientField, BorderLayout.CENTER);
        
        // Amount field
        JPanel amountPanel = new JPanel(new BorderLayout(5, 5));
        amountPanel.add(new JLabel("Amount ($):"), BorderLayout.NORTH);
        amountField = new JTextField();
        amountPanel.add(amountField, BorderLayout.CENTER);
        
        // Note field
        JPanel notePanel = new JPanel(new BorderLayout(5, 5));
        notePanel.add(new JLabel("Note (optional):"), BorderLayout.NORTH);
        noteField = new JTextField();
        notePanel.add(noteField, BorderLayout.CENTER);
        
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sendButton = new JButton("Send Money");
        requestButton = new JButton("Request Money");
        buttonsPanel.add(sendButton);
        buttonsPanel.add(requestButton);
        
        // Make components have consistent width
        Dimension preferredSize = new Dimension(Integer.MAX_VALUE, recipientPanel.getPreferredSize().height);
        recipientPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferredSize.height));
        amountPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferredSize.height));
        notePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferredSize.height));
        
        // Add components to form panel with spacing
        formPanel.add(selectedContactPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(recipientPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(amountPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(notePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(buttonsPanel);
        
        transferPanel.add(formPanel);
    }
    
    private void buildRequestsPanel() {
        requestsPanel = new JPanel(new BorderLayout());
        requestsPanel.setBorder(BorderFactory.createTitledBorder("Pending Requests"));
        
        // Requests table
        String[] requestColumns = {"Request", "Date", "Amount", "Note", "Status"};
        requestsTableModel = new DefaultTableModel(requestColumns, 0);
        requestsTable = new JTable(requestsTableModel);
        requestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestsTable.setAutoCreateRowSorter(true);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        approveButton = new JButton("Approve");
        declineButton = new JButton("Decline");
        buttonPanel.add(approveButton);
        buttonPanel.add(declineButton);
        
        // Add components to panel
        requestsPanel.add(new JScrollPane(requestsTable), BorderLayout.CENTER);
        requestsPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    // Getters for controller access
    public JTextField getContactsSearchField() {
        return contactsSearchField;
    }
    
    public JTextField getDirectorySearchField() {
        return directorySearchField;
    }
    
    public JTable getContactsTable() {
        return contactsTable;
    }
    
    public DefaultTableModel getContactsTableModel() {
        return contactsTableModel;
    }
    
    public JTable getDirectoryTable() {
        return directoryTable;
    }
    
    public DefaultTableModel getDirectoryTableModel() {
        return directoryTableModel;
    }
    
    public JTable getRequestsTable() {
        return requestsTable;
    }
    
    public DefaultTableModel getRequestsTableModel() {
        return requestsTableModel;
    }
    
    public JTextField getRecipientField() {
        return recipientField;
    }
    
    public JTextField getAmountField() {
        return amountField;
    }
    
    public JTextField getNoteField() {
        return noteField;
    }
    
    public JButton getSendButton() {
        return sendButton;
    }
    
    public JButton getRequestButton() {
        return requestButton;
    }
    
    public JButton getApproveButton() {
        return approveButton;
    }
    
    public JButton getDeclineButton() {
        return declineButton;
    }
    
    public JButton getAddContactButton() {
        return addContactButton;
    }
    
    public JButton getRemoveContactButton() {
        return removeContactButton;
    }
    
   
}