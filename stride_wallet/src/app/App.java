package app;

import controller.AuthController;
import view.AuthView;

public class App {

	public static void main(String[] args) {
		// Create the authentication view (login screen)
		AuthView authView = new AuthView();

		// Create and link the authentication controller
		new AuthController(authView);

		// Display the login screen
		authView.setVisible(true);

	}

}
