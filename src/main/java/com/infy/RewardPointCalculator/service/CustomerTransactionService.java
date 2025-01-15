package com.infy.RewardPointCalculator.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.RewardPointCalculator.BusinessLogic.RewardPointCalculator;
import com.infy.RewardPointCalculator.dto.CustomerTransactionDTO;
import com.infy.RewardPointCalculator.model.Customer;
import com.infy.RewardPointCalculator.model.CustomerTransaction;
import com.infy.RewardPointCalculator.model.RewardPoints;
import com.infy.RewardPointCalculator.repository.CustomerRepository;
import com.infy.RewardPointCalculator.repository.CustomerTransactionRepository;
import com.infy.RewardPointCalculator.repository.RewardPointsRepository;

import jakarta.validation.Valid;

@Service
public class CustomerTransactionService {

    @Autowired
    private CustomerTransactionRepository transactionRepository;

    @Autowired
    private RewardPointsRepository rewardPointsRepository;

    @Autowired
    private CustomerRepository customerRepository;  // Add repository for Customer

    // Get all transactions for a specific customer
    public List<CustomerTransaction> getTransactions(Long customerId) {
        return transactionRepository.findByCustomerId(customerId);
    }

    // Add a new transaction and calculate reward points
    public CustomerTransaction addTransaction(Long customerId, CustomerTransactionDTO transactionDTO) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerTransaction transaction = new CustomerTransaction();
        transaction.setCustomer(customer);  // Set the existing customer from the database
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setSpentDetails(transactionDTO.getSpentDetails());
        transaction.setDate(transactionDTO.getTransactionDate());
        transactionRepository.save(transaction);

        // Extract month and year from the transaction's date
        int month = transaction.getDate().getMonthValue();
        int year = transaction.getDate().getYear();
        // Calculate and save reward points
        int points = RewardPointCalculator.calculatePoints(transactionDTO.getAmount());
        List<RewardPoints> rewardPointsList = rewardPointsRepository.findByCustomerAndMonthAndYear(customer, month, year);
        RewardPoints rewardPoints;
        if (rewardPointsList.isEmpty()) {
            rewardPoints = new RewardPoints();
            rewardPoints.setCustomer(customer);
            rewardPoints.setMonth(month);
            rewardPoints.setYear(year);
            rewardPoints.setPoints(0);  // Initialize with zero points if new entry
        }else {
            rewardPoints = rewardPointsList.get(0);  // Get the first entry from the list
        }

        rewardPoints.setPoints(rewardPoints.getPoints() + points);
        rewardPointsRepository.save(rewardPoints);

        updateRewardPoints(customerId, transaction.getDate().getMonthValue(), transaction.getDate().getYear());

        
        return transaction;
    }

    // Edit an existing transaction
    public CustomerTransaction editTransaction(Long customerId, Long transactionId, @Valid CustomerTransactionDTO transactionDTO) {
        CustomerTransaction existingTransaction = transactionRepository.findByCustomerIdAndId(customerId,transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        existingTransaction.setAmount(transactionDTO.getAmount());
        existingTransaction.setSpentDetails(transactionDTO.getSpentDetails());
        existingTransaction.setDate(transactionDTO.getTransactionDate());
        transactionRepository.save(existingTransaction);
        
        int month = existingTransaction.getDate().getMonthValue();
        int year = existingTransaction.getDate().getYear();
        // Recalculate and update reward points based on the new transaction data
        int points = RewardPointCalculator.calculatePoints(transactionDTO.getAmount());
        List<RewardPoints> rewardPointsList = rewardPointsRepository.findByCustomerAndMonthAndYear(existingTransaction.getCustomer(), month, year);

        // If no existing RewardPoints record is found, create a new one
        RewardPoints rewardPoints;
        if (rewardPointsList.isEmpty()) {
            rewardPoints = new RewardPoints();
            rewardPoints.setCustomer(existingTransaction.getCustomer());
            rewardPoints.setMonth(month);
            rewardPoints.setYear(year);
            rewardPoints.setPoints(0);  // Initialize with zero points if new entry
        } else {
            // If an existing RewardPoints record is found, use the first one
            rewardPoints = rewardPointsList.get(0);
        }

        rewardPoints.setPoints(rewardPoints.getPoints() + points);
        rewardPointsRepository.save(rewardPoints);
        
        updateRewardPoints(customerId, month, year);

        return existingTransaction;
    }

    // Delete a transaction and adjust reward points
    public void deleteTransaction(Long customerId, Long transactionId) {
    	Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        CustomerTransaction transaction = transactionRepository.findByCustomerIdAndId(customerId,transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Adjust reward points based on the transaction being deleted
        int points = RewardPointCalculator.calculatePoints(transaction.getAmount());
        List<RewardPoints> rewardPointsList = rewardPointsRepository.findByCustomerAndMonthAndYear(customer, transaction.getDate().getMonthValue(), transaction.getDate().getYear());

        if (!rewardPointsList.isEmpty()) {
            RewardPoints rewardPoints = rewardPointsList.get(0);
            rewardPoints.setPoints(rewardPoints.getPoints() - points);
            rewardPointsRepository.save(rewardPoints);
        }
        transactionRepository.delete(transaction);
        
        updateRewardPoints(customerId, transaction.getDate().getMonthValue(), transaction.getDate().getYear());

    }
    
    public void updateRewardPoints(Long customerId, Integer month, Integer year) {
        // Fetch the customer from the database
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Retrieve all transactions for the customer within the given month and year
        List<CustomerTransaction> transactions = transactionRepository.findByCustomerIdAndDateBetween(
            customerId, 
            LocalDate.of(year, month, 1), 
            LocalDate.of(year, month, 1).withDayOfMonth(LocalDate.of(year, month, 1).lengthOfMonth())
        );

        // Calculate total points
        int totalPoints = 0;
        for (CustomerTransaction transaction : transactions) {
            totalPoints += RewardPointCalculator.calculatePoints(transaction.getAmount());
        }

        // Retrieve the RewardPoints record for the given customer, month, and year
        List<RewardPoints> rewardPointsList = rewardPointsRepository.findByCustomerAndMonthAndYear(customer, month, year);
        RewardPoints rewardPoints;

        if (rewardPointsList.isEmpty()) {
            // If no reward points record exists, create a new one
            rewardPoints = new RewardPoints();
            rewardPoints.setCustomer(customer);  // Set the correct Customer object
            rewardPoints.setMonth(month);
            rewardPoints.setYear(year);
            rewardPoints.setPoints(totalPoints);  // Set the calculated total points
            rewardPointsRepository.save(rewardPoints);
        } else {
            // If a record exists, update the points
            rewardPoints = rewardPointsList.get(0); // Get the first matching record
            rewardPoints.setPoints(totalPoints);  // Update the points
            rewardPointsRepository.save(rewardPoints);
        }

    }


}
