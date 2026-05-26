package com.onlinemanagementgrocery.grocerymanagement.service;

import com.onlinemanagementgrocery.grocerymanagement.exception.ResourceNotFoundException;
import com.onlinemanagementgrocery.grocerymanagement.model.GroceryItem;
import com.onlinemanagementgrocery.grocerymanagement.repository.GroceryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroceryItemService {

    private final GroceryItemRepository groceryItemRepository;

    public List<GroceryItem> getAllGroceryItems() {
        return groceryItemRepository.findAll();
    }

    public GroceryItem getGroceryItemById(String id) {
        return groceryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grocery item not found with id: " + id));
    }

    public List<GroceryItem> getGroceryItemsByCategory(String category) {
        return groceryItemRepository.findByCategory(category);
    }

    public GroceryItem createGroceryItem(GroceryItem groceryItem) {
        return groceryItemRepository.save(groceryItem);
    }

    public GroceryItem updateGroceryItem(String id, GroceryItem groceryItemDetails) {
        GroceryItem existingItem = getGroceryItemById(id);

        existingItem.setName(groceryItemDetails.getName());
        existingItem.setCategory(groceryItemDetails.getCategory());
        existingItem.setPrice(groceryItemDetails.getPrice());
        existingItem.setQuantity(groceryItemDetails.getQuantity());

        return groceryItemRepository.save(existingItem);
    }

    public void deleteGroceryItem(String id) {
        if (!groceryItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Grocery item not found with id: " + id);
        }
        groceryItemRepository.deleteById(id);
    }
}
