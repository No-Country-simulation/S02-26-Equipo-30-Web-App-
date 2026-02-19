package com.nc.horseretail.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.nc.horseretail.dto.HorseResponse;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HorseRepository extends JpaRepository<Horse, UUID> {

    boolean existsByExternalId(String horseId);

    @Query("SELECT h FROM Horse h WHERE " +
            "LOWER(h.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(h.breed) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(CAST(h.mainUse AS string)) " +
            "LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Horse> search(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT h.owner.id) FROM Horse h")
    long countDistinctOwners();

    Long countHorsesByOwner(User domainUser);

    List<HorseResponse> findAllByOwner(User owner);

    Page<Horse> findByOwner(User user, Pageable pageable);
}