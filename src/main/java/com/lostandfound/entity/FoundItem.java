package com.lostandfound.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "found_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FoundItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String category;
    
    @Column(name = "found_location")
    private String foundLocation;
    
    @Column(name = "found_date")
    private LocalDateTime foundDate;
    
    @Column(name = "contact_info")
    private String contactInfo;
    
    @Enumerated(EnumType.STRING)
    private ItemStatus status = ItemStatus.ACTIVE;
    
    @Lob
    @Column(name = "image_data")
    private byte[] imageData;
    
    @Column(name = "image_name")
    private String imageName;
    
    @Column(name = "image_type")
    private String imageType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimed_by_user_id")
    private User claimedByUser;
    
    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;
    
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum ItemStatus {
        ACTIVE, CLAIMED, RESOLVED
    }
}
