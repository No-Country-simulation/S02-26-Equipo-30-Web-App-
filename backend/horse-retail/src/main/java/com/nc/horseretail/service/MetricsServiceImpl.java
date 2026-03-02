package com.nc.horseretail.service;

import com.nc.horseretail.dto.FeedbackRequest;
import com.nc.horseretail.dto.FeedbackResponse;
import com.nc.horseretail.dto.metrics.BreedMetricResponse;
import com.nc.horseretail.dto.metrics.CountryMetricResponse;
import com.nc.horseretail.dto.metrics.MonthlyGrowthResponse;
import com.nc.horseretail.model.feedback.Feedback;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.FeedbackRepository;
import com.nc.horseretail.repository.HorseRepository;
import com.nc.horseretail.repository.ListingRepository;
import com.nc.horseretail.repository.MetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MetricsServiceImpl implements MetricsService {

    private final HorseRepository horseRepository;
    private final MetricsRepository metricsRepository;
    private final FeedbackRepository feedbackRepository;
    private final ListingRepository listingRepository;

    // ============================
    // PUBLIC METRICS
    // ============================

    @Override
    public long countActiveSellers() {
        return horseRepository.countDistinctOwners();
    }

    @Override
    public FeedbackResponse getSatisfactionMetrics() {

        Double average = feedbackRepository.getAverageRating();

        return FeedbackResponse.builder()
                .averageRating(Math.round(average * 10.0) / 10.0)
                .totalFeedbacks(feedbackRepository.count())
                .build();
    }

    @Override
    @Transactional
    public void saveFeedback(FeedbackRequest request, User user) {

        // Evita feedback duplicado
        if (feedbackRepository.existsByUser(user)) {
            throw new IllegalStateException("User already submitted feedback");
        }

        Feedback feedback = Feedback.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .user(user)
                .build();

        feedbackRepository.save(feedback);
    }

    @Override
    public Long countAllListings() {
        return horseRepository.count();
    }

    @Override
    public List<BreedMetricResponse> getTopBreeds() {

        List<Object[]> results = metricsRepository.getTopBreedsRaw();
        long totalListings = horseRepository.count();

        return results.stream().map(row -> {
            String breed = (String) row[0];
            Long count = (Long) row[1];

            double percentage = totalListings == 0 ? 0.0 : (count * 100.0) / totalListings;

            return BreedMetricResponse.builder()
                    .breed(breed)
                    .listingCount(count)
                    .percentage(Math.round(percentage * 10.0) / 10.0)
                    .build();
        }).toList();
    }

    @Override
    public List<CountryMetricResponse> getTopCountries() {

        List<Object[]> results = metricsRepository.getTopCountriesRaw();
        long totalListings = horseRepository.count();

        return results.stream().map(row -> {
            String country = (String) row[0];
            Long count = (Long) row[1];

            double percentage = totalListings == 0 ? 0.0 : (count * 100.0) / totalListings;

            return CountryMetricResponse.builder()
                    .country(country)
                    .listingCount(count)
                    .percentage(Math.round(percentage * 10.0) / 10.0)
                    .build();
        }).toList();
    }

    // ============================
    // ADMIN METRICS
    // ============================

    @Override
    public Long countUsers() {
        return metricsRepository.countUsers();
    }

    @Override
    public Long countConversations() {
        return metricsRepository.countConversations();
    }

    @Override
    public Long countSoldListings() {
        return metricsRepository.countSoldListings();
    }

    @Override
    public BigDecimal getTotalRevenue() {
        return listingRepository.getTotalRevenue();
    }

    @Override
    public List<MonthlyGrowthResponse> getMonthlyGrowth() {

        List<Object[]> results = metricsRepository.getMonthlyGrowthRaw();

        List<MonthlyGrowthResponse> monthlyGrowth = new ArrayList<>();
        Long previousUsers = null;

        for (Object[] row : results) {
            Integer year = ((Number) row[0]).intValue();
            Integer month = ((Number) row[1]).intValue();
            Long users = ((Number) row[2]).longValue();

            double growth = 0.0;
            if (previousUsers != null && previousUsers > 0) {
                growth = ((users - previousUsers) * 100.0) / previousUsers;
            }

            monthlyGrowth.add(MonthlyGrowthResponse.builder()
                    .year(year)
                    .month(month)
                    .newUsers(users)
                    .newListings(0L)
                    .revenue(BigDecimal.ZERO)
                    .growthPercentage(Math.round(growth * 10.0) / 10.0)
                    .build());

            previousUsers = users;
        }

        return monthlyGrowth;
    }
}
