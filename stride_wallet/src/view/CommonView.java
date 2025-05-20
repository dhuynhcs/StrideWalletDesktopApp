package view;

import javax.swing.*;
import java.awt.*;

public class CommonView extends JPanel {

	private JPanel topbarPanel, mainContentPanel;
	private JLabel nameLabel, lastNameLabel, balanceLabel;
	private JButton transferButton, profileButton, logoutButton;
	private static final Color TOPBAR_COLOR = new Color(81, 43, 219);
	private JFrame parentFrame; // Reference to the main frame

	public CommonView(JFrame frame) {
		this.parentFrame = frame;
		setLayout(new BorderLayout());
		buildTopbarPanel();
		add(topbarPanel, BorderLayout.NORTH);
		mainContentPanel = new JPanel(new CardLayout());
		add(mainContentPanel, BorderLayout.CENTER);

		((CardLayout) mainContentPanel.getLayout()).show(mainContentPanel, "default");
	}

	private void buildTopbarPanel() {
		topbarPanel = new JPanel();
		topbarPanel.setLayout(new BoxLayout(topbarPanel, BoxLayout.X_AXIS));
		topbarPanel.setBackground(TOPBAR_COLOR);
		topbarPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		topbarPanel.setPreferredSize(new Dimension(800, 60));

		// Add user information section
		JPanel userInfoPanel = new JPanel();
		userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.X_AXIS));
		userInfoPanel.setBackground(TOPBAR_COLOR);
		userInfoPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
		
		// Add header labels
		nameLabel = new JLabel("John");
		styleLabel(nameLabel, 16);
		
		lastNameLabel = new JLabel("Doe");
		styleLabel(lastNameLabel, 16);
		
		balanceLabel = new JLabel("$1,000.00");
		styleLabel(balanceLabel, 14);
		
		userInfoPanel.add(nameLabel);
		userInfoPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		userInfoPanel.add(lastNameLabel);
		userInfoPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		userInfoPanel.add(balanceLabel);
		
		// Add navigation buttons
		JPanel navigationPanel = new JPanel();
		navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));
		navigationPanel.setBackground(TOPBAR_COLOR);
		navigationPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
		
		transferButton = createNavButton("Transfer");
		profileButton = createNavButton("Profile");
		
		navigationPanel.add(Box.createHorizontalGlue());
		navigationPanel.add(transferButton);
		navigationPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		navigationPanel.add(profileButton);
		
		// Add logout button
		logoutButton = createNavButton("Logout");
		
		// Add all components to topbar
		topbarPanel.add(userInfoPanel);
		topbarPanel.add(Box.createHorizontalGlue());
		topbarPanel.add(navigationPanel);
		topbarPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		topbarPanel.add(logoutButton);
	}

	//style utility function for our labels
	private void styleLabel(JLabel label, int fontSize) {
		label.setForeground(Color.WHITE);
		label.setFont(label.getFont().deriveFont(Font.BOLD, fontSize));
		label.setAlignmentY(Component.CENTER_ALIGNMENT);
	}

	//universal function for creating nav buttons
	private JButton createNavButton(String text) {
		JButton button = new JButton(text);
		Dimension buttonSize = new Dimension(100, 30);
		button.setPreferredSize(buttonSize);
		button.setMaximumSize(buttonSize);
		button.setAlignmentY(Component.CENTER_ALIGNMENT);
		return button;
	}

	//getters and setters
	public JButton getBtnProfile() {
		return profileButton;
	}

	public JButton getBtnTransfer() {
		return transferButton;
	}

	public JButton getBtnLogout() {
		return logoutButton;
	}

	public JPanel getMainContentPanel() {
		return mainContentPanel;
	}

	public JLabel getNameLabel() {
		return nameLabel;
	}

	public JLabel getLastNameLabel() {
		return lastNameLabel;
	}

	public void setLastNameLabel(JLabel lastNameLabel) {
		this.lastNameLabel = lastNameLabel;
	}

	public void setNameLabel(JLabel nameLabel) {
		this.nameLabel = nameLabel;
	}

	public JLabel getBalanceLabel() {
		return balanceLabel;
	}
 
	public void setBalanceLabel(JLabel balanceLabel) {
		this.balanceLabel = balanceLabel;
	}

	//view testing
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("CashApp Dashboard");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			CommonView commonPanel = new CommonView(frame);
			frame.setContentPane(commonPanel);
			frame.setSize(800, 600);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}
}