package com.nc.horseretail.service;

import com.nc.horseretail.dto.FeedbackRequest;
import com.nc.horseretail.dto.FeedbackResponse;
import com.nc.horseretail.model.feedback.Feedback;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.HorseRepository;
import com.nc.horseretail.repository.MetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MetricsServiceImpl implements MetricsService {

    private final HorseRepository horseRepository;
    private final MetricsRepository metricsRepository;

    @Override
    @Transactional(readOnly = true)
    public long countActiveSellers() {
        return horseRepository.countDistinctOwners();
    }

    @Override
    @Transactional(readOnly = true)
    public FeedbackResponse getSatisfactionMetrics() {
        Double average = metricsRepository.getAverageRating();
        Long total = metricsRepository.count();
        
        return FeedbackResponse.builder()
                .averageRating(Math.round(average * 10.0) / 10.0)
                .totalFeedbacks(total)
                .build();
    }

    @Override
    @Transactional
    public void saveFeedback(FeedbackRequest request, User user) {
        Feedback feedback = Feedback.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .user(user)
                .build();

        metricsRepository.save(feedback);
    }
}
