package com.onlinemanagementgrocery.grocerymanagement.service;

import com.onlinemanagementgrocery.grocerymanagement.exception.ResourceNotFoundException;
import com.onlinemanagementgrocery.grocerymanagement.model.Customer;
import com.onlinemanagementgrocery.grocerymanagement.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    public Customer createCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Customer with email already exists: " + customer.getEmail());
        }
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(String id, Customer customerDetails) {
        Customer existingCustomer = getCustomerById(id);

        if (!existingCustomer.getEmail().equals(customerDetails.getEmail())
                && customerRepository.existsByEmail(customerDetails.getEmail())) {
            throw new IllegalArgumentException("Customer with email already exists: " + customerDetails.getEmail());
        }

        existingCustomer.setName(customerDetails.getName());
        existingCustomer.setEmail(customerDetails.getEmail());
        existingCustomer.setAddress(customerDetails.getAddress());
        existingCustomer.setPhone(customerDetails.getPhone());

        return customerRepository.save(existingCustomer);
    }

    public void deleteCustomer(String id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
}
