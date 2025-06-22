package com.lostandfound.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class LostItemRequest {
    private String title;
    private String description;
    private String category;
    private String lostLocation;
    private LocalDateTime lostDate;
    private String contactInfo;
    private MultipartFile image;
}
