package com.project.code.Controller;

import com.project.code.Model.Review;
import com.project.code.Model.Customer;
import com.project.code.Repository.ReviewRepository;
import com.project.code.Repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CustomerRepository customerRepository;


    // ⭐ GET REVIEWS FOR A PRODUCT IN A STORE
    @GetMapping("/{storeId}/{productId}")
    public Map<String, Object> getReviews(
            @PathVariable long storeId,
            @PathVariable long productId) {

        Map<String, Object> response = new HashMap<>();

        // 1️⃣ Fetch all reviews for this store + product
        List<Review> reviews = reviewRepository.findByStoreIdAndProductId(storeId, productId);

        // 2️⃣ Build a clean list of review objects with comment, rating, and customerName
        List<Map<String, Object>> formattedReviews = new ArrayList<>();

        for (Review review : reviews) {
            Map<String, Object> reviewData = new HashMap<>();

            reviewData.put("comment", review.getComment());
            reviewData.put("rating", review.getRating());

            // Fetch customer name
            Customer customer = customerRepository.findByid(review.getCustomerId());

            if (customer != null) {
                reviewData.put("customerName", customer.getName());
            } else {
                reviewData.put("customerName", "Unknown");
            }

            formattedReviews.add(reviewData);
        }

        // 3️⃣ Return response
        response.put("reviews", formattedReviews);
        return response;
    }
}
