package com.project.code.Controller;

import com.project.code.Model.Store;
import com.project.code.Repository.StoreRepository;
import com.project.code.Service.OrderService;
import com.project.code.dto.PlaceOrderRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderService orderService;


    // ⭐ ADD STORE
    @PostMapping
    public Map<String, String> addStore(@RequestBody Store store) {
        Map<String, String> response = new HashMap<>();

        Store savedStore = storeRepository.save(store);

        response.put("message", "Store created successfully with ID: " + savedStore.getId());
        return response;
    }


    // ⭐ VALIDATE STORE
    @GetMapping("validate/{storeId}")
    public boolean validateStore(@PathVariable long storeId) {
        return storeRepository.findById(storeId).isPresent();
    }


    // ⭐ PLACE ORDER
    @PostMapping("/placeOrder")
    public Map<String, String> placeOrder(@RequestBody PlaceOrderRequestDTO request) {
        Map<String, String> response = new HashMap<>();

        try {
            orderService.saveOrder(request);
            response.put("message", "Order placed successfully");
        } catch (Exception e) {
            response.put("Error", e.getMessage());
        }

        return response;
    }
}
