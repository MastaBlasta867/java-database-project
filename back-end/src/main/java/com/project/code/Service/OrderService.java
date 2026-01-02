package com.project.code.Service;

import com.project.code.model.sql.PlaceOrderRequestDTO;
import com.project.code.model.sql.PurchaseProductDTO;
import com.project.code.model.sql.Customer;
import com.project.code.model.sql.Inventory;
import com.project.code.model.sql.OrderDetails;
import com.project.code.model.sql.OrderItem;
import com.project.code.model.sql.Store;

import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.OrderDetailsRepository;
import com.project.code.Repo.OrderItemRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Repo.StoreRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

