package com.nc.horseretail.service;

import com.nc.horseretail.dto.FeedbackRequest;

public interface MetricsService {
    long countActiveSellers();
    Double getSatisfactionScore();
    void saveFeedback(FeedbackRequest request, User user);
}