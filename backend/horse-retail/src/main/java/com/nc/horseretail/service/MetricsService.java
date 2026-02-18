package com.nc.horseretail.service;

import com.nc.horseretail.dto.FeedbackRequest;
import com.nc.horseretail.dto.FeedbackResponse;
import  com.nc.horseretail.model.user.User;

public interface MetricsService {
    long countActiveSellers();
    FeedbackResponse getSatisfactionMetrics();
    void saveFeedback(FeedbackRequest request, User user);
}
