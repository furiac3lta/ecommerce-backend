package com.colors.ecommerce.backend.infrastucture.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReturnRequest {
    private Integer orderId;
    private List<ReturnItemRequest> items;
    private String createdBy;
}
