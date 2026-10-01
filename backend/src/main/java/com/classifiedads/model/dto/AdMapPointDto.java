package com.classifiedads.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdMapPointDto {
    private Long id;
    private String title;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String locationName;
    private String category;
    private String thumbnailUrl;
    private Long userId;
    private String userName;
}
