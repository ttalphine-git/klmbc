package com.classifiedads.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdDetailDto {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private String status;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String locationName;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userProfileImage;
    private List<String> imageUrls;
}
