package com.stock_service.stock_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockResponseDTO {

    private Long productId;
    private Integer quantity;
    private String warehouse;
    private String status;
    private LocalDateTime lastUpdate;
}
