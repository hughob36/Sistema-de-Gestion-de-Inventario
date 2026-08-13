package com.product_service.product_service.service;

import com.product_service.product_service.client.IStockClient;
import com.product_service.product_service.dto.ProductRequestDTO;
import com.product_service.product_service.dto.ProductResponseDTO;
import com.product_service.product_service.dto.StockResponseDTO;
import com.product_service.product_service.exception.ResourceNotFoundException;
import com.product_service.product_service.mapper.IProductMapper;
import com.product_service.product_service.model.Product;
import com.product_service.product_service.repository.IProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private IProductRepository productRepository;

    @Mock
    private IStockClient stockClient;

    @Mock
    private IProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductResponseDTO productResponseDTO;
    private ProductRequestDTO productRequestDTO;
    private StockResponseDTO stockResponseDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setProductId(1L);

        productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setProductId(1L);

        productRequestDTO = new ProductRequestDTO();

        stockResponseDTO = new StockResponseDTO();
        stockResponseDTO.setProductId(1L);
        stockResponseDTO.setQuantity(10);
        stockResponseDTO.setStatus("IN_STOCK");
    }

    @Nested
    @DisplayName("Tests para findAll()")
    class FindAllTests {

        @Test
        @DisplayName("Debe retornar lista de productos con información de stock cuando existen coincidencias")
        void findAll_Success() {
            // Arrange
            List<Product> products = List.of(product);
            List<ProductResponseDTO> dtos = List.of(productResponseDTO);
            List<StockResponseDTO> stocks = List.of(stockResponseDTO);

            when(productRepository.findAll()).thenReturn(products);
            when(productMapper.toProductResponseDTOList(products)).thenReturn(dtos);
            when(stockClient.findAll()).thenReturn(stocks);

            // Act
            List<ProductResponseDTO> result = productService.findAll();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(10, result.get(0).getStock());
            assertEquals("IN_STOCK", result.get(0).getStatus());

            verify(productRepository).findAll();
            verify(stockClient).findAll();
        }

        @Test
        @DisplayName("Debe asignar stock 0 y status OUT_OF_STOCK cuando el stockClient falla o no encuentra el producto")
        void findAll_StockFallbackOrNotFound() {
            // Arrange
            List<Product> products = List.of(product);
            List<ProductResponseDTO> dtos = List.of(productResponseDTO);

            when(productRepository.findAll()).thenReturn(products);
            when(productMapper.toProductResponseDTOList(products)).thenReturn(dtos);
            when(stockClient.findAll()).thenReturn(Collections.emptyList()); // Simula respuesta vacía o fallback

            // Act
            List<ProductResponseDTO> result = productService.findAll();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(0, result.get(0).getStock());
            assertEquals("OUT_OF_STOCK", result.get(0).getStatus());
        }
    }

    @Nested
    @DisplayName("Tests para findById()")
    class FindByIdTests {

        @Test
        @DisplayName("Debe retornar el producto con su stock cuando existe el ID y el stock")
        void findById_Success() {
            // Arrange
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(stockClient.findByProductId(1L)).thenReturn(Optional.of(stockResponseDTO));
            when(productMapper.toProductResponseDTO(product)).thenReturn(productResponseDTO);

            // Act
            ProductResponseDTO result = productService.findById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(10, result.getStock());
            assertEquals("IN_STOCK", result.getStatus());
            verify(productRepository).findById(1L);
            verify(stockClient).findByProductId(1L);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el producto no existe en la BD")
        void findById_ProductNotFound_ThrowsException() {
            // Arrange
            when(productRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> productService.findById(1L)
            );

            assertEquals("Resource not found.", exception.getMessage());
            verify(stockClient, never()).findByProductId(any());
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando no se encuentra el stock del producto")
        void findById_StockNotFound_ThrowsException() {
            // Arrange
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(stockClient.findByProductId(1L)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> productService.findById(1L)
            );

            assertEquals("Resource stock not found.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Tests para save()")
    class SaveTests {

        @Test
        @DisplayName("Debe guardar y retornar el nuevo producto DTO exitosamente")
        void save_Success() {
            // Arrange
            when(productMapper.toProduct(productRequestDTO)).thenReturn(product);
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toProductResponseDTO(product)).thenReturn(productResponseDTO);

            // Act
            ProductResponseDTO result = productService.save(productRequestDTO);

            // Assert
            assertNotNull(result);
            verify(productRepository).save(product);
        }
    }

    @Nested
    @DisplayName("Tests para deleteById()")
    class DeleteByIdTests {

        @Test
        @DisplayName("Debe eliminar el producto correctamente si el ID existe")
        void deleteById_Success() {
            // Arrange
            when(productRepository.existsById(1L)).thenReturn(true);
            doNothing().when(productRepository).deleteById(1L);

            // Act
            assertDoesNotThrow(() -> productService.deleteById(1L));

            // Assert
            verify(productRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException si se intenta eliminar un ID inexistente")
        void deleteById_NotFound_ThrowsException() {
            // Arrange
            when(productRepository.existsById(1L)).thenReturn(false);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> productService.deleteById(1L)
            );

            assertEquals("Id not found.", exception.getMessage());
            verify(productRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Tests para updateById()")
    class UpdateByIdTests {

        @Test
        @DisplayName("Debe actualizar y devolver el producto cuando el ID existe")
        void updateById_Success() {
            // Arrange
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            doNothing().when(productMapper).updateProductFromDto(productRequestDTO, product);
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toProductResponseDTO(product)).thenReturn(productResponseDTO);

            // Act
            ProductResponseDTO result = productService.updateById(1L, productRequestDTO);

            // Assert
            assertNotNull(result);
            verify(productRepository).findById(1L);
            verify(productMapper).updateProductFromDto(productRequestDTO, product);
            verify(productRepository).save(product);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException si se intenta actualizar un ID inexistente")
        void updateById_NotFound_ThrowsException() {
            // Arrange
            when(productRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> productService.updateById(1L, productRequestDTO)
            );

            assertEquals("Resource not found.", exception.getMessage());
            verify(productRepository, never()).save(any());
        }
    }
}