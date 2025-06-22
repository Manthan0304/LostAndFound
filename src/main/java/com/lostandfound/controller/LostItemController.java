package com.lostandfound.controller;

import com.lostandfound.dto.*;
import com.lostandfound.security.UserDetailsImpl;
import com.lostandfound.service.LostItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/lost-items")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LostItemController {

    private final LostItemService lostItemService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemResponse> createLostItem(@ModelAttribute LostItemRequest request) {
        try {
            ItemResponse response = lostItemService.createLostItem(request, getCurrentUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<ItemResponse>> getAllLostItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ItemResponse> items = lostItemService.getAllActiveLostItems(pageable);

        if (items.isEmpty() && page > 0) {
            return ResponseEntity.badRequest().body(Page.empty(pageable));
        }
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ItemResponse>> searchLostItems(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ItemResponse> items = lostItemService.searchLostItems(keyword, pageable);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getLostItemById(@PathVariable Long id) {
        ItemResponse item = lostItemService.getLostItemById(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getItemImage(@PathVariable Long id) {
        byte[] imageData = lostItemService.getItemImage(id);
        if (imageData == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return ResponseEntity.ok().headers(headers).body(imageData);
    }

    @PostMapping("/{id}/claim")
    public ResponseEntity<ItemResponse> claimLostItem(
            @PathVariable Long id,
            @RequestBody ClaimRequest request) {
        ItemResponse response = lostItemService.claimLostItem(id, getCurrentUserId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-items")
    public ResponseEntity<List<ItemResponse>> getUserLostItems() {
        List<ItemResponse> items = lostItemService.getUserLostItems(getCurrentUserId());
        return ResponseEntity.ok(items);
    }

    @GetMapping("/my-claims")
    public ResponseEntity<List<ItemResponse>> getUserClaimedItems() {
        List<ItemResponse> items = lostItemService.getUserClaimedLostItems(getCurrentUserId());
        return ResponseEntity.ok(items);
    }
}
