// File: exception/InsufficientFundsException.java
package exception;

public class InsufficientFundsException extends Exception {
    private final double required;
    private final double available;

    public InsufficientFundsException(double required, double available) {
        super(String.format("Insufficient funds. Required: $%.2f, Available: $%.2f", required, available));
        this.required = required;
        this.available = available;
    }

    public double getRequired() { return required; }
    public double getAvailable() { return available; }
}

// File: exception/InsufficientSharesException.java
package exception;

public class InsufficientSharesException extends Exception {
    private final int requested;
    private final int available;

    public InsufficientSharesException(int requested, int available) {
        super(String.format("Insufficient shares. Requested: %d, Available: %d", requested, available));
        this.requested = requested;
        this.available = available;
    }

    public int getRequested() { return requested; }
    public int getAvailable() { return available; }
}

// File: exception/StockNotFoundException.java
package exception;

public class StockNotFoundException extends Exception {
    private final String ticker;

    public StockNotFoundException(String ticker) {
        super("Stock not found with ticker symbol: " + ticker);
        this.ticker = ticker;
    }

    public String getTicker() { return ticker; }
}

// File: exception/InvalidInputException.java
package exception;

public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}

// File: model/Stock.java
package model;

import java.util.Objects;

public class Stock {
    private final String ticker;
    private String companyName;
    private double currentPrice;
    private int availableShares;
    private final String sector;

    public Stock(String ticker, String companyName, double currentPrice, int availableShares, String sector) {
        validateTicker(ticker);
        validateCompanyName(companyName);
        validatePrice(currentPrice);
        validateShares(availableShares);
        this.ticker = ticker.toUpperCase();
        this.companyName = companyName;
        this.currentPrice = currentPrice;
        this.availableShares = availableShares;
        this.sector = sector;
    }

    private void validateTicker(String ticker) {
        if (ticker == null || ticker.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticker symbol cannot be null or empty.");
        }
        if (ticker.length() > 5) {
            throw new IllegalArgumentException("Ticker symbol cannot exceed 5 characters.");
        }
    }

    private void validateCompanyName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Company name cannot be null or empty.");
        }
    }

    private void validatePrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Stock price must be greater than zero.");
        }
    }

    private void validateShares(int shares) {
        if (shares < 0) {
            throw new IllegalArgumentException("Available shares cannot be negative.");
        }
    }

    public String getTicker() { return ticker; }
    public String getCompanyName() { return companyName; }
    public double getCurrentPrice() { return currentPrice; }
    public int getAvailableShares() { return availableShares; }
    public String getSector() { return sector; }

    public void setCompanyName(String companyName) {
        validateCompanyName(companyName);
        this.companyName = companyName;
    }

    public void setCurrentPrice(double currentPrice) {
        validatePrice(currentPrice);
        this.currentPrice = currentPrice;
    }

    public void setAvailableShares(int availableShares) {
        validateShares(availableShares);
        this.availableShares = availableShares;
    }

    public void decreaseShares(int quantity) {
        if (quantity > availableShares) {
            throw new IllegalStateException("Cannot decrease shares below zero.");
        }
        this.availableShares -= quantity;
    }

    public void increaseShares(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to increase must be positive.");
        }
        this.availableShares += quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock)) return false;
        Stock stock = (Stock) o;
        return Objects.equals(ticker, stock.ticker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker);
    }

    @Override
    public String toString() {
        return String.format("%-6s | %-30s | $%10.2f | Shares: %8d | Sector: %s",
                ticker, companyName, currentPrice, availableShares, sector);
    }
}

// File: model/TransactionType.java
package model;

public enum TransactionType {
    BUY("Buy"),
    SELL("Sell");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }

    @Override
    public String toString() { return displayName; }
}

// File: model/Transaction.java
package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class Transaction {
    private final String transactionId;
    private final String ticker;
    private final String companyName;
    private final TransactionType type;
    private final int quantity;
    private final double pricePerShare;
    private final double totalAmount;
    private final LocalDateTime timestamp;

    public Transaction(String ticker, String companyName, TransactionType type,
                       int quantity, double pricePerShare) {
        this.transactionId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.ticker = ticker;
        this.companyName = companyName;
        this.type = type;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.totalAmount = quantity * pricePerShare;
        this.timestamp = LocalDateTime.now();
    }

    public String getTransactionId() { return transactionId; }
    public String getTicker() { return ticker; }
    public String getCompanyName() { return companyName; }
    public TransactionType getType() { return type; }
    public int getQuantity() { return quantity; }
    public double getPricePerShare() { return pricePerShare; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %s | %-6s | %-4s | Qty: %6d | Price: $%8.2f | Total: $%12.2f | %s",
                transactionId, timestamp.format(formatter), ticker,
                type.getDisplayName(), quantity, pricePerShare, totalAmount,
                type == TransactionType.BUY ? "DEBIT" : "CREDIT");
    }
}

// File: model/PortfolioHolding.java
package model;

