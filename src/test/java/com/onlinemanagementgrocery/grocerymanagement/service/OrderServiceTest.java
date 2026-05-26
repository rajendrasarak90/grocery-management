package com.onlinemanagementgrocery.grocerymanagement.service;

import com.onlinemanagementgrocery.grocerymanagement.dto.OrderRequest;
import com.onlinemanagementgrocery.grocerymanagement.exception.ResourceNotFoundException;
import com.onlinemanagementgrocery.grocerymanagement.model.GroceryItem;
import com.onlinemanagementgrocery.grocerymanagement.model.Order;
import com.onlinemanagementgrocery.grocerymanagement.model.OrderItem;
import com.onlinemanagementgrocery.grocerymanagement.repository.CustomerRepository;
import com.onlinemanagementgrocery.grocerymanagement.repository.GroceryItemRepository;
import com.onlinemanagementgrocery.grocerymanagement.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private GroceryItemRepository groceryItemRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_calculatesTotalPriceAndSavesOrder() {
        OrderRequest request = OrderRequest.builder()
                .customerId("cust1")
                .items(List.of(
                        OrderItem.builder().groceryItemId("item1").quantity(2).build(),
                        OrderItem.builder().groceryItemId("item2").quantity(1).build()))
                .build();

        GroceryItem item1 = GroceryItem.builder()
                .id("item1")
                .name("Milk")
                .price(new BigDecimal("3.50"))
                .build();
        GroceryItem item2 = GroceryItem.builder()
                .id("item2")
                .name("Bread")
                .price(new BigDecimal("2.00"))
                .build();

        when(customerRepository.existsById("cust1")).thenReturn(true);
        when(groceryItemRepository.findById("item1")).thenReturn(Optional.of(item1));
        when(groceryItemRepository.findById("item2")).thenReturn(Optional.of(item2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId("order1");
            return order;
        });

        Order result = orderService.createOrder(request);

        assertEquals("order1", result.getId());
        assertEquals("cust1", result.getCustomerId());
        assertEquals(2, result.getItems().size());
        assertEquals(new BigDecimal("9.00"), result.getTotalPrice());
    }

    @Test
    void createOrder_whenCustomerNotFound_throwsException() {
        OrderRequest request = OrderRequest.builder()
                .customerId("missing")
                .items(List.of(OrderItem.builder().groceryItemId("item1").quantity(1).build()))
                .build();

        when(customerRepository.existsById("missing")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(request));
    }

    @Test
    void getOrderById_whenNotFound_throwsException() {
        when(orderRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById("99"));
    }
}
