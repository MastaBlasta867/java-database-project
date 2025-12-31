package com.example.service;

import com.example.dto.PlaceOrderRequestDTO;
import com.example.dto.PurchaseProductDTO;
import com.example.model.Customer;
import com.example.model.Inventory;
import com.example.model.OrderDetails;
import com.example.model.OrderItem;
import com.example.model.Store;
import com.example.repository.CustomerRepository;
import com.example.repository.InventoryRepository;
import com.example.repository.OrderDetailsRepository;
import com.example.repository.OrderItemRepository;
import com.example.repository.ProductRepository;
import com.example.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;


    // ⭐ MAIN METHOD: Save Order
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {

        // 1️⃣ Retrieve or create customer
        Customer customer = customerRepository.findByEmail(placeOrderRequest.getEmail());
        if (customer == null) {
            customer = new Customer();
            customer.setName(placeOrderRequest.getCustomerName());
            customer.setEmail(placeOrderRequest.getEmail());
            customerRepository.save(customer);
        }

        // 2️⃣ Retrieve store
        Store store = storeRepository.findById(placeOrderRequest.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        // 3️⃣ Create OrderDetails
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCustomer(customer);
        orderDetails.setStore(store);
        orderDetails.setTotalPrice(placeOrderRequest.getTotalPrice());
        orderDetails.setOrderDate(LocalDateTime.now());
        orderDetailsRepository.save(orderDetails);

        // 4️⃣ Loop through purchased products
        for (PurchaseProductDTO item : placeOrderRequest.getPurchaseProduct()) {

            // Get inventory for this product in this store
            Inventory inventory = inventoryRepository
                    .findByProductIdAndStoreId(item.getProductId(), placeOrderRequest.getStoreId());

            if (inventory == null) {
                throw new RuntimeException("Inventory not found for product ID: " + item.getProductId());
            }

            // Check stock
            if (inventory.getStockLevel() < item.getQuantity()) {
                throw new RuntimeException("Not enough stock for product ID: " + item.getProductId());
            }

            // Reduce stock
            inventory.setStockLevel(inventory.getStockLevel() - item.getQuantity());
            inventoryRepository.save(inventory);

            // Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderDetails(orderDetails);
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getPrice());

            orderItemRepository.save(orderItem);
        }
    }
}