public class PortfolioHolding {
    private final String ticker;
    private final String companyName;
    private int quantity;
    private double averageBuyPrice;
    private double totalInvested;

    public PortfolioHolding(String ticker, String companyName, int quantity, double buyPrice) {
        this.ticker = ticker;
        this.companyName = companyName;
        this.quantity = quantity;
        this.averageBuyPrice = buyPrice;
        this.totalInvested = quantity * buyPrice;
    }

    public String getTicker() { return ticker; }
    public String getCompanyName() { return companyName; }
    public int getQuantity() { return quantity; }
    public double getAverageBuyPrice() { return averageBuyPrice; }
    public double getTotalInvested() { return totalInvested; }

    public void addShares(int additionalQuantity, double buyPrice) {
        double newInvestment = additionalQuantity * buyPrice;
        totalInvested += newInvestment;
        quantity += additionalQuantity;
        averageBuyPrice = totalInvested / quantity;
    }

    public void removeShares(int quantityToRemove) {
        if (quantityToRemove > quantity) {
            throw new IllegalArgumentException("Cannot remove more shares than held.");
        }
        double proportionRemoved = (double) quantityToRemove / quantity;
        totalInvested -= totalInvested * proportionRemoved;
        quantity -= quantityToRemove;
        if (quantity == 0) {
            totalInvested = 0;
            averageBuyPrice = 0;
        }
    }

    public double calculateCurrentValue(double currentPrice) {
        return quantity * currentPrice;
    }

    public double calculateProfitLoss(double currentPrice) {
        return calculateCurrentValue(currentPrice) - totalInvested;
    }

    public double calculateProfitLossPercentage(double currentPrice) {
        if (totalInvested == 0) return 0;
        return (calculateProfitLoss(currentPrice) / totalInvested) * 100;
    }

    @Override
    public String toString() {
        return String.format("%-6s | %-30s | Qty: %6d | Avg Buy: $%8.2f | Invested: $%12.2f",
                ticker, companyName, quantity, averageBuyPrice, totalInvested);
    }
}

// File: model/Investor.java
package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Investor {
    private final String investorId;
    private String name;
    private String email;
    private double walletBalance;
    private final List<PortfolioHolding> portfolio;
    private final List<Transaction> transactionHistory;

    public Investor(String investorId, String name, String email, double initialBalance) {
        validateInvestorId(investorId);
        validateName(name);
        validateEmail(email);
        validateBalance(initialBalance);
        this.investorId = investorId;
        this.name = name;
        this.email = email;
        this.walletBalance = initialBalance;
        this.portfolio = new ArrayList<>();
        this.transactionHistory = new ArrayList<>();
    }

    private void validateInvestorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Investor ID cannot be null or empty.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Investor name cannot be null or empty.");
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email address format.");
        }
    }

    private void validateBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
    }

    public String getInvestorId() { return investorId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public double getWalletBalance() { return walletBalance; }
    public List<PortfolioHolding> getPortfolio() { return Collections.unmodifiableList(portfolio); }
    public List<Transaction> getTransactionHistory() { return Collections.unmodifiableList(transactionHistory); }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public void setEmail(String email) {
        validateEmail(email);
        this.email = email;
    }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        walletBalance += amount;
    }

    public void deductBalance(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deduction amount must be positive.");
        if (amount > walletBalance) throw new IllegalStateException("Insufficient wallet balance.");
        walletBalance -= amount;
    }

    public void creditBalance(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Credit amount must be positive.");
        walletBalance += amount;
    }

    public void addTransaction(Transaction transaction) {
        Objects.requireNonNull(transaction, "Transaction cannot be null.");
        transactionHistory.add(transaction);
    }

    public void addOrUpdateHolding(String ticker, String companyName, int quantity, double price) {
        PortfolioHolding existing = findHolding(ticker);
        if (existing != null) {
            existing.addShares(quantity, price);
        } else {
            portfolio.add(new PortfolioHolding(ticker, companyName, quantity, price));
        }
    }

    public void reduceOrRemoveHolding(String ticker, int quantity) {
        PortfolioHolding holding = findHolding(ticker);
        if (holding == null) throw new IllegalStateException("Holding not found for ticker: " + ticker);
        holding.removeShares(quantity);
        if (holding.getQuantity() == 0) {
            portfolio.removeIf(h -> h.getTicker().equals(ticker));
        }
    }

    public PortfolioHolding findHolding(String ticker) {
        return portfolio.stream()
                .filter(h -> h.getTicker().equalsIgnoreCase(ticker))
                .findFirst()
                .orElse(null);
    }

    public int getHeldShares(String ticker) {
        PortfolioHolding holding = findHolding(ticker);
        return holding != null ? holding.getQuantity() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Investor)) return false;
        Investor investor = (Investor) o;
        return Objects.equals(investorId, investor.investorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(investorId);
    }

    @Override
    public String toString() {
        return String.format("ID: %-10s | Name: %-20s | Email: %-30s | Balance: $%.2f",
                investorId, name, email, walletBalance);
    }
}

