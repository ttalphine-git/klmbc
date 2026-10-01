package com.classifiedads.repository;

import com.classifiedads.model.entity.AdImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdImageRepository extends JpaRepository<AdImage, Long> {
    List<AdImage> findByAdIdOrderByDisplayOrder(Long adId);
    void deleteByAdId(Long adId);
}
