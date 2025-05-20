# StrideWallet - Digital Payment Application
By Daniel Huynh

## Project Overview
StrideWallet is a comprehensive Java Swing application designed to provide a secure and user-friendly digital payment solution. This system allows users to manage their digital wallets, transfer money to other users, request payments, manage contacts, and track transaction history.

Future WIP I'm hoping to implement someday:
Transaction Fees
Rewards based on fitness activity goals such as weekly step count.
Rewards can include things like transaction fee discounts, Web3 coins, cashback, etc

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
[Screenshots would be inserted here]

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