// File: service/StockMarket.java
package service;

import exception.InvalidInputException;
import exception.StockNotFoundException;
import model.Stock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StockMarket {
    private final List<Stock> listedStocks;

    public StockMarket() {
        this.listedStocks = new ArrayList<>();
        seedDefaultStocks();
    }

    private void seedDefaultStocks() {
        listedStocks.add(new Stock("AAPL", "Apple Inc.", 189.50, 10000, "Technology"));
        listedStocks.add(new Stock("GOOGL", "Alphabet Inc.", 141.80, 8000, "Technology"));
        listedStocks.add(new Stock("MSFT", "Microsoft Corporation", 415.20, 12000, "Technology"));
        listedStocks.add(new Stock("AMZN", "Amazon.com Inc.", 185.60, 9000, "Consumer Discretionary"));
        listedStocks.add(new Stock("TSLA", "Tesla Inc.", 248.50, 7000, "Automotive"));
        listedStocks.add(new Stock("JPM", "JPMorgan Chase & Co.", 197.30, 6000, "Finance"));
        listedStocks.add(new Stock("JNJ", "Johnson & Johnson", 161.20, 5000, "Healthcare"));
        listedStocks.add(new Stock("V", "Visa Inc.", 278.90, 4500, "Finance"));
        listedStocks.add(new Stock("NVDA", "NVIDIA Corporation", 875.40, 3000, "Technology"));
        listedStocks.add(new Stock("META", "Meta Platforms Inc.", 502.30, 5500, "Technology"));
    }

    public void listStock(Stock stock) throws InvalidInputException {
        if (stock == null) throw new InvalidInputException("Stock cannot be null.");
        if (findStockByTicker(stock.getTicker()).isPresent()) {
            throw new InvalidInputException("A stock with ticker '" + stock.getTicker() + "' is already listed.");
        }
        listedStocks.add(stock);
    }

    public void delistStock(String ticker) throws StockNotFoundException, InvalidInputException {
        if (ticker == null || ticker.trim().isEmpty()) {
            throw new InvalidInputException("Ticker symbol cannot be null or empty.");
        }
        Stock stock = getStockByTicker(ticker);
        listedStocks.remove(stock);
    }

    public Stock getStockByTicker(String ticker) throws StockNotFoundException {
        return findStockByTicker(ticker)
                .orElseThrow(() -> new StockNotFoundException(ticker.toUpperCase()));
    }

    public Optional<Stock> findStockByTicker(String ticker) {
        if (ticker == null) return Optional.empty();
        return listedStocks.stream()
                .filter(s -> s.getTicker().equalsIgnoreCase(ticker))
                .findFirst();
    }

    public List<Stock> getAllListedStocks() {
        return Collections.unmodifiableList(listedStocks);
    }

    public List<Stock> getStocksBySector(String sector) {
        if (sector == null || sector.trim().isEmpty()) return Collections.emptyList();
        return listedStocks.stream()
                .filter(s -> s.getSector().equalsIgnoreCase(sector))
                .collect(Collectors.toList());
    }

    public List<Stock> searchStocksByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return Collections.emptyList();
        String lowerKeyword = keyword.toLowerCase();
        return listedStocks.stream()
                .filter(s -> s.getCompanyName().toLowerCase().contains(lowerKeyword)
                        || s.getTicker().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public void updateStockPrice(String ticker, double newPrice)
            throws StockNotFoundException, InvalidInputException {
        if (newPrice <= 0) throw new InvalidInputException("Stock price must be greater than zero.");
        Stock stock = getStockByTicker(ticker);
        stock.setCurrentPrice(newPrice);
    }

    public int getTotalListedStocksCount() {
        return listedStocks.size();
    }
}

// File: service/TradingService.java
package service;

import exception.*;
import model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TradingService {
    private final StockMarket stockMarket;
    private final List<Investor> registeredInvestors;
    private final List<Transaction> allTransactions;

    public TradingService(StockMarket stockMarket) {
        Objects.requireNonNull(stockMarket, "StockMarket cannot be null.");
        this.stockMarket = stockMarket;
        this.registeredInvestors = new ArrayList<>();
        this.allTransactions = new ArrayList<>();
    }

    public void registerInvestor(Investor investor) throws InvalidInputException {
        if (investor == null) throw new InvalidInputException("Investor cannot be null.");
        if (findInvestorById(investor.getInvestorId()).isPresent()) {
            throw new InvalidInputException("An investor with ID '" + investor.getInvestorId() + "' is already registered.");
        }
        registeredInvestors.add(investor);
    }

    public Investor getInvestorById(String investorId)
            throws StockNotFoundException, InvalidInputException {
        if (investorId == null || investorId.trim().isEmpty()) {
            throw new InvalidInputException("Investor ID cannot be null or empty.");
        }
        return findInvestorById(investorId)
                .orElseThrow(() -> new StockNotFoundException("Investor not found with ID: " + investorId));
    }

    private Optional<Investor> findInvestorById(String investorId) {
        if (investorId == null) return Optional.empty();
        return registeredInvestors.stream()
                .filter(i -> i.getInvestorId().equalsIgnoreCase(investorId))
                .findFirst();
    }

    public void buyStock(String investorId, String ticker, int quantity)
            throws StockNotFoundException, InsufficientFundsException,
            InsufficientSharesException, InvalidInputException {

        validateQuantity(quantity);
        Investor investor = getInvestorById(investorId);
        Stock stock = stockMarket.getStockByTicker(ticker);

        if (stock.getAvailableShares() < quantity) {
            throw new InsufficientSharesException(quantity, stock.getAvailableShares());
        }

        double totalCost = quantity * stock.getCurrentPrice();
        if (investor.getWalletBalance() < totalCost) {
            throw new InsufficientFundsException(totalCost, investor.getWalletBalance());
        }

        investor.deductBalance(totalCost);
        stock.decreaseShares(quantity);
        investor.addOrUpdateHolding(stock.getTicker(), stock.getCompanyName(), quantity, stock.getCurrentPrice());

        Transaction transaction = new Transaction(
                stock.getTicker(), stock.getCompanyName(),
                TransactionType.BUY, quantity, stock.getCurrentPrice());

        investor.addTransaction(transaction);
        allTransactions.add(transaction);
    }

    public void sellStock(String investorId, String ticker, int quantity)
            throws StockNotFoundException, InsufficientSharesException, InvalidInputException {

        validateQuantity(quantity);
        Investor investor = getInvestorById(investorId);
        Stock stock = stockMarket.getStockByTicker(ticker);

        int heldShares = investor.getHeldShares(ticker);
        if (heldShares < quantity) {
            throw new InsufficientSharesException(quantity, heldShares);
        }

        double totalProceeds = quantity * stock.getCurrentPrice();
        investor.reduceOrRemoveHolding(ticker, quantity);
        stock.increaseShares(quantity);
        investor.creditBalance(totalProceeds);

        Transaction transaction = new Transaction(
                stock.getTicker(), stock.getCompanyName(),
                TransactionType.SELL, quantity, stock.getCurrentPrice());

        investor.addTransaction(transaction);
        allTransactions.add(transaction);
    }

    public PortfolioSummary getPortfolioSummary(String investorId)
            throws StockNotFoundException, InvalidInputException {
        Investor investor = getInvestorById(investorId);
        List<PortfolioHolding> holdings = new ArrayList<>(investor.getPortfolio());

        double totalCurrentValue = 0;
        double totalInvested = 0;

        List<PortfolioHoldingDetail> details = new ArrayList<>();

        for (PortfolioHolding holding : holdings) {
            double currentPrice;
            try {
                currentPrice = stockMarket.getStockByTicker(holding.getTicker()).getCurrentPrice();
            } catch (StockNotFoundException e) {
                currentPrice = holding.getAverageBuyPrice();
            }

            double currentValue = holding.calculateCurrentValue(currentPrice);
            double profitLoss = holding.calculateProfitLoss(currentPrice);
            double profitLossPercent = holding.calculateProfitLossPercentage(currentPrice);

            totalCurrentValue += currentValue;
            totalInvested += holding.getTotalInvested();

            details.add(new PortfolioHoldingDetail(
                    holding.getTicker(), holding.getCompanyName(),
                    holding.getQuantity(), holding.getAverageBuyPrice(),
                    currentPrice, currentValue, profitLoss, profitLossPercent));
        }

        double overallProfitLoss = totalCurrentValue - totalInvested;
        double overallProfitLossPercent = totalInvested > 0
                ? (overallProfitLoss / totalInvested) * 100 : 0;

        return new PortfolioSummary(investor, details, totalCurrentValue,
                totalInvested, overallProfitLoss, overallProfitLossPercent);
    }

    public List<Transaction> getInvestorTransactionHistory(String investorId)
            throws StockNotFoundException, InvalidInputException {
        Investor investor = getInvestorById(investorId);
        return new ArrayList<>(investor.getTransactionHistory());
    }

    public List<Transaction> getInvestorTransactionsByType(String investorId, TransactionType type)
            throws StockNotFoundException, InvalidInputException {
        List<Transaction> history = getInvestorTransactionHistory(investorId);
        return history.stream()
                .filter(t -> t.getType() == type)
                .collect(Collectors.toList());
    }

    public List<Transaction> getAllTransactions() {
        return Collections.unmodifiableList(allTransactions);
    }

    public List<Investor> getAllInvestors() {
        return Collections.unmodifiableList(registeredInvestors);
    }

    public void depositFunds(String investorId, double amount)
            throws StockNotFoundException, InvalidInputException {
        if (amount <= 0) throw new InvalidInputException("Deposit amount must be positive.");
        Investor investor = getInvestorById(investorId);
        investor.deposit(amount);
    }

    private void validateQuantity(int quantity) throws InvalidInputException {
        if (quantity <= 0) {
            throw new InvalidInputException("Quantity must be a positive integer.");
        }
    }
}

// File: service/PortfolioSummary.java
package service;

import model.Investor;
import java.util.Collections;
import java.util.List;

public class PortfolioSummary {
    private final Investor investor;
    private final List<PortfolioHoldingDetail> holdingDetails;
    private final double totalCurrentValue;
    private final double totalInvested;
    private final double overallProfitLoss;
    private final double overallProfitLossPercentage;

    public PortfolioSummary(Investor investor, List<PortfolioHoldingDetail> holdingDetails,
                            double totalCurrentValue, double totalInvested,
                            double overallProfitLoss, double overallProfitLossPercentage) {
        this.investor = investor;
        this.holdingDetails = holdingDetails;
        this.totalCurrentValue = totalCurrentValue;
        this.totalInvested = totalInvested;
        this.overallProfitLoss = overallProfitLoss;
        this.overallProfitLossPercentage = overallProfitLossPercentage;
    }

    public Investor getInvestor() { return investor; }
    public List<PortfolioHoldingDetail> getHoldingDetails() { return Collections.unmodifiableList(holdingDetails); }
    public double getTotalCurrentValue() { return totalCurrentValue; }
    public double getTotalInvested() { return totalInvested; }
    public double getOverallProfitLoss() { return overallProfitLoss; }
    public double getOverallProfitLossPercentage() { return overallProfitLossPercentage; }
    public double getWalletBalance() { return investor.getWalletBalance(); }
    public double getTotalNetWorth() { return totalCurrentValue + investor.getWalletBalance(); }
}

// File: service/PortfolioHoldingDetail.java
package service;

public class PortfolioHoldingDetail {
    private final String ticker;
    private final String companyName;
    private final int quantity;
    private final double averageBuyPrice;
    private final double currentPrice;
    private final double currentValue;
    private final double profitLoss;
    private final double profitLossPercentage;

    public PortfolioHoldingDetail(String ticker, String companyName, int quantity,
                                   double averageBuyPrice, double currentPrice,
                                   double currentValue, double profitLoss,
                                   double profitLossPercentage) {
        this.ticker = ticker;
        this.companyName = companyName;
        this.quantity = quantity;
        this.averageBuyPrice = averageBuyPrice;
        this.currentPrice = currentPrice;
        this.currentValue = currentValue;
        this.profitLoss = profitLoss;
        this.profitLossPercentage = profitLossPercentage;
    }

    public String getTicker() { return ticker; }
    public String getCompanyName() { return companyName; }
    public int getQuantity() { return quantity; }
    public double getAverageBuyPrice() { return averageBuyPrice; }
    public double getCurrentPrice() { return currentPrice; }
    public double getCurrentValue() { return currentValue; }
    public double getProfitLoss() { return profitLoss; }
    public double getProfitLossPercentage() { return profitLossPercentage; }

    @Override
    public String toString() {
        String plSign = profitLoss >= 0 ? "+" : "";
        return String.format("%-6s | %-30s | Qty: %5d | Avg: $%8.2f | Curr: $%8.2f | Value: $%12.2f | P&L: %s$%.2f (%s%.2f%%)",
                ticker, companyName, quantity, averageBuyPrice,
                currentPrice, currentValue, plSign, profitLoss, plSign, profitLossPercentage);
    }
}

// File: ui/ConsolePrinter.java
package ui;

import model.Stock;
import model.Transaction;
import service.PortfolioHoldingDetail;
import service.PortfolioSummary;

import java.util.List;

public class ConsolePrinter {

    private static final String SEPARATOR_HEAVY = "=".repeat(100);
    private static final String SEPARATOR_LIGHT = "-".repeat(100);

    public void printBanner() {
        System.out.println(SEPARATOR_HEAVY);
        System.out.println("         ███████╗████████╗ ██████╗  ██████╗██╗  ██╗    ████████╗██████╗  █████╗ ██████╗ ███████╗");
        System.out.println("         ██╔════╝╚══██╔══╝██╔═══██╗██╔════╝██║ ██╔╝    ╚══██╔══╝██╔══██╗██╔══██╗██╔══██╗██╔════╝");
        System.out.println("         ███████╗   ██║   ██║   ██║██║     █████╔╝        ██║   ██████╔╝███████║██║  ██║█████╗  ");
        System.out.println("         ╚════██║   ██║   ██║   ██║██║     ██╔═██╗        ██║   ██╔══██╗██╔══██║██║  ██║██╔══╝  ");
        System.out.println("         ███████║   ██║   ╚██████╔╝╚██████╗██║  ██╗       ██║   ██║  ██║██║  ██║██████╔╝███████╗");
        System.out.println("         ╚══════╝   ╚═╝    ╚═════╝  ╚═════╝╚═╝  ╚═╝       ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝╚═════╝ ╚══════╝");
        System.out.println(SEPARATOR_HEAVY);
        System.out.println("                           Welcome to the Stock Trading Platform");
        System.out.println(SEPARATOR_HEAVY);
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println(SEPARATOR_HEAVY);
        System.out.println("                                    MAIN MENU");
        System.out.println(SEPARATOR_HEAVY);
        System.out.println("  [1]  View All Listed Stocks");
        System.out.println("  [2]  Search Stocks");
        System.out.println("  [3]  Register New Investor");
        System.out.println("  [4]  Deposit Funds");
        System.out.println("  [5]  Buy Stock");
        System.out.println("  [6]  Sell Stock");
        System.out.println("  [7]  View Portfolio");
        System.out.println("  [8]  View Transaction History");
        System.out.println("  [9]  Update Stock Price (Admin)");
        System.out.println("  [10] List New Stock (Admin)");
        System.out.println("  [11] View All Investors");
        System.out.println("  [0]  Exit");
        System.out.println(SEPARATOR_HEAVY);
        System.out.print("  Enter your choice: ");
    }

    public void printAllStocks(List<Stock> stocks) {
        System.out.println();
        System.out.println(SEPARATOR_HEAVY);
        System.out.printf("  %-6s | %-30s | %12s | %10s | %s%n",
                "TICKER", "COMPANY NAME", "PRICE", "SHARES", "SECTOR");
        System.out.println(SEPARATOR_LIGHT);
        if (stocks.isEmpty()) {
            System.out.println("  No stocks currently listed.");
        } else {
            stocks.forEach(s -> System.out.println("  " + s));
        }
        System.out.println(SEPARATOR_HEAVY);
    }

    public void printPortfolioSummary(PortfolioSummary summary) {
        System.out.println();
        System.out.println(SEPARATOR_HEAVY);
        System.out.println("  PORTFOLIO SUMMARY — " + summary.getInvestor().getName()
                + " (" + summary.getInvestor().getInvestorId() + ")");
        System.out.println(SEPARATOR_LIGHT);
        System.out.printf("  Wallet Balance     : $%,.2f%n", summary.getWalletBalance());
        System.out.printf("  Total Invested     : $%,.2f%n", summary.getTotalInvested());
        System.out.printf("  Portfolio Value    : $%,.2f%n", summary.getTotalCurrentValue());
        System.out.printf("  Overall P&L        : %s$%,.2f (%s%.2f%%)%n",
                summary.getOverallProfitLoss() >= 0 ? "+" : "",
                summary.getOverallProfitLoss(),
                summary.getOverallProfitLoss() >= 0 ? "+" : "",
                summary.getOverallProfitLossPercentage());
        System.out.printf("  Total Net Worth    : $%,.2f%n", summary.getTotalNetWorth());
        System.out.println(SEPARATOR_LIGHT);

        if (summary.getHoldingDetails().isEmpty()) {
            System.out.println("  No holdings in portfolio.");
        } else {
            System.out.printf("  %-6s | %-30s | %5s | %10s | %10s | %14s | %s%n",
                    "TICKER", "COMPANY", "QTY", "AVG BUY", "CURR PRICE", "VALUE", "P&L");
            System.out.println(SEPARATOR_LIGHT);
            for (PortfolioHoldingDetail detail : summary.getHoldingDetails()) {
                System.out.println("  " + detail);
            }
        }
        System.out.println(SEPARATOR_HEAVY);
    }

    public void printTransactionHistory(List<Transaction> transactions, String investorName) {
        System.out.println();
        System.out.println(SEPARATOR_HEAVY);
        System.out.println("  TRANSACTION HISTORY — " + investorName);
        System.out.println(SEPARATOR_LIGHT);
        if (transactions.isEmpty()) {
            System.out.println("  No transactions found.");
        } else {
            transactions.forEach(t -> System.out.println("  " + t));
            System.out.println(SEPARATOR_LIGHT);
            System.out.printf("  Total Transactions: %d%n", transactions.size());
        }
        System.out.println(SEPARATOR_HEAVY);
    }

    public void printSuccess(String message) {
        System.out.println();
        System.out.println("  ✔ SUCCESS: " + message);
    }

    public void printError(String message) {
        System.out.println();
        System.out.println("  ✘ ERROR: " + message);
    }

    public void printInfo(String message) {
        System.out.println();
        System.out.println("  ℹ INFO: " + message);
    }

    public void printSeparator() {
        System.out.println(SEPARATOR_LIGHT);
    }
}

// File: ui/ConsoleInputHandler.java
package ui;

import java.util.Scanner;

public class ConsoleInputHandler {
    private final Scanner scanner;

    public ConsoleInputHandler() {
        this.scanner = new Scanner(System.in);
    }

    public String readString(String prompt) {
        System.out.print("  " + prompt + ": ");
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print("  " + prompt + ": ");
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a valid integer.");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            System.out.print("  " + prompt + ": ");
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a valid number.");
            }
        }
    }

    public void close() {
        scanner.close();
    }
}

