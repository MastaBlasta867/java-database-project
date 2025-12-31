package com.example.repository;

import com.example.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByStoreIdAndProductId(Long storeId, Long productId);

}
