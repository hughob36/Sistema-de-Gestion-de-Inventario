package com.product_service.product_service.service;

import com.product_service.product_service.client.IStockClient;
import com.product_service.product_service.dto.ProductRequestDTO;
import com.product_service.product_service.dto.ProductResponseDTO;
import com.product_service.product_service.dto.StockResponseDTO;
import com.product_service.product_service.exception.ResourceNotFoundException;
import com.product_service.product_service.mapper.IProductMapper;
import com.product_service.product_service.model.Product;
import com.product_service.product_service.repository.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{

    private final IProductRepository productRepository;
    private final IStockClient stockClient;
    private final IProductMapper productMapper;

    @Override
    public List<ProductResponseDTO> findAll() {

        List<Product> products = productRepository.findAll();
        List<ProductResponseDTO> productDTOs = productMapper.toProductResponseDTOList(products);

        // Si stock-service falla, esto devuelve [] por el fallback
        List<StockResponseDTO> stockList = stockClient.findAll();

        // Convertir stock list → mapa para buscar rápido
        Map<Long, StockResponseDTO> stockMap = stockList.stream()
                .collect(Collectors.toMap(StockResponseDTO::getProductId, s -> s));

        // Completar los productos con info del stock
        for (ProductResponseDTO dto : productDTOs) {

            StockResponseDTO stock = stockMap.get(dto.getProductId());

            if (stock != null) {
                dto.setStock(stock.getQuantity());
                dto.setStatus(stock.getStatus());
            } else {
                dto.setStock(0);
                dto.setStatus("OUT_OF_STOCK");
            }
        }
        return productDTOs;
    }

    @Override
    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found."));

        StockResponseDTO stock = stockClient.findByProductId(product.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource stock not found."));

        ProductResponseDTO productResponseDTO = productMapper.toProductResponseDTO(product);

        productResponseDTO.setStock(stock.getQuantity());
        productResponseDTO.setStatus(stock.getStatus());

        return productResponseDTO;
    }

    @Override
    public ProductResponseDTO save(ProductRequestDTO productRequestDTO) {
        Product product = productMapper.toProduct(productRequestDTO);
        return productMapper.toProductResponseDTO(productRepository.save(product));
    }

    @Override
    public void deleteById(Long id) {
        if(!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Id not found.");
        }
        productRepository.deleteById(id);
    }

    @Override
    public ProductResponseDTO updateById(Long id, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found."));

        productMapper.updateProductFromDto(productRequestDTO,product);
        Product saveProduct = productRepository.save(product);
        return productMapper.toProductResponseDTO(saveProduct);
    }
}