// File: app/StockTradingApp.java
package app;

import exception.*;
import model.*;
import service.*;
import ui.*;

import java.util.List;

public class StockTradingApp {
    private final StockMarket stockMarket;
    private final TradingService tradingService;
    private final ConsolePrinter printer;
    private final ConsoleInputHandler inputHandler;

    public StockTradingApp() {
        this.stockMarket = new StockMarket();
        this.tradingService = new TradingService(stockMarket);
        this.printer = new ConsolePrinter();
        this.inputHandler = new ConsoleInputHandler();
        seedSampleInvestors();
    }

    private void seedSampleInvestors() {
        try {
            Investor alice = new Investor("INV001", "Alice Johnson", "alice@example.com", 50000.00);
            Investor bob = new Investor("INV002", "Bob Smith", "bob@example.com", 30000.00);
            tradingService.registerInvestor(alice);
            tradingService.registerInvestor(bob);
        } catch (InvalidInputException e) {
            printer.printError("Failed to seed investors: " + e.getMessage());
        }
    }

    public void run() {
        printer.printBanner();

        boolean running = true;
        while (running) {
            printer.printMainMenu();
            int choice = inputHandler.readInt("");

            try {
                switch (choice) {
                    case 1  -> handleViewAllStocks();
                    case 2  -> handleSearchStocks();
                    case 3  -> handleRegisterInvestor();
                    case 4  -> handleDepositFunds();
                    case 5  -> handleBuyStock();
                    case 6  -> handleSellStock();
                    case 7  -> handleViewPortfolio();
                    case 8  -> handleViewTransactionHistory();
                    case 9  -> handleUpdateStockPrice();
                    case 10 -> handleListNewStock();
                    case 11 -> handleViewAllInvestors();
                    case 0  -> {
                        printer.printInfo("Thank you for using Stock Trading Platform. Goodbye!");
                        running = false;
                    }
                    default -> printer.printError("Invalid choice. Please select a valid option from the menu.");
                }
            } catch (Exception e) {
                printer.printError("An unexpected error occurred: " + e.getMessage());
            }
        }

        inputHandler.close();
    }

