package com.onlinemanagementgrocery.grocerymanagement.service;

import com.onlinemanagementgrocery.grocerymanagement.exception.ResourceNotFoundException;
import com.onlinemanagementgrocery.grocerymanagement.model.GroceryItem;
import com.onlinemanagementgrocery.grocerymanagement.repository.GroceryItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroceryItemServiceTest {

    @Mock
    private GroceryItemRepository groceryItemRepository;

    @InjectMocks
    private GroceryItemService groceryItemService;

    @Test
    void getGroceryItemById_whenFound_returnsItem() {
        GroceryItem item = GroceryItem.builder()
                .id("1")
                .name("Apple")
                .category("Fruits")
                .price(new BigDecimal("1.50"))
                .quantity(100)
                .build();

        when(groceryItemRepository.findById("1")).thenReturn(Optional.of(item));

        GroceryItem result = groceryItemService.getGroceryItemById("1");

        assertEquals("Apple", result.getName());
        assertEquals("Fruits", result.getCategory());
    }

    @Test
    void getGroceryItemById_whenNotFound_throwsException() {
        when(groceryItemRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> groceryItemService.getGroceryItemById("99"));
    }

    @Test
    void updateGroceryItem_updatesFieldsAndSaves() {
        GroceryItem existing = GroceryItem.builder()
                .id("1")
                .name("Apple")
                .category("Fruits")
                .price(new BigDecimal("1.50"))
                .quantity(100)
                .build();

        GroceryItem updated = GroceryItem.builder()
                .name("Green Apple")
                .category("Fruits")
                .price(new BigDecimal("2.00"))
                .quantity(80)
                .build();

        when(groceryItemRepository.findById("1")).thenReturn(Optional.of(existing));
        when(groceryItemRepository.save(existing)).thenReturn(existing);

        GroceryItem result = groceryItemService.updateGroceryItem("1", updated);

        assertEquals("Green Apple", result.getName());
        assertEquals(new BigDecimal("2.00"), result.getPrice());
        assertEquals(80, result.getQuantity());
    }
}
