package com.infy.RewardPointCalculator.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.RewardPointCalculator.model.Customer;
import com.infy.RewardPointCalculator.model.RewardPoints;
import com.infy.RewardPointCalculator.service.RewardPointsService;

@RestController
@RequestMapping("/api/customers/{customerId}/reward-points")
public class RewardPointController {

    @Autowired
    private RewardPointsService rewardPointsService;

    // Get reward points for a specific customer, month, and year
    @GetMapping("/{month}/{year}")
    public ResponseEntity<RewardPoints> getRewardPoints(
            @PathVariable Long customerId, @PathVariable Integer month, @PathVariable Integer year) {
        RewardPoints rewardPoints = rewardPointsService.getRewardPoints(customerId, month, year);
        return ResponseEntity.ok(rewardPoints);
    }

    // Get all reward points for a specific customer
    @GetMapping
    public ResponseEntity<List<RewardPoints>> getAllRewardPoints(@PathVariable Long customerId) {
        List<RewardPoints> allPoints = rewardPointsService.getAllRewardPoints(customerId);
        return ResponseEntity.ok(allPoints);
    }
}

