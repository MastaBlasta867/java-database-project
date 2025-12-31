package com.example.repository;

import com.example.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    Store findByid(Long id);

    @Query("SELECT s FROM Store s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    List<Store> findBySubName(@Param("pname") String pname);

}
