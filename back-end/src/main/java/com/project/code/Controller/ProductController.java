package com.project.code.Controller;

import com.project.code.Model.Product;
import com.project.code.Repository.InventoryRepository;
import com.project.code.Repository.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ServiceClass serviceClass;

    @Autowired
    private InventoryRepository inventoryRepository;


    // ⭐ ADD PRODUCT
    @PostMapping
    public Map<String, String> addProduct(@RequestBody Product product) {
        Map<String, String> response = new HashMap<>();

        try {
            // Validate product name
            if (!serviceClass.validateProduct(product)) {
                response.put("message", "Product already exists");
                return response;
            }

            productRepository.save(product);
            response.put("message", "Product added successfully");

        } catch (DataIntegrityViolationException e) {
            response.put("message", "Data integrity violation (duplicate SKU or invalid data)");
        } catch (Exception e) {
            response.put("message", "Error adding product");
        }

        return response;
    }


    // ⭐ GET PRODUCT BY ID
    @GetMapping("/product/{id}")
    public Map<String, Object> getProductById(@PathVariable long id) {
        Map<String, Object> response = new HashMap<>();

        Product product = productRepository.findById(id).orElse(null);
        response.put("products", product);

        return response;
    }


    // ⭐ UPDATE PRODUCT
    @PutMapping
    public Map<String, String> updateProduct(@RequestBody Product product) {
        Map<String, String> response = new HashMap<>();

        try {
            productRepository.save(product);
            response.put("message", "Product updated successfully");

        } catch (Exception e) {
            response.put("message", "Error updating product");
        }

        return response;
    }


    // ⭐ FILTER BY NAME + CATEGORY
    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterByCategoryProduct(
            @PathVariable String name,
            @PathVariable String category) {

        Map<String, Object> response = new HashMap<>();
        List<Product> products;

        if (name.equals("null")) {
            // Filter by category only
            products = productRepository.findByCategory(category);
        } else if (category.equals("null")) {
            // Filter by name only
            products = productRepository.findProductBySubName(name);
        } else {
            // Filter by both
            products = productRepository.findProductBySubNameAndCategory(name, category);
        }

        response.put("products", products);
        return response;
    }


    // ⭐ LIST ALL PRODUCTS
    @GetMapping
    public Map<String, Object> listProduct() {
        Map<String, Object> response = new HashMap<>();

        List<Product> products = productRepository.findAll();
        response.put("products", products);

        return response;
    }


    // ⭐ FILTER BY CATEGORY + STORE ID
    @GetMapping("filter/{category}/{storeid}")
    public Map<String, Object> getProductByCategoryAndStoreId(
            @PathVariable String category,
            @PathVariable long storeid) {

        Map<String, Object> response = new HashMap<>();

        List<Product> products = productRepository.findProductByCategory(category, storeid);
        response.put("product", products);

        return response;
    }


    // ⭐ DELETE PRODUCT
    @DeleteMapping("/{id}")
    public Map<String, String> deleteProduct(@PathVariable long id) {
        Map<String, String> response = new HashMap<>();

        // Validate product exists
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


    // ⭐ SEARCH PRODUCT BY NAME
    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct(@PathVariable String name) {
        Map<String, Object> response = new HashMap<>();

        List<Product> products = productRepository.findProductBySubName(name);
        response.put("products", products);

        return response;
    }
}
