package com.onlinemanagementgrocery.grocerymanagement.service;

import com.onlinemanagementgrocery.grocerymanagement.exception.ResourceNotFoundException;
import com.onlinemanagementgrocery.grocerymanagement.model.Customer;
import com.onlinemanagementgrocery.grocerymanagement.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void getAllCustomers_returnsAllCustomers() {
        Customer customer = Customer.builder()
                .id("1")
                .name("John Doe")
                .email("john@example.com")
                .address("123 Main St")
                .phone("555-0100")
                .build();

        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<Customer> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
    }

    @Test
    void getCustomerById_whenFound_returnsCustomer() {
        Customer customer = Customer.builder().id("1").name("John Doe").build();
        when(customerRepository.findById("1")).thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerById("1");

        assertEquals("1", result.getId());
    }

    @Test
    void getCustomerById_whenNotFound_throwsException() {
        when(customerRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById("99"));
    }

    @Test
    void createCustomer_savesAndReturnsCustomer() {
        Customer customer = Customer.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .address("456 Oak Ave")
                .phone("555-0200")
                .build();

        when(customerRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            saved.setId("1");
            return saved;
        });

        Customer result = customerService.createCustomer(customer);

        assertNotNull(result.getId());
        assertEquals("Jane Doe", result.getName());
        verify(customerRepository).save(customer);
    }

    @Test
    void createCustomer_whenEmailExists_throwsException() {
        Customer customer = Customer.builder().email("existing@example.com").build();
        when(customerRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(customer));
    }

    @Test
    void deleteCustomer_whenNotFound_throwsException() {
        when(customerRepository.existsById("99")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> customerService.deleteCustomer("99"));
    }
}
