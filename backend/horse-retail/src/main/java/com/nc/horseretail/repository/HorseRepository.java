package com.nc.horseretail.repository;

import com.nc.horseretail.model.horse.Horse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HorseRepository extends JpaRepository<Horse, UUID> {
    boolean existsByExternalId(String horseId);

    @Query("SELECT h FROM Horse h WHERE " +
           "LOWER(h.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(h.breed) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(CAST(h.mainUse AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Horse> searchGlobal(@Param("keyword") String keyword);
}
