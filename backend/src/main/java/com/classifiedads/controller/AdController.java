package com.classifiedads.controller;

import com.classifiedads.model.dto.AdDetailDto;
import com.classifiedads.model.dto.AdMapPointDto;
import com.classifiedads.model.dto.CreateAdRequest;
import com.classifiedads.service.AdService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/ads")
@Slf4j
public class AdController {

    @Autowired
    private AdService adService;

    @GetMapping
    public ResponseEntity<Page<AdDetailDto>> getAllAds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdDetailDto> ads = adService.getAllAds(pageable);
        return ResponseEntity.ok(ads);
    }

    @GetMapping("/map-points")
    public ResponseEntity<List<AdMapPointDto>> getMapPoints(
            @RequestParam BigDecimal minLat,
            @RequestParam BigDecimal maxLat,
            @RequestParam BigDecimal minLon,
            @RequestParam BigDecimal maxLon) {
        List<AdMapPointDto> points = adService.getMapPoints(minLat, maxLat, minLon, maxLon);
        return ResponseEntity.ok(points);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<AdDetailDto>> getAdsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdDetailDto> ads = adService.getAdsByCategory(category, pageable);
        return ResponseEntity.ok(ads);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<AdDetailDto>> getUserAds(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdDetailDto> ads = adService.getUserAds(userId, pageable);
        return ResponseEntity.ok(ads);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdDetailDto> getAdById(@PathVariable Long id) {
        AdDetailDto ad = adService.getAdById(id);
        return ResponseEntity.ok(ad);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AdDetailDto> createAd(@Valid @RequestBody CreateAdRequest request) {
        try {
            AdDetailDto ad = adService.createAd(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ad);
        } catch (Exception e) {
            log.error("Ad creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AdDetailDto> updateAd(
            @PathVariable Long id,
            @Valid @RequestBody CreateAdRequest request) {
        try {
            AdDetailDto ad = adService.updateAd(id, request);
            return ResponseEntity.ok(ad);
        } catch (Exception e) {
            log.error("Ad update failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAd(@PathVariable Long id) {
        try {
            adService.deleteAd(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Ad deletion failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AdDetailDto> uploadAdImages(
            @PathVariable Long id,
            @RequestParam("files") MultipartFile[] files) {
        try {
            AdDetailDto ad = adService.uploadAdImages(id, files);
            return ResponseEntity.ok(ad);
        } catch (IOException e) {
            log.error("Image upload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{adId}/images/{imageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAdImage(
            @PathVariable Long adId,
            @PathVariable Long imageId) {
        try {
            adService.deleteAdImage(adId, imageId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Image deletion failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