    private void handleViewAllStocks() {
        List<Stock> stocks = stockMarket.getAllListedStocks();
        printer.printAllStocks(stocks);
        printer.printInfo("Total stocks listed: " + stocks.size());
    }

    private void handleSearchStocks() {
        String keyword = inputHandler.readString("Enter ticker symbol or company keyword");
        List<Stock> results = stockMarket.searchStocksByName(keyword);
        if (results.isEmpty()) {
            printer.printInfo("No stocks found matching '" + keyword + "'.");
        } else {
            printer.printAllStocks(results);
        }
    }

    private void handleRegisterInvestor() {
        System.out.println();
        System.out.println("  -- Register New Investor --");
        String id = inputHandler.readString("Enter Investor ID");
        String name = inputHandler.readString("Enter Full Name");
        String email = inputHandler.readString("Enter Email Address");
        double balance = inputHandler.readDouble("Enter Initial Wallet Balance ($)");

        try {
            Investor investor = new Investor(id, name, email, balance);
            tradingService.registerInvestor(investor);
            printer.printSuccess("Investor '" + name + "' registered successfully with ID: " + id);
        } catch (IllegalArgumentException | InvalidInputException e) {
            printer.printError(e.getMessage());
        }
    }

    private void handleDepositFunds() {
        System.out.println();
        System.out.println("  -- Deposit Funds --");
        String investorId = inputHandler.readString("Enter Investor ID");
        double amount = inputHandler.readDouble("Enter Deposit Amount ($)");

        try {
            tradingService.depositFunds(investorId, amount);
            printer.printSuccess(String.format("$%.2f deposited successfully to account %s.", amount, investorId));
        } catch (StockNotFoundException | InvalidInputException e) {
            printer.printError(e.getMessage());
        }
    }

