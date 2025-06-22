package com.lostandfound.service;

import com.lostandfound.dto.*;
import com.lostandfound.entity.FoundItem;
import com.lostandfound.entity.User;
import com.lostandfound.repository.FoundItemRepository;
import com.lostandfound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FoundItemService {
    
    private final FoundItemRepository foundItemRepository;
    private final UserRepository userRepository;
    
    public ItemResponse createFoundItem(FoundItemRequest request, Long userId) throws IOException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        FoundItem foundItem = new FoundItem();
        foundItem.setTitle(request.getTitle());
        foundItem.setDescription(request.getDescription());
        foundItem.setCategory(request.getCategory());
        foundItem.setFoundLocation(request.getFoundLocation());
        foundItem.setFoundDate(request.getFoundDate());
        foundItem.setContactInfo(request.getContactInfo());
        foundItem.setUser(user);
        
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            foundItem.setImageData(request.getImage().getBytes());
            foundItem.setImageName(request.getImage().getOriginalFilename());
            foundItem.setImageType(request.getImage().getContentType());
        }
        
        FoundItem saved = foundItemRepository.save(foundItem);
        return convertToResponse(saved);
    }

    public Page<ItemResponse> getAllActiveFoundItems(Pageable pageable) {
        Page<FoundItem> page = foundItemRepository.findByStatusOrderByCreatedAtDesc(
                FoundItem.ItemStatus.ACTIVE, pageable);

        if (page.getTotalPages() > 0 && pageable.getPageNumber() >= page.getTotalPages()) {
            return Page.empty(pageable);
        }

        return page.map(this::convertToResponse);
    }
    
    public Page<ItemResponse> searchFoundItems(String keyword, Pageable pageable) {
        return foundItemRepository.findByStatusAndKeywordOrderByCreatedAtDesc(
            FoundItem.ItemStatus.ACTIVE, keyword, pageable)
            .map(this::convertToResponse);
    }
    
    public List<ItemResponse> getUserFoundItems(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return foundItemRepository.findByUserOrderByCreatedAtDesc(user)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public List<ItemResponse> getUserClaimedFoundItems(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return foundItemRepository.findByClaimedByUserOrderByClaimedAtDesc(user)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public ItemResponse claimFoundItem(Long itemId, Long claimerId, ClaimRequest request) {
        FoundItem foundItem = foundItemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Found item not found"));
        
        if (foundItem.getStatus() != FoundItem.ItemStatus.ACTIVE) {
            throw new RuntimeException("Item is not available for claiming");
        }
        
        User claimer = userRepository.findById(claimerId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        foundItem.setClaimedByUser(claimer);
        foundItem.setClaimedAt(LocalDateTime.now());
        foundItem.setStatus(FoundItem.ItemStatus.CLAIMED);
        
        FoundItem saved = foundItemRepository.save(foundItem);
        return convertToResponse(saved);
    }
    
    public ItemResponse getFoundItemById(Long id) {
        FoundItem foundItem = foundItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Found item not found"));
        return convertToResponse(foundItem);
    }
    
    public byte[] getItemImage(Long id) {
        FoundItem foundItem = foundItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Found item not found"));
        return foundItem.getImageData();
    }
    
    private ItemResponse convertToResponse(FoundItem foundItem) {
        return ItemResponse.builder()
            .id(foundItem.getId())
            .title(foundItem.getTitle())
            .description(foundItem.getDescription())
            .category(foundItem.getCategory())
            .location(foundItem.getFoundLocation())
            .date(foundItem.getFoundDate())
            .contactInfo(foundItem.getContactInfo())
            .status(foundItem.getStatus().toString())
            .ownerUsername(foundItem.getUser().getUsername())
            .ownerEmail(foundItem.getUser().getEmail())
            .claimedByUsername(foundItem.getClaimedByUser() != null ? foundItem.getClaimedByUser().getUsername() : null)
            .claimedByEmail(foundItem.getClaimedByUser() != null ? foundItem.getClaimedByUser().getEmail() : null)
            .claimedAt(foundItem.getClaimedAt())
            .createdAt(foundItem.getCreatedAt())
            .imageUrl(foundItem.getImageData() != null ? "/api/found-items/" + foundItem.getId() + "/image" : null)
            .build();
    }
}
