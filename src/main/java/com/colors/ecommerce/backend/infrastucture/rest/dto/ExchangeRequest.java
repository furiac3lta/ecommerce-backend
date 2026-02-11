package com.colors.ecommerce.backend.infrastucture.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExchangeRequest {
    private Integer orderId;
    private List<ReturnItemRequest> returnItems;
    private List<ExchangeItemRequest> newItems;
    private String createdBy;
}