    private void handleBuyStock() {
        System.out.println();
        System.out.println("  -- Buy Stock --");
        String investorId = inputHandler.readString("Enter Investor ID");
        String ticker = inputHandler.readString("Enter Ticker Symbol");
        int quantity = inputHandler.readInt("Enter Quantity to Buy");

        try {
            Stock stock = stockMarket.getStockByTicker(ticker);
            double cost = quantity * stock.getCurrentPrice();
            System.out.printf("  Estimated Cost: %d shares × $%.2f = $%.2f%n",
                    quantity, stock.getCurrentPrice(), cost);
            String confirm = inputHandler.readString("Confirm purchase? (yes/no)");
            if (!confirm.equalsIgnoreCase("yes") && !confirm.equalsIgnoreCase("y")) {
                printer.printInfo("Purchase cancelled.");
                return;
            }
            tradingService.buyStock(investorId, ticker, quantity);
            printer.printSuccess(String.format("Successfully purchased %d shares of %s (%s) for $%.2f.",
                    quantity, stock.getCompanyName(), ticker.toUpperCase(), cost));
        } catch (StockNotFoundException | InsufficientFundsException
                 | InsufficientSharesException | InvalidInputException e) {
            printer.printError(e.getMessage());
        }
    }

    private void handleSellStock() {
        System.out.println();
        System.out.println("  -- Sell Stock --");
        String investorId = inputHandler.readString("Enter Investor ID");
        String ticker = inputHandler.readString("Enter Ticker Symbol");
        int quantity = inputHandler.readInt("Enter Quantity to Sell");

        try {
            Stock stock = stockMarket.getStockByTicker(ticker);
            double proceeds = quantity * stock.getCurrentPrice();
            System.out.printf("  Estimated Proceeds: %d shares × $%.2f = $%.2f%n",
                    quantity, stock.getCurrentPrice(), proceeds);
            String confirm = inputHandler.readString("Confirm sale? (yes/no)");
            if (!confirm.equalsIgnoreCase("yes") && !confirm.equalsIgnoreCase("y")) {
                printer.printInfo("Sale cancelled.");
                return;
            }
            tradingService.sellStock(investorId, ticker, quantity);
            printer.printSuccess(String.format("Successfully sold %d shares of %s (%s) for $%.2f.",
                    quantity, stock.getCompanyName(), ticker.toUpperCase(), proceeds));
        } catch (StockNotFoundException | InsufficientSharesException | InvalidInputException e) {
            printer.printError(e.getMessage());
        }
    }

