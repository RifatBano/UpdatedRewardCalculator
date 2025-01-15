package com.infy.RewardPointCalculator.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.RewardPointCalculator.dto.CustomerDTO;
import com.infy.RewardPointCalculator.dto.LoginDTO;
import com.infy.RewardPointCalculator.model.Customer;
import com.infy.RewardPointCalculator.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    private Set<String> blacklistedTokens = new HashSet<>();

    public Customer register(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPassword(customerDTO.getPassword());  // In production, use BCrypt
        return customerRepository.save(customer);
    }

    public String login(LoginDTO loginDTO) {
        // Implement login logic (validate credentials)
        return "JWT_Token";  // Simplified for this example
    }

    public void logout(String token) {
    	 blacklistedTokens.add(token);    }
}
