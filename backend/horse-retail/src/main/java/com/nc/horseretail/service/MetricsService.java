package com.nc.horseretail.service;

import com.nc.horseretail.dto.FeedbackRequest;
import com.nc.horseretail.dto.FeedbackResponse;
import com.nc.horseretail.dto.metrics.BreedMetricResponse;
import com.nc.horseretail.dto.metrics.CountryMetricResponse;
import com.nc.horseretail.dto.metrics.MonthlyGrowthResponse;
import com.nc.horseretail.model.user.User;

import java.math.BigDecimal;
import java.util.List;

public interface MetricsService {

    long countActiveSellers();

    FeedbackResponse getSatisfactionMetrics();

    void saveFeedback(FeedbackRequest request, User user);

    Long countAllListings();

    List<BreedMetricResponse> getTopBreeds();

    List<CountryMetricResponse> getTopCountries();

    Long countUsers();

    Long countConversations();

    Long countSoldListings();

    BigDecimal getTotalRevenue();

    List<MonthlyGrowthResponse> getMonthlyGrowth();
}
