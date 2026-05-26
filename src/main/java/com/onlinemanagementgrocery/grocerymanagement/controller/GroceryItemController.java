package com.onlinemanagementgrocery.grocerymanagement.controller;

import com.onlinemanagementgrocery.grocerymanagement.model.GroceryItem;
import com.onlinemanagementgrocery.grocerymanagement.service.GroceryItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grocery-items")
@RequiredArgsConstructor
public class GroceryItemController {

    private final GroceryItemService groceryItemService;

    @GetMapping
    public ResponseEntity<List<GroceryItem>> getAllGroceryItems(
            @RequestParam(required = false) String category) {
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(groceryItemService.getGroceryItemsByCategory(category));
        }
        return ResponseEntity.ok(groceryItemService.getAllGroceryItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroceryItem> getGroceryItemById(@PathVariable String id) {
        return ResponseEntity.ok(groceryItemService.getGroceryItemById(id));
    }

    @PostMapping
    public ResponseEntity<GroceryItem> createGroceryItem(@Valid @RequestBody GroceryItem groceryItem) {
        GroceryItem createdItem = groceryItemService.createGroceryItem(groceryItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroceryItem> updateGroceryItem(
            @PathVariable String id,
            @Valid @RequestBody GroceryItem groceryItem) {
        return ResponseEntity.ok(groceryItemService.updateGroceryItem(id, groceryItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroceryItem(@PathVariable String id) {
        groceryItemService.deleteGroceryItem(id);
        return ResponseEntity.noContent().build();
    }
}
