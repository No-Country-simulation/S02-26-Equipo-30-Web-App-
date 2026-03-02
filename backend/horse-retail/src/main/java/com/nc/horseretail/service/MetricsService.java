package com.nc.horseretail.service;

import com.nc.horseretail.dto.FeedbackRequest;
import com.nc.horseretail.dto.FeedbackResponse;
import com.nc.horseretail.dto.metrics.BreedMetricResponse;
import com.nc.horseretail.dto.metrics.CountryMetricResponse;
import com.nc.horseretail.dto.metrics.MonthlyGrowthResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface MetricsService {

    long countActiveSellers();

    FeedbackResponse getSatisfactionMetrics();

    void saveFeedback(FeedbackRequest request, UUID userId);

    Long countAllListings();

    List<BreedMetricResponse> getTopBreeds();

    List<CountryMetricResponse> getTopCountries();

    Long countUsers();

    Long countConversations();

    Long countSoldListings();

    BigDecimal getTotalRevenue();

    List<MonthlyGrowthResponse> getMonthlyGrowth();
}
