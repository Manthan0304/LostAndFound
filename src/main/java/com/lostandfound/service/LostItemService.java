package com.lostandfound.service;

import com.lostandfound.dto.*;
import com.lostandfound.entity.LostItem;
import com.lostandfound.entity.User;
import com.lostandfound.repository.LostItemRepository;
import com.lostandfound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LostItemService {
    
    private final LostItemRepository lostItemRepository;
    private final UserRepository userRepository;
    
    public ItemResponse createLostItem(LostItemRequest request, Long userId) throws IOException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        LostItem lostItem = new LostItem();
        lostItem.setTitle(request.getTitle());
        lostItem.setDescription(request.getDescription());
        lostItem.setCategory(request.getCategory());
        lostItem.setLostLocation(request.getLostLocation());
        lostItem.setLostDate(request.getLostDate());
        lostItem.setContactInfo(request.getContactInfo());
        lostItem.setUser(user);
        
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            lostItem.setImageData(request.getImage().getBytes());
            lostItem.setImageName(request.getImage().getOriginalFilename());
            lostItem.setImageType(request.getImage().getContentType());
        }
        
        LostItem saved = lostItemRepository.save(lostItem);
        return convertToResponse(saved);
    }

    public Page<ItemResponse> getAllActiveLostItems(Pageable pageable) {
        Page<LostItem> page = lostItemRepository.findByStatusOrderByCreatedAtDesc(LostItem.ItemStatus.ACTIVE, pageable);

        if (page.getTotalPages() > 0 && pageable.getPageNumber() >= page.getTotalPages()) {
            return Page.empty(pageable);
        }

        return page.map(this::convertToResponse);
    }


    public Page<ItemResponse> searchLostItems(String keyword, Pageable pageable) {
        return lostItemRepository.findByStatusAndKeywordOrderByCreatedAtDesc(
            LostItem.ItemStatus.ACTIVE, keyword, pageable)
            .map(this::convertToResponse);
    }
    
    public List<ItemResponse> getUserLostItems(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return lostItemRepository.findByUserOrderByCreatedAtDesc(user)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public List<ItemResponse> getUserClaimedLostItems(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return lostItemRepository.findByClaimedByUserOrderByClaimedAtDesc(user)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public ItemResponse claimLostItem(Long itemId, Long claimerId, ClaimRequest request) {
        LostItem lostItem = lostItemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Lost item not found"));
        
        if (lostItem.getStatus() != LostItem.ItemStatus.ACTIVE) {
            throw new RuntimeException("Item is not available for claiming");
        }
        
        User claimer = userRepository.findById(claimerId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        lostItem.setClaimedByUser(claimer);
        lostItem.setClaimedAt(LocalDateTime.now());
        lostItem.setStatus(LostItem.ItemStatus.CLAIMED);
        
        LostItem saved = lostItemRepository.save(lostItem);
        return convertToResponse(saved);
    }
    
    public ItemResponse getLostItemById(Long id) {
        LostItem lostItem = lostItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Lost item not found"));
        return convertToResponse(lostItem);
    }
    
    public byte[] getItemImage(Long id) {
        LostItem lostItem = lostItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Lost item not found"));
        return lostItem.getImageData();
    }
    
    private ItemResponse convertToResponse(LostItem lostItem) {
        return ItemResponse.builder()
            .id(lostItem.getId())
            .title(lostItem.getTitle())
            .description(lostItem.getDescription())
            .category(lostItem.getCategory())
            .location(lostItem.getLostLocation())
            .date(lostItem.getLostDate())
            .contactInfo(lostItem.getContactInfo())
            .status(lostItem.getStatus().toString())
            .ownerUsername(lostItem.getUser().getUsername())
            .ownerEmail(lostItem.getUser().getEmail())
            .claimedByUsername(lostItem.getClaimedByUser() != null ? lostItem.getClaimedByUser().getUsername() : null)
            .claimedByEmail(lostItem.getClaimedByUser() != null ? lostItem.getClaimedByUser().getEmail() : null)
            .claimedAt(lostItem.getClaimedAt())
            .createdAt(lostItem.getCreatedAt())
            .imageUrl(lostItem.getImageData() != null ? "/api/lost-items/" + lostItem.getId() + "/image" : null)
            .build();
    }
}
