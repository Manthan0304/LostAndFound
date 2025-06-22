package com.lostandfound.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String location;
    private LocalDateTime date;
    private String contactInfo;
    private String status;
    private String ownerUsername;
    private String ownerEmail;
    private String claimedByUsername;
    private String claimedByEmail;
    private LocalDateTime claimedAt;
    private LocalDateTime createdAt;
    private String imageUrl;
}
