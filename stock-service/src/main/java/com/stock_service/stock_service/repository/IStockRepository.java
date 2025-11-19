package com.stock_service.stock_service.repository;

import com.stock_service.stock_service.model.StockRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IStockRepository extends JpaRepository<StockRecord, Long> {

    public Optional<StockRecord> findByProductId(Long id);
}
