package com.project.code.Controller;

import com.project.code.model.sql.Inventory;
import com.project.code.model.sql.Product;

import com.project.code.repository.InventoryRepository;
import com.project.code.repository.ProductRepository;

import com.project.code.Service.ServiceClass;
import com.project.code.dto.CombinedRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ServiceClass serviceClass;


    // ⭐ UPDATE INVENTORY
    @PutMapping
    public Map<String, String> updateInventory(@RequestBody CombinedRequest request) {
        Map<String, String> response = new HashMap<>();

        try {
            Product product = request.getProduct();
            Inventory inventory = request.getInventory();

            // Validate product ID
            if (!serviceClass.validateProductId(product.getId())) {
                response.put("message", "Product ID does not exist");
                return response;
            }

            // Check if inventory exists
            Inventory existingInventory = inventoryRepository.findByProductIdAndStoreId(
                    product.getId(),
                    inventory.getStore().getId()
            );

            if (existingInventory != null) {
                // Update product
                productRepository.save(product);

                // Update inventory
                existingInventory.setStockLevel(inventory.getStockLevel());
                existingInventory.setPrice(inventory.getPrice());
                inventoryRepository.save(existingInventory);

                response.put("message", "Successfully updated product");
            } else {
                response.put("message", "No data available");
            }

        } catch (DataIntegrityViolationException e) {
            response.put("message", "Data integrity error");
        } catch (Exception e) {
            response.put("message", "Error updating inventory");
        }

        return response;
    }


    // ⭐ SAVE INVENTORY
    @PostMapping
    public Map<String, String> saveInventory(@RequestBody Inventory inventory) {
        Map<String, String> response = new HashMap<>();

        try {
            // Validate inventory
            if (!serviceClass.validateInventory(inventory)) {
                response.put("message", "Data already present");
                return response;
            }

            inventoryRepository.save(inventory);
            response.put("message", "Data saved successfully");

        } catch (DataIntegrityViolationException e) {
            response.put("message", "Data integrity error");
        } catch (Exception e) {
            response.put("message", "Error saving inventory");
        }

        return response;
    }


    // ⭐ GET ALL PRODUCTS FOR STORE
    @GetMapping("/{storeid}")
    public Map<String, Object> getAllProducts(@PathVariable long storeid) {
        Map<String, Object> response = new HashMap<>();

        List<Product> products = productRepository.findProductsByStoreId(storeid);
        response.put("products", products);

        return response;
    }


    // ⭐ FILTER PRODUCTS BY CATEGORY + NAME
    @GetMapping("filter/{category}/{name}/{storeid}")
    public Map<String, Object> getProductName(
            @PathVariable String category,
            @PathVariable String name,
            @PathVariable long storeid) {

        Map<String, Object> response = new HashMap<>();
        List<Product> products;

        if (category.equals("null")) {
            // Filter by name only
            products = productRepository.findByNameLike(storeid, name);
        } else if (name.equals("null")) {
            // Filter by category only
            products = productRepository.findByCategoryAndStoreId(category, storeid);
        } else {
            // Filter by both
            products = productRepository.findByNameAndCategory(name, category, storeid);
        }

        response.put("product", products);
        return response;
    }


    // ⭐ SEARCH PRODUCT BY NAME
    @GetMapping("search/{name}/{storeId}")
    public Map<String, Object> searchProduct(
            @PathVariable String name,
            @PathVariable long storeId) {

        Map<String, Object> response = new HashMap<>();

        List<Product> products = productRepository.findByNameLike(storeId, name);
        response.put("product", products);

        return response;
    }


    // ⭐ REMOVE PRODUCT
    @DeleteMapping("/{id}")
    public Map<String, String> removeProduct(@PathVariable long id) {
        Map<String, String> response = new HashMap<>();

        if (!serviceClass.validateProductId(id)) {
            response.put("message", "Product not present in database");
            return response;
        }

        // Delete inventory first
        inventoryRepository.deleteByProductId(id);

        // Delete product
        productRepository.deleteById(id);

        response.put("message", "Product deleted successfully");
        return response;
    }


    // ⭐ VALIDATE QUANTITY
    @GetMapping("validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity(
            @PathVariable int quantity,
            @PathVariable long storeId,
            @PathVariable long productId) {

        Inventory inventory = inventoryRepository.findByProductIdAndStoreId(productId, storeId);

        if (inventory == null) return false;

        return inventory.getStockLevel() >= quantity;
    }
}
