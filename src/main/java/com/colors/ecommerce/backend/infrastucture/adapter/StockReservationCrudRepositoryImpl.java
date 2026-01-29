package com.colors.ecommerce.backend.infrastucture.adapter;

import com.colors.ecommerce.backend.domain.model.StockReservation;
import com.colors.ecommerce.backend.domain.port.IStockReservationRepository;
import com.colors.ecommerce.backend.infrastucture.entity.OrderEntity;
import com.colors.ecommerce.backend.infrastucture.entity.ProductVariantEntity;
import com.colors.ecommerce.backend.infrastucture.entity.StockReservationEntity;
import com.colors.ecommerce.backend.infrastucture.mapper.StockReservationMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class StockReservationCrudRepositoryImpl implements IStockReservationRepository {
    private final StockReservationMapper stockReservationMapper;
    private final IStockReservationCrudRepository stockReservationCrudRepository;

    public StockReservationCrudRepositoryImpl(StockReservationMapper stockReservationMapper, IStockReservationCrudRepository stockReservationCrudRepository) {
        this.stockReservationMapper = stockReservationMapper;
        this.stockReservationCrudRepository = stockReservationCrudRepository;
    }

    @Override
    public StockReservation save(StockReservation reservation) {
        StockReservationEntity entity = stockReservationMapper.toStockReservationEntity(reservation);
        if (reservation.getVariantId() != null) {
            ProductVariantEntity variantEntity = new ProductVariantEntity();
            variantEntity.setId(reservation.getVariantId());
            entity.setVariantEntity(variantEntity);
        }
        if (reservation.getOrderId() != null) {
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setId(reservation.getOrderId());
            entity.setOrderEntity(orderEntity);
        }
        return stockReservationMapper.toStockReservation(stockReservationCrudRepository.save(entity));
    }

    @Override
    public List<StockReservation> findByOrderId(Integer orderId) {
        return toList(stockReservationMapper.toStockReservationList(stockReservationCrudRepository.findByOrderEntityId(orderId)));
    }

    @Override
    public List<StockReservation> findActiveByOrderId(Integer orderId) {
        return toList(stockReservationMapper.toStockReservationList(stockReservationCrudRepository.findActiveByOrderId(orderId)));
    }

    @Override
    public Integer sumActiveReservedQty(Integer variantId, LocalDateTime now) {
        return stockReservationCrudRepository.sumActiveReservedQty(variantId, now);
    }

    @Override
    public List<StockReservation> findExpiredActive(LocalDateTime now) {
        return toList(stockReservationMapper.toStockReservationList(stockReservationCrudRepository.findExpiredActive(now)));
    }

    @Override
    public List<StockReservation> findByVariantId(Integer variantId) {
        return toList(stockReservationMapper.toStockReservationList(stockReservationCrudRepository.findByVariantEntityId(variantId)));
    }

    private List<StockReservation> toList(Iterable<StockReservation> iterable) {
        java.util.List<StockReservation> list = new java.util.ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
