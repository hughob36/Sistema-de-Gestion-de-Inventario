package com.stock_service.stock_service.service;

import com.stock_service.stock_service.dto.StockRequestDTO;
import com.stock_service.stock_service.dto.StockResponseDTO;
import com.stock_service.stock_service.exception.ResourceNotFoundException;
import com.stock_service.stock_service.mapper.IStockMapper;
import com.stock_service.stock_service.model.StockRecord;
import com.stock_service.stock_service.repository.IStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService implements IStockService{

    private final IStockRepository stockRepository;
    private final IStockMapper stockMapper;

    @Override
    public List<StockResponseDTO> findAll() {
        return stockMapper.toStockResponseDTOList(stockRepository.findAll());
    }

    @Override
    public StockResponseDTO findById(Long id) {
        StockRecord stockRecord = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found."));
        return stockMapper.toStockResponseDTO(stockRecord);
    }

    @Override
    public StockResponseDTO save(StockRequestDTO stockRequestDTO) {
        StockRecord stockRecord = stockMapper.toStock(stockRequestDTO);
        return stockMapper.toStockResponseDTO(stockRepository.save(stockRecord));
    }

    @Override
    public void deleteById(Long id) {
        if(!stockRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found.");
        }
        stockRepository.deleteById(id);
    }

    @Override
    public StockResponseDTO updateById(Long id, StockRequestDTO stockRequestDTO) {
        StockRecord stockRecordFound = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found." ));
        stockMapper.updateStockFromDto(stockRequestDTO,stockRecordFound);
        return stockMapper.toStockResponseDTO(stockRepository.save(stockRecordFound));
    }

    @Override
    public StockResponseDTO findByProductId(Long id) {
        StockRecord stockRecordFound = stockRepository.findByProductId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product id not found."));
        return stockMapper.toStockResponseDTO(stockRecordFound);
    }
}
