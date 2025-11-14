package com.product_service.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockResponseDTO {

    private Long productId;
    private Integer quantity;
    private String warehouse; // Almacén donde está ubicado
    private String status; // "AVAILABLE", "RESERVED", "OUT_OF_STOCK"
}
