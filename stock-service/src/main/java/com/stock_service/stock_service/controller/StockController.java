package com.stock_service.stock_service.controller;

import com.stock_service.stock_service.dto.StockRequestDTO;
import com.stock_service.stock_service.dto.StockResponseDTO;
import com.stock_service.stock_service.service.IStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final IStockService stockService;

    @GetMapping
    public ResponseEntity<List<StockResponseDTO>> getStockAll() {
        return ResponseEntity.ok(stockService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockResponseDTO> getStockById(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.findById(id));
    }

    @PostMapping
    //@PreAuthorize("permitAll()")
    public ResponseEntity<StockResponseDTO> createStock(@RequestBody StockRequestDTO stockRequestDTO) {
        return ResponseEntity.ok(stockService.save(stockRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStockById(@PathVariable Long id) {
        stockService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockResponseDTO> updateStockById(@PathVariable Long id, @RequestBody StockRequestDTO stockRequestDTO) {
        return ResponseEntity.ok(stockService.updateById(id,stockRequestDTO));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<StockResponseDTO> findByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(stockService.findByProductId(productId));
    }


}
