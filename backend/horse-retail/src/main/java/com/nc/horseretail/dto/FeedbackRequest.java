package com.nc.horseretail.dto;

import lombok.Data;

@Data
public class FeedbackRequest {
    private Integer rating;
    private String comment;
}