package com.nc.horseretail.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MetricsRepositoryImpl implements MetricsRepository {

    private final EntityManager entityManager;

    @Override
    public List<Object[]> getTopBreedsRaw() {
        return entityManager.createQuery("""
                SELECT h.breed, COUNT(h)
                FROM Horse h
                GROUP BY h.breed
                ORDER BY COUNT(h) DESC
                """, Object[].class)
                .setMaxResults(5)
                .getResultList();
    }

    @Override
    public List<Object[]> getTopCountriesRaw() {
        return entityManager.createQuery("""
                SELECT h.location.country, COUNT(h)
                FROM Horse h
                GROUP BY h.location.country
                ORDER BY COUNT(h) DESC
                """, Object[].class)
                .setMaxResults(5)
                .getResultList();
    }

    @Override
    public Long countUsers() {
        return entityManager.createQuery(
                "SELECT COUNT(u) FROM User u",
                Long.class
        ).getSingleResult();
    }

    @Override
    public Long countConversations() {
        return entityManager.createQuery(
                "SELECT COUNT(c) FROM Conversation c",
                Long.class
        ).getSingleResult();
    }

    @Override
    public Long countSoldListings() {
        return entityManager.createQuery(
                "SELECT COUNT(l) FROM Listing l WHERE l.status = 'SOLD'",
                Long.class
        ).getSingleResult();
    }

    @Override
    public List<Object[]> getMonthlyGrowthRaw() {
        return entityManager.createQuery("""
                SELECT YEAR(u.createdAt),
                       MONTH(u.createdAt),
                       COUNT(u)
                FROM User u
                GROUP BY YEAR(u.createdAt), MONTH(u.createdAt)
                ORDER BY YEAR(u.createdAt), MONTH(u.createdAt)
                """, Object[].class)
                .getResultList();
    }
}