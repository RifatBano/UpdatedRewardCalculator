package com.infy.RewardPointCalculator.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.RewardPointCalculator.model.Customer;
import com.infy.RewardPointCalculator.model.RewardPoints;
import com.infy.RewardPointCalculator.repository.CustomerRepository;
import com.infy.RewardPointCalculator.repository.RewardPointsRepository;

@Service
public class RewardPointsService {

    @Autowired
    private RewardPointsRepository rewardPointsRepository;
    @Autowired
    private CustomerRepository customerRepository;
   
    public RewardPoints getRewardPoints(Long customerId, Integer month, Integer year) {
    	
    	Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    	
        List<RewardPoints> rewardPointsList = rewardPointsRepository.findByCustomerAndMonthAndYear(customer, month, year);

        // If no reward points are found, return a new RewardPoints object with zero points
        if (rewardPointsList.isEmpty()) {
            return new RewardPoints(null, customer, 0, month, year);  // Return a default with zero points
        }

        // Aggregate the points by summing them up
        int totalPoints = rewardPointsList.stream()
            .mapToInt(RewardPoints::getPoints) // Sum up the points
            .sum();


        // Create a new RewardPoints object with the aggregated points
        RewardPoints aggregatedRewardPoints = new RewardPoints();
        aggregatedRewardPoints.setCustomer(customer);  // Set the customer properly
        aggregatedRewardPoints.setPoints(totalPoints);
        aggregatedRewardPoints.setMonth(month);
        aggregatedRewardPoints.setYear(year);

        return aggregatedRewardPoints;
    }

    public List<RewardPoints> getAllRewardPoints(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        return rewardPointsRepository.findByCustomer(customer);
    }
    
    

}
