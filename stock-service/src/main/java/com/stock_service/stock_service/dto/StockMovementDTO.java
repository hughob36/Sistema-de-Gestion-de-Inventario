package com.stock_service.stock_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter
public class StockMovementDTO {

    private Long productId;
    private MovementType type; // IN (entrada), OUT (salida), ADJUSTMENT (ajuste)
    private Integer quantity;
    private String warehouse;
    private String reason; // "PURCHASE", "SALE", "RETURN", "ADJUSTMENT"
    private LocalDateTime timestamp;

    public enum MovementType {
        IN,         // Entrada de mercadería
        OUT,        // Salida de mercadería
        ADJUSTMENT  // Ajuste de inventario
    }
}
