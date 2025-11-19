package com.stock_service.stock_service.service;


import com.stock_service.stock_service.dto.StockRequestDTO;
import com.stock_service.stock_service.dto.StockResponseDTO;

import java.util.List;

public interface IStockService {

    public List<StockResponseDTO> findAll();
    public StockResponseDTO findById(Long id);
    public StockResponseDTO save(StockRequestDTO stockRequestDTO);
    public void deleteById(Long id);
    public StockResponseDTO updateById(Long id, StockRequestDTO stockRequestDTO);

    public StockResponseDTO findByProductId(Long id);
}
