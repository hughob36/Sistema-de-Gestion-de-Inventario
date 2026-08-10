package com.product_service.product_service.client.fallback;

import com.product_service.product_service.client.IStockClient;
import com.product_service.product_service.dto.StockResponseDTO;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class StockClientFallbackFactory implements FallbackFactory<IStockClient> {

    @Override
    public IStockClient create(Throwable cause) {
        return new IStockClient() {

            @Override
            public List<StockResponseDTO> findAll() {
                // En caso de error o servicio caído, retornamos una lista vacía
                return Collections.emptyList();
            }

            @Override
            public Optional<StockResponseDTO> findByProductId(Long productId) {
                // En caso de error, retornamos Optional vacío
                return Optional.empty();
            }
        };
    }
}
