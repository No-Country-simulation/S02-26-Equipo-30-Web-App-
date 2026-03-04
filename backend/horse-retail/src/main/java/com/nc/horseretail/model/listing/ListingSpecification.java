package com.nc.horseretail.model.listing;

import com.nc.horseretail.dto.ListingFilterRequest;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.horse.Location;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ListingSpecification {

    private ListingSpecification() {
        // Private constructor to prevent instantiation
    }

    public static Specification<Listing> withFilters(ListingFilterRequest filter) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Join with Horse
            Join<Listing, Horse> horseJoin = root.join("horse", JoinType.INNER);


            // Only public and active listings
            predicates.add(
                    root.get("status").in(
                            ListingStatus.PUBLIC,
                            ListingStatus.ACTIVE
                    )
            );

            if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {

                String pattern = "%" + filter.getKeyword().toLowerCase() + "%";

                predicates.add(
                        cb.like(
                                cb.lower(horseJoin.get("name")),
                                pattern
                        )
                );
            }

            if (filter.getDiscipline() != null) {
                predicates.add(
                        cb.equal(
                                horseJoin.get("mainUse"),
                                filter.getDiscipline()
                        )
                );
            }

            if (filter.getBreed() != null && !filter.getBreed().isBlank()) {
                predicates.add(
                        cb.equal(
                                cb.lower(horseJoin.get("breed")),
                                filter.getBreed().toLowerCase()
                        )
                );
            }

            if (filter.getLocation() != null && !filter.getLocation().isBlank()) {

                String pattern = "%" + filter.getLocation().toLowerCase() + "%";

                Path<Location> locationPath = horseJoin.get("location");

                predicates.add(
                        cb.or(
                                cb.like(cb.lower(locationPath.get("country")), pattern),
                                cb.like(cb.lower(locationPath.get("region")), pattern),
                                cb.like(cb.lower(locationPath.get("city")), pattern)
                        )
                );
            }

            if (filter.getMinPrice() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("askingPriceUsd"),
                                filter.getMinPrice()
                        )
                );
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("askingPriceUsd"),
                                filter.getMaxPrice()
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}