    private void handleViewPortfolio() {
        System.out.println();
        System.out.println("  -- View Portfolio --");
        String investorId = inputHandler.readString("Enter Investor ID");

        try {
            PortfolioSummary summary = tradingService.getPortfolioSummary(investorId);
            printer.printPortfolioSummary(summary);
        } catch (StockNotFoundException | InvalidInputException e) {
            printer.printError(e.getMessage());
        }
    }

    private void handleViewTransactionHistory() {
        System.out.println();
        System.out.println("  -- Transaction History --");
        String investorId = inputHandler.readString("Enter Investor ID");
        System.out.println("  Filter: [1] All  [2] Buy Only  [3] Sell Only");
        int filter = inputHandler.readInt("Enter filter choice");

        try {
            List<Transaction> transactions;
            String investorName;

            try {
                investorName = tradingService.getInvestorById(investorId).getName();
            } catch (StockNotFoundException e) {
                printer.printError("Investor not found: " + investorId);
                return;
            }

            switch (filter) {
                case 2 -> transactions = tradingService.getInvestorTransactionsByType(investorId, TransactionType.BUY);
                case 3 -> transactions = tradingService.getInvestorTransactionsByType(investorId, TransactionType.SELL);
                default -> transactions = tradingService.getInvestorTransactionHistory(investorId);
            }

            printer.printTransactionHistory(transactions, investorName);
        } catch (StockNotFoundException | InvalidInputException e) {
            printer.printError(e.getMessage());
        }
    }

