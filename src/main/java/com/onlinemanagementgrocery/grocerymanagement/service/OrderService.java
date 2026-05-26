package com.onlinemanagementgrocery.grocerymanagement.service;

import com.onlinemanagementgrocery.grocerymanagement.dto.OrderRequest;
import com.onlinemanagementgrocery.grocerymanagement.exception.ResourceNotFoundException;
import com.onlinemanagementgrocery.grocerymanagement.model.GroceryItem;
import com.onlinemanagementgrocery.grocerymanagement.model.Order;
import com.onlinemanagementgrocery.grocerymanagement.model.OrderItem;
import com.onlinemanagementgrocery.grocerymanagement.repository.CustomerRepository;
import com.onlinemanagementgrocery.grocerymanagement.repository.GroceryItemRepository;
import com.onlinemanagementgrocery.grocerymanagement.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final GroceryItemRepository groceryItemRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    public List<Order> getOrdersByCustomerId(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        return orderRepository.findByCustomerId(customerId);
    }

    public Order createOrder(OrderRequest orderRequest) {
        if (!customerRepository.existsById(orderRequest.getCustomerId())) {
            throw new ResourceNotFoundException("Customer not found with id: " + orderRequest.getCustomerId());
        }

        List<OrderItem> resolvedItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItem item : orderRequest.getItems()) {
            GroceryItem groceryItem = groceryItemRepository.findById(item.getGroceryItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Grocery item not found with id: " + item.getGroceryItemId()));

            OrderItem orderItem = OrderItem.builder()
                    .groceryItemId(groceryItem.getId())
                    .quantity(item.getQuantity())
                    .unitPrice(groceryItem.getPrice())
                    .build();

            resolvedItems.add(orderItem);
            totalPrice = totalPrice.add(
                    groceryItem.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        Order order = Order.builder()
                .customerId(orderRequest.getCustomerId())
                .items(resolvedItems)
                .orderDate(orderRequest.getOrderDate() != null ? orderRequest.getOrderDate() : LocalDateTime.now())
                .totalPrice(totalPrice)
                .build();

        return orderRepository.save(order);
    }

    public Order updateOrder(String id, OrderRequest orderRequest) {
        Order existingOrder = getOrderById(id);

        if (!customerRepository.existsById(orderRequest.getCustomerId())) {
            throw new ResourceNotFoundException("Customer not found with id: " + orderRequest.getCustomerId());
        }

        List<OrderItem> resolvedItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItem item : orderRequest.getItems()) {
            GroceryItem groceryItem = groceryItemRepository.findById(item.getGroceryItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Grocery item not found with id: " + item.getGroceryItemId()));

            OrderItem orderItem = OrderItem.builder()
                    .groceryItemId(groceryItem.getId())
                    .quantity(item.getQuantity())
                    .unitPrice(groceryItem.getPrice())
                    .build();

            resolvedItems.add(orderItem);
            totalPrice = totalPrice.add(
                    groceryItem.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        existingOrder.setCustomerId(orderRequest.getCustomerId());
        existingOrder.setItems(resolvedItems);
        existingOrder.setOrderDate(
                orderRequest.getOrderDate() != null ? orderRequest.getOrderDate() : existingOrder.getOrderDate());
        existingOrder.setTotalPrice(totalPrice);

        return orderRepository.save(existingOrder);
    }

    public void deleteOrder(String id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }
}
