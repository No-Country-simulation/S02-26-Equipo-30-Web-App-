package com.nc.horseretail.repository;

import com.nc.horseretail.model.feedback.Feedback;
import com.nc.horseretail.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface MetricsRepository {

    List<Object[]> getTopBreedsRaw();

    List<Object[]> getTopCountriesRaw();

    Long countUsers();

    Long countConversations();

    Long countSoldListings();

    List<Object[]> getMonthlyGrowthRaw();
}