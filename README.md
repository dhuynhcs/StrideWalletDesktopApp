<p align="center">
  <img width="610" alt="Screenshot 2025-05-20 at 8 20 20 AM" src="https://github.com/user-attachments/assets/66148fa4-4b78-457c-9e72-55b38e9c1626" />
</p>

# StrideWallet - Digital Payment Application
By Daniel Huynh

## Project Overview
StrideWallet is a comprehensive Java Swing application designed to provide a secure and user-friendly digital payment system simulation. This system allows users to manage their digital wallets, transfer money to other users, request payments, manage contacts, and track transaction history.

Future WIP I'm hoping to implement someday:
- Transaction Fees
- Rewards based on fitness activity goals such as weekly step count hence the name Stride Wallet
- Rewards can include things like transaction fee discounts, Web3 coins, cashback, etc

## Database Entries
Users you can test with:

User Table

<img width="847" alt="Screenshot 2025-05-20 at 8 39 43 AM" src="https://github.com/user-attachments/assets/ccbf8633-9d61-424c-bca6-38979550c434" />

Users login with their cashtag aka their username
- John Doe Credentials: johndoe, Password123
- Jane Smith Credentials: janesmith, Password123
- all other accounts also have Password123

Transaction Table

<img width="845" alt="Screenshot 2025-05-20 at 8 47 22 AM" src="https://github.com/user-attachments/assets/e9e31b04-55e2-4369-b59a-72a7e8376e6b" />

Contact Table

<img width="839" alt="Screenshot 2025-05-20 at 8 47 48 AM" src="https://github.com/user-attachments/assets/eb30c3c0-bcfc-4485-8858-6ea4d9e31a86" />

Wallet Table

<img width="845" alt="Screenshot 2025-05-20 at 8 48 11 AM" src="https://github.com/user-attachments/assets/dc728fb8-690b-418a-83a1-70a177f239a6" />

- Wallet and transaction tables could probably be encrypted in the future

## Features

### User Authentication
- Secure login system with SHA-256 password hashing
- Cashtag-based user identification
- Password validation

### Wallet Management
- Digital wallet creation and management
- Real-time balance tracking
- Multi-currency support foundation (currently focused on USD)
- Default wallet assignment

### Money Transfer System
- Send money to any registered user
- Request money from contacts or other users
- Deposit funds to wallet
- Transaction confirmation dialogs

### Contact Management
- Add users to personal contact list
- Remove contacts
- Search contacts by name or cashtag
- User directory with search capability

### Transaction Management
- Comprehensive transaction history
- Multiple transaction types (send, request, deposit)
- Transaction status tracking (completed, requested, declined)
- Color-coded transaction history for easy identification
  - Green for incoming funds
  - Red for outgoing funds
  - Gray for pending requests

### Payment Request System
- Send payment requests to other users
- Approve or decline incoming payment requests
- Request status tracking

## Technical Implementation

### MVC Architecture
The application follows the Model-View-Controller (MVC) architectural pattern:
- **Models**: Represent data structures (User, Wallet, Transaction, Contact)
- **Views**: Handle user interface components
- **Controllers**: Manage application logic and user interactions

### Security
- Password encryption using SHA-256 hashing
- Input validation to prevent malicious entries
- Transaction validation for financial safety

### Database & Concurrency Management
- SQL-based data storage
- Dedicated Data Access Objects (DAOs) for each entity
- Transaction management for ensuring financial data integrity
- Concurrency control via Transaction, Commit, and Rollback for handling multiple simultaneous transactions

## Project Structure
- **view**: Contains all UI components
  - AuthView, CommonView, ProfileView, TransferView
- **controller**: Contains logic for handling user interactions
  - AuthController, CommonController, ProfileController, TransferController
- **model**: Contains data structures
  - User, Wallet, Transaction, Contact
- **dao**: Contains database access objects
  - UserDAO, WalletDAO, TransactionDAO, ContactDAO
- **my_util**: Contains utility classes
  - DatabaseUtil, Security, Verification, RefreshUtility

## Screenshots
<p align="center">
<img width="313" alt="Screenshot 2025-05-20 at 8 18 42 AM" src="https://github.com/user-attachments/assets/021d1ed6-19db-45a7-a6f8-98ce6f793f14" />
</p>

<p align="center">
  <img width="610" alt="Screenshot 2025-05-20 at 8 20 20 AM" src="https://github.com/user-attachments/assets/66148fa4-4b78-457c-9e72-55b38e9c1626" />
</p>

<p align="center">
<img width="807" alt="Screenshot 2025-05-20 at 8 22 55 AM" src="https://github.com/user-attachments/assets/5b99d3df-6e42-475f-b6c5-27c233e4fade" />
</p>

## Installation and Setup
1. Ensure Java JDK 8 or higher is installed
2. Set up a MySQL database and run the included SQL script
3. Configure database connection in DatabaseUtil.java:
   ```java
   private static final String DBURL = "jdbc:mysql://localhost:3306/stridewallet_db";
   private static final String USERNAME = "your_username";
   private static final String PASSWORD = "your_password";
   ```
4. Compile the project:
   ```
   javac -d bin src/**/*.java
   ```
5. Run the application:
   ```
   java -cp bin AuthController
   ```
6. Login:
   ```
   - Users login with their cashtag aka their username
   John Doe Credentials: johndoe, Password123 J
   ane Smith Credentials: janesmith, Password123
   all other accounts also have Password123
   ```


## Database Schema

### Users Table
Stores user account information including personal details and authentication credentials.

### Wallets Table
Manages user wallet information, including balance, currency, and wallet type.

### Transactions Table
Records all financial transactions within the system, including money transfers, requests, and deposits.

### Contacts Table
Stores user contact relationships for quick access to frequent transfer targets.

## Future Enhancements
- Multi-currency support implementation
- Scheduled recurring payments
- Enhanced transaction analytics and reporting
- Mobile companion application
- Two-factor authentication
- Spending category tracking and budgeting tools