    private void handleUpdateStockPrice() {
        System.out.println();
        System.out.println("  -- Update Stock Price (Admin) --");
        String ticker = inputHandler.readString("Enter Ticker Symbol");
        double newPrice = inputHandler.readDouble("Enter New Price ($)");

        try {
            stockMarket.updateStockPrice(ticker, newPrice);
            printer.printSuccess(String.format("Price of %s updated to $%.2f.", ticker.toUpperCase(), newPrice));
        } catch (StockNotFoundException | InvalidInputException e) {
            printer.printError(e.getMessage());
        }
    }

    private void handleListNewStock() {
        System.out.println();
        System.out.println("  -- List New Stock (Admin) --");
        String ticker = inputHandler.readString("Enter Ticker Symbol (max 5 chars)");
        String companyName = inputHandler.readString("Enter Company Name");
        double price = inputHandler.readDouble("Enter Initial Price ($)");
        int shares = inputHandler.readInt("Enter Number of Shares Available");
        String sector = inputHandler.readString("Enter Sector");

        try {
            Stock newStock = new Stock(ticker, companyName, price, shares, sector);
            stockMarket.listStock(newStock);
            printer.printSuccess("Stock '" + ticker.toUpperCase() + " - " + companyName + "' listed successfully.");
        } catch (IllegalArgumentException | InvalidInputException e) {
            printer.printError(e.getMessage());
        }
    }

    private void handleViewAllInvestors() {
        List<model.Investor> investors = tradingService.getAllInvestors();
        System.out.println();
        System.out.println("  =".repeat(50));
        System.out.println("  REGISTERED INVESTORS");
        System.out.println("  -".repeat(50));
        if (investors.isEmpty()) {
            printer.printInfo("No investors registered.");
        } else {
            investors.forEach(i -> System.out.println("  " + i));
        }
        System.out.println("  =".repeat(50));
    }
}

// File: Main.java
import app.StockTradingApp;

public class Main {
    public static void main(String[] args) {
        StockTradingApp app = new StockTradingApp();
        app.run();
    }
}
