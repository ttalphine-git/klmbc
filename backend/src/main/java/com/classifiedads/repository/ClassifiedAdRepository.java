package com.classifiedads.repository;

import com.classifiedads.model.entity.ClassifiedAd;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ClassifiedAdRepository extends JpaRepository<ClassifiedAd, Long> {
    Page<ClassifiedAd> findByStatus(String status, Pageable pageable);
    Page<ClassifiedAd> findByUserId(Long userId, Pageable pageable);
    Page<ClassifiedAd> findByStatusAndCategory(String status, String category, Pageable pageable);

    @Query("SELECT a FROM ClassifiedAd a WHERE a.status = 'ACTIVE' AND a.latitude BETWEEN :minLat AND :maxLat AND a.longitude BETWEEN :minLon AND :maxLon")
    List<ClassifiedAd> findByLocationBounds(
        @Param("minLat") BigDecimal minLat,
        @Param("maxLat") BigDecimal maxLat,
        @Param("minLon") BigDecimal minLon,
        @Param("maxLon") BigDecimal maxLon
    );
}
