package com.classifiedads.service;

import com.classifiedads.model.dto.AdDetailDto;
import com.classifiedads.model.dto.AdMapPointDto;
import com.classifiedads.model.dto.CreateAdRequest;
import com.classifiedads.model.entity.AdImage;
import com.classifiedads.model.entity.ClassifiedAd;
import com.classifiedads.model.entity.User;
import com.classifiedads.repository.AdImageRepository;
import com.classifiedads.repository.ClassifiedAdRepository;
import com.classifiedads.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdService {

    @Autowired
    private ClassifiedAdRepository adRepository;

    @Autowired
    private AdImageRepository adImageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public Page<AdDetailDto> getAllAds(Pageable pageable) {
        return adRepository.findByStatus("ACTIVE", pageable)
                .map(this::mapToDetailDto);
    }

    @Transactional(readOnly = true)
    public Page<AdDetailDto> getAdsByCategory(String category, Pageable pageable) {
        return adRepository.findByStatusAndCategory("ACTIVE", category, pageable)
                .map(this::mapToDetailDto);
    }

    @Transactional(readOnly = true)
    public Page<AdDetailDto> getUserAds(Long userId, Pageable pageable) {
        return adRepository.findByUserId(userId, pageable)
                .map(this::mapToDetailDto);
    }

    @Transactional(readOnly = true)
    public AdDetailDto getAdById(Long adId) {
        ClassifiedAd ad = adRepository.findById(adId)
                .orElseThrow(() -> new RuntimeException("Ad not found"));
        ad.setViewCount(ad.getViewCount() + 1);
        adRepository.save(ad);
        return mapToDetailDto(ad);
    }

    @Transactional(readOnly = true)
    public List<AdMapPointDto> getMapPoints(BigDecimal minLat, BigDecimal maxLat, BigDecimal minLon, BigDecimal maxLon) {
        List<ClassifiedAd> ads = adRepository.findByLocationBounds(minLat, maxLat, minLon, maxLon);
        return ads.stream()
                .map(this::mapToMapPointDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdDetailDto createAd(CreateAdRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ClassifiedAd ad = ClassifiedAd.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .locationName(request.getLocationName())
                .status("ACTIVE")
                .viewCount(0)
                .build();

        ClassifiedAd savedAd = adRepository.save(ad);
        log.info("Ad created with ID: {}", savedAd.getId());
        return mapToDetailDto(savedAd);
    }

    @Transactional
    public AdDetailDto updateAd(Long adId, CreateAdRequest request) {
        ClassifiedAd ad = adRepository.findById(adId)
                .orElseThrow(() -> new RuntimeException("Ad not found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!ad.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to update this ad");
        }

        ad.setTitle(request.getTitle());
        ad.setDescription(request.getDescription());
        ad.setPrice(request.getPrice());
        ad.setCategory(request.getCategory());
        ad.setLatitude(request.getLatitude());
        ad.setLongitude(request.getLongitude());
        ad.setLocationName(request.getLocationName());

        ClassifiedAd updatedAd = adRepository.save(ad);
        return mapToDetailDto(updatedAd);
    }

    @Transactional
    public void deleteAd(Long adId) {
        ClassifiedAd ad = adRepository.findById(adId)
                .orElseThrow(() -> new RuntimeException("Ad not found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!ad.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this ad");
        }

        adRepository.deleteById(adId);
        log.info("Ad deleted with ID: {}", adId);
    }

    @Transactional
    public AdDetailDto uploadAdImages(Long adId, MultipartFile[] files) throws IOException {
        ClassifiedAd ad = adRepository.findById(adId)
                .orElseThrow(() -> new RuntimeException("Ad not found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!ad.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to upload images for this ad");
        }

        int displayOrder = adImageRepository.findByAdIdOrderByDisplayOrder(adId).size();

        for (MultipartFile file : files) {
            String filePath = fileStorageService.uploadFile(file, "ads/" + adId);
            AdImage image = AdImage.builder()
                    .ad(ad)
                    .imageUrl(filePath)
                    .displayOrder(displayOrder++)
                    .build();
            adImageRepository.save(image);
        }

        return mapToDetailDto(ad);
    }

    @Transactional
    public void deleteAdImage(Long adId, Long imageId) {
        AdImage image = adImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        if (!image.getAd().getId().equals(adId)) {
            throw new RuntimeException("Image does not belong to this ad");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!image.getAd().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this image");
        }

        try {
            fileStorageService.deleteFile(image.getImageUrl());
        } catch (IOException e) {
            log.error("Failed to delete image file: {}", e.getMessage());
        }

        adImageRepository.deleteById(imageId);
    }

    private AdDetailDto mapToDetailDto(ClassifiedAd ad) {
        List<String> imageUrls = adImageRepository.findByAdIdOrderByDisplayOrder(ad.getId())
                .stream()
                .map(AdImage::getImageUrl)
                .collect(Collectors.toList());

        return AdDetailDto.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .description(ad.getDescription())
                .price(ad.getPrice())
                .category(ad.getCategory())
                .status(ad.getStatus())
                .latitude(ad.getLatitude())
                .longitude(ad.getLongitude())
                .locationName(ad.getLocationName())
                .viewCount(ad.getViewCount())
                .createdAt(ad.getCreatedAt())
                .updatedAt(ad.getUpdatedAt())
                .userId(ad.getUser().getId())
                .userName(ad.getUser().getFullName())
                .userEmail(ad.getUser().getEmail())
                .userPhone(ad.getUser().getPhoneNumber())
                .userProfileImage(ad.getUser().getProfileImageUrl())
                .imageUrls(imageUrls)
                .build();
    }

    private AdMapPointDto mapToMapPointDto(ClassifiedAd ad) {
        String thumbnailUrl = adImageRepository.findByAdIdOrderByDisplayOrder(ad.getId())
                .stream()
                .findFirst()
                .map(AdImage::getImageUrl)
                .orElse(null);

        return AdMapPointDto.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .latitude(ad.getLatitude())
                .longitude(ad.getLongitude())
                .locationName(ad.getLocationName())
                .category(ad.getCategory())
                .thumbnailUrl(thumbnailUrl)
                .userId(ad.getUser().getId())
                .userName(ad.getUser().getFullName())
                .build();
    }
}
