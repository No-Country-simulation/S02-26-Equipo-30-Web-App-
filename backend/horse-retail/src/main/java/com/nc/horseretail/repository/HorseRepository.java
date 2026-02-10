package com.nc.horseretail.repository;

import com.nc.horseretail.model.horse.Horse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HorseRepository extends JpaRepository<Horse, UUID> {
}
