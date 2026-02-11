package com.colors.ecommerce.backend.infrastucture.adapter;

import com.colors.ecommerce.backend.domain.model.StockMovement;
import com.colors.ecommerce.backend.domain.model.StockMovementReason;
import com.colors.ecommerce.backend.domain.model.StockMovementType;
import com.colors.ecommerce.backend.domain.port.IStockMovementRepository;
import com.colors.ecommerce.backend.infrastucture.entity.OrderEntity;
import com.colors.ecommerce.backend.infrastucture.entity.ProductVariantEntity;
import com.colors.ecommerce.backend.infrastucture.entity.StockMovementEntity;
import com.colors.ecommerce.backend.infrastucture.mapper.StockMovementMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class StockMovementCrudRepositoryImpl implements IStockMovementRepository {
    private final StockMovementMapper stockMovementMapper;
    private final IStockMovementCrudRepository stockMovementCrudRepository;

    public StockMovementCrudRepositoryImpl(StockMovementMapper stockMovementMapper, IStockMovementCrudRepository stockMovementCrudRepository) {
        this.stockMovementMapper = stockMovementMapper;
        this.stockMovementCrudRepository = stockMovementCrudRepository;
    }

    @Override
    public StockMovement save(StockMovement movement) {
        StockMovementEntity entity = stockMovementMapper.toStockMovementEntity(movement);
        if (movement.getVariantId() != null) {
            ProductVariantEntity variantEntity = new ProductVariantEntity();
            variantEntity.setId(movement.getVariantId());
            entity.setVariantEntity(variantEntity);
        }
        if (movement.getOrderId() != null) {
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setId(movement.getOrderId());
            entity.setOrderEntity(orderEntity);
        }
        return stockMovementMapper.toStockMovement(stockMovementCrudRepository.save(entity));
    }

    @Override
    public Iterable<StockMovement> findByVariantIdAndDateRange(Integer variantId, LocalDateTime from, LocalDateTime to) {
        return stockMovementMapper.toStockMovementList(stockMovementCrudRepository.findByVariantAndRange(variantId, from, to));
    }

    @Override
    public Iterable<StockMovement> findByFilters(Integer variantId, LocalDateTime from, LocalDateTime to, StockMovementType type, StockMovementReason reason) {
        return stockMovementMapper.toStockMovementList(stockMovementCrudRepository.findByFilters(variantId, from, to, type, reason));
    }

    @Override
    public Iterable<StockMovement> findByOrderId(Integer orderId) {
        return stockMovementMapper.toStockMovementList(stockMovementCrudRepository.findByOrderId(orderId));
    }

    @Override
    public Iterable<StockMovement> findByDateRange(LocalDateTime from, LocalDateTime to) {
        return stockMovementMapper.toStockMovementList(stockMovementCrudRepository.findByDateRange(from, to));
    }
}
