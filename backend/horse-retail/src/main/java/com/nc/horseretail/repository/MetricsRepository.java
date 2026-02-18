package com.nc.horseretail.repository;

import com.nc.horseretail.model.feedback.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MetricsRepository extends JpaRepository<Feedback, UUID> {

    @Query("SELECT COALESCE(AVG(f.rating), 0.0) FROM Feedback f")
    Double getAverageRating();
}