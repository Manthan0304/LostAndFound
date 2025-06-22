package com.lostandfound.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class FoundItemRequest {
    private String title;
    private String description;
    private String category;
    private String foundLocation;
    private LocalDateTime foundDate;
    private String contactInfo;
    private MultipartFile image;
}
