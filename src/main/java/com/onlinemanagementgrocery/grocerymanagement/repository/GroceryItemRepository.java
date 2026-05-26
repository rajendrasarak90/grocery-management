package com.onlinemanagementgrocery.grocerymanagement.repository;

import com.onlinemanagementgrocery.grocerymanagement.model.GroceryItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroceryItemRepository extends MongoRepository<GroceryItem, String> {

    List<GroceryItem> findByCategory(String category);
}
