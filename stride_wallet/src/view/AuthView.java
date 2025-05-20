package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.*;

public class AuthView extends JFrame {
    private JTextField txtCashtag;
    private JPasswordField txtPassword;
    private JPanel loginPanel, welcomePanel, mainPanel;
    private JButton btnLogin, btnClear;

    public AuthView() {
        setTitle("App - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Using a vertical grid layout with 2 rows and 1 column
        mainPanel = new JPanel(new GridLayout(2, 1));

        buildWelcomePanel();
        buildLoginPanel();

        mainPanel.add(welcomePanel);
        mainPanel.add(loginPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Set the size and position
        setSize(300, 400);
        centerWindow();

        // Make the window visible
        setVisible(true);
    }

    public void buildWelcomePanel() {
        welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(81, 43, 219));

        JLabel welcomeLabel = new JLabel("StrideWallet", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE);

        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
    }

    public void buildLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Single credentials panel with 2 rows, 2 columns
        // add fields and set color
        JPanel credentialsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        credentialsPanel.setBackground(Color.WHITE);
        credentialsPanel.add(new JLabel("Cashtag:"));
        txtCashtag = new JTextField();
        credentialsPanel.add(txtCashtag);
        credentialsPanel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        credentialsPanel.add(txtPassword);

        // Button interaction panel
        JPanel btnPanel = new JPanel();
        btnClear = new JButton("Clear");
        btnLogin = new JButton("Login");
        btnPanel.add(btnClear);
        btnPanel.add(btnLogin);
        btnPanel.setBackground(Color.WHITE);

        loginPanel.add(Box.createVerticalGlue());

        // add the panels to our login
        loginPanel.add(credentialsPanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        loginPanel.add(btnPanel);

        loginPanel.add(Box.createVerticalGlue());
    }

    //center the window
    private void centerWindow() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);
    }

    //getters and setters
    public JTextField getTxtCashtagField() {
        return txtCashtag;
    }

    public void setTxtCashtag(JTextField txtCashtag) {
        this.txtCashtag = txtCashtag;
    }

    public JPasswordField getTxtPasswordField() {
        return txtPassword;
    }

    public void setTxtPassword(JPasswordField txtPassword) {
        this.txtPassword = txtPassword;
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public void setBtnLogin(JButton btnLogin) {
        this.btnLogin = btnLogin;
    }

    public JButton getBtnClear() {
        return btnClear;
    }

    // view testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AuthView();
            }
        });
    }
}