package com.stock_service.stock_service.service;

import com.stock_service.stock_service.dto.StockRequestDTO;
import com.stock_service.stock_service.dto.StockResponseDTO;
import com.stock_service.stock_service.exception.ResourceNotFoundException;
import com.stock_service.stock_service.mapper.IStockMapper;
import com.stock_service.stock_service.model.StockRecord;
import com.stock_service.stock_service.repository.IStockRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private IStockRepository stockRepository;

    @Mock
    private IStockMapper stockMapper;

    @InjectMocks
    private StockService stockService;

    private StockRecord stockRecord;
    private StockRequestDTO stockRequestDTO;
    private StockResponseDTO stockResponseDTO;

    @BeforeEach
    void setUp() {
        stockRecord = new StockRecord();
        stockRequestDTO = new StockRequestDTO();
        stockResponseDTO = new StockResponseDTO();
    }

    @Nested
    @DisplayName("findAll Tests")
    class FindAllTests {

        @Test
        @DisplayName("Debe retornar la lista de StockResponseDTO cuando existen registros")
        void findAll_WhenRecordsExist_ReturnsList() {
            List<StockRecord> records = List.of(stockRecord);
            List<StockResponseDTO> expectedResponse = List.of(stockResponseDTO);

            when(stockRepository.findAll()).thenReturn(records);
            when(stockMapper.toStockResponseDTOList(records)).thenReturn(expectedResponse);

            List<StockResponseDTO> result = stockService.findAll();

            assertThat(result).isNotNull().hasSize(1);
            assertThat(result).isEqualTo(expectedResponse);
            verify(stockRepository).findAll();
            verify(stockMapper).toStockResponseDTOList(records);
        }

        @Test
        @DisplayName("Debe retornar una lista vacía cuando no existen registros")
        void findAll_WhenEmpty_ReturnsEmptyList() {
            when(stockRepository.findAll()).thenReturn(Collections.emptyList());
            when(stockMapper.toStockResponseDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

            List<StockResponseDTO> result = stockService.findAll();

            assertThat(result).isNotNull().isEmpty();
            verify(stockRepository).findAll();
            verify(stockMapper).toStockResponseDTOList(Collections.emptyList());
        }
    }

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Debe retornar StockResponseDTO cuando el ID existe")
        void findById_WhenIdExists_ReturnsStockResponseDTO() {
            Long id = 1L;
            when(stockRepository.findById(id)).thenReturn(Optional.of(stockRecord));
            when(stockMapper.toStockResponseDTO(stockRecord)).thenReturn(stockResponseDTO);

            StockResponseDTO result = stockService.findById(id);

            assertThat(result).isNotNull().isEqualTo(stockResponseDTO);
            verify(stockRepository).findById(id);
            verify(stockMapper).toStockResponseDTO(stockRecord);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el ID no existe")
        void findById_WhenIdDoesNotExist_ThrowsResourceNotFoundException() {
            Long id = 99L;
            when(stockRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> stockService.findById(id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Resource not found.");

            verify(stockRepository).findById(id);
            verifyNoInteractions(stockMapper);
        }
    }

    @Nested
    @DisplayName("save Tests")
    class SaveTests {

        @Test
        @DisplayName("Debe guardar y retornar StockResponseDTO exitosamente")
        void save_Success_ReturnsStockResponseDTO() {
            when(stockMapper.toStock(stockRequestDTO)).thenReturn(stockRecord);
            when(stockRepository.save(stockRecord)).thenReturn(stockRecord);
            when(stockMapper.toStockResponseDTO(stockRecord)).thenReturn(stockResponseDTO);

            StockResponseDTO result = stockService.save(stockRequestDTO);

            assertThat(result).isNotNull().isEqualTo(stockResponseDTO);
            verify(stockMapper).toStock(stockRequestDTO);
            verify(stockRepository).save(stockRecord);
            verify(stockMapper).toStockResponseDTO(stockRecord);
        }
    }

    @Nested
    @DisplayName("deleteById Tests")
    class DeleteByIdTests {

        @Test
        @DisplayName("Debe eliminar el registro cuando el ID existe")
        void deleteById_WhenIdExists_DeletesSuccessfully() {
            Long id = 1L;
            when(stockRepository.existsById(id)).thenReturn(true);
            doNothing().when(stockRepository).deleteById(id);

            stockService.deleteById(id);

            verify(stockRepository).existsById(id);
            verify(stockRepository).deleteById(id);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el ID a eliminar no existe")
        void deleteById_WhenIdDoesNotExist_ThrowsResourceNotFoundException() {
            Long id = 99L;
            when(stockRepository.existsById(id)).thenReturn(false);

            assertThatThrownBy(() -> stockService.deleteById(id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Resource not found.");

            verify(stockRepository).existsById(id);
            verify(stockRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("updateById Tests")
    class UpdateByIdTests {

        @Test
        @DisplayName("Debe actualizar y retornar StockResponseDTO cuando el ID existe")
        void updateById_WhenIdExists_UpdatesAndReturnsResponse() {
            Long id = 1L;
            when(stockRepository.findById(id)).thenReturn(Optional.of(stockRecord));
            doNothing().when(stockMapper).updateStockFromDto(stockRequestDTO, stockRecord);
            when(stockRepository.save(stockRecord)).thenReturn(stockRecord);
            when(stockMapper.toStockResponseDTO(stockRecord)).thenReturn(stockResponseDTO);

            StockResponseDTO result = stockService.updateById(id, stockRequestDTO);

            assertThat(result).isNotNull().isEqualTo(stockResponseDTO);
            verify(stockRepository).findById(id);
            verify(stockMapper).updateStockFromDto(stockRequestDTO, stockRecord);
            verify(stockRepository).save(stockRecord);
            verify(stockMapper).toStockResponseDTO(stockRecord);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException al intentar actualizar un ID inexistente")
        void updateById_WhenIdDoesNotExist_ThrowsResourceNotFoundException() {
            Long id = 99L;
            when(stockRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> stockService.updateById(id, stockRequestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Resource not found.");

            verify(stockRepository).findById(id);
            verify(stockMapper, never()).updateStockFromDto(any(), any());
            verify(stockRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("findByProductId Tests")
    class FindByProductIdTests {

        @Test
        @DisplayName("Debe retornar StockResponseDTO cuando el Product ID existe")
        void findByProductId_WhenProductIdExists_ReturnsStockResponseDTO() {
            Long productId = 10L;
            when(stockRepository.findByProductId(productId)).thenReturn(Optional.of(stockRecord));
            when(stockMapper.toStockResponseDTO(stockRecord)).thenReturn(stockResponseDTO);

            StockResponseDTO result = stockService.findByProductId(productId);

            assertThat(result).isNotNull().isEqualTo(stockResponseDTO);
            verify(stockRepository).findByProductId(productId);
            verify(stockMapper).toStockResponseDTO(stockRecord);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el Product ID no existe")
        void findByProductId_WhenProductIdDoesNotExist_ThrowsResourceNotFoundException() {
            Long productId = 99L;
            when(stockRepository.findByProductId(productId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> stockService.findByProductId(productId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Product id not found.");

            verify(stockRepository).findByProductId(productId);
            verifyNoInteractions(stockMapper);
        }
    }
}