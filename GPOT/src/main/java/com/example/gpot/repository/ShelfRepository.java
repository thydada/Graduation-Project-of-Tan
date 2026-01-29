package com.example.gpot.repository;

import com.example.gpot.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {

    List<Shelf> findByWarehouseId(Long warehouseId);

    List<Shelf> findByShelfType(String shelfType);

    List<Shelf> findByStatus(Integer status);
}
