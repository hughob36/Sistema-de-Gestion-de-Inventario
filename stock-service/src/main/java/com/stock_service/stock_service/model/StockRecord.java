package com.stock_service.stock_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_records")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId; // Referencia al producto (no FK física)

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, length = 100)
    private String warehouse; // Almacén: "CENTRAL", "WAREHOUSE_A", "WAREHOUSE_B"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StockStatus status; // AVAILABLE, RESERVED, OUT_OF_STOCK

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @Column(name = "reserved_quantity")
    private Integer reservedQuantity = 0; // Cantidad reservada para pedidos

    @PrePersist
    @PreUpdate
    protected void onCreateOrUpdate() {
        lastUpdate = LocalDateTime.now();

        // Actualizar estado basado en cantidad
        if (quantity <= 0) {
            status = StockStatus.OUT_OF_STOCK;
        } else if (reservedQuantity > 0 && reservedQuantity == quantity) {
            status = StockStatus.RESERVED;
        } else {
            status = StockStatus.AVAILABLE;
        }
    }
}
