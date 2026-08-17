package com.product_service.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductResponseDTO {

    private Long id;
    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Boolean active;

    // Información de stock
    private Integer stock;
    private String status; // "AVAILABLE","OUT_OF_STOCK"

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
