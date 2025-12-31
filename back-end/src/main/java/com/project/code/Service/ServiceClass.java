package com.project.code.Service;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repository.InventoryRepository;
import com.project.code.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceClass {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;


    // 1. validateInventory
    // Returns false if inventory exists for product + store, otherwise true
    public boolean validateInventory(Inventory inventory) {

        Inventory existing = inventoryRepository.findByProductIdAndStoreId(
                inventory.getProduct().getId(),
                inventory.getStore().getId()
        );

        return existing == null;   // true = valid, false = duplicate
    }


    // 2. validateProduct
    // Returns false if a product with the same name exists, otherwise true
    public boolean validateProduct(Product product) {

        Product existing = productRepository.findByName(product.getName());

        return existing == null;   // true = valid, false = duplicate
    }


    // 3. validateProductId
    // Returns false if product does NOT exist, otherwise true
    public boolean validateProductId(long id) {

        Product product = productRepository.findByid(id);

        return product != null;    // true = exists, false = not found
    }


    // 4. getInventoryId
    // Returns the inventory record for product + store
    public Inventory getInventoryId(Inventory inventory) {

        return inventoryRepository.findByProductIdAndStoreId(
                inventory.getProduct().getId(),
                inventory.getStore().getId()
        );
    }
}
