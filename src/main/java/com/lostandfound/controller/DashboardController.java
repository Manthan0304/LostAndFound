package com.lostandfound.controller;

import com.lostandfound.dto.ItemResponse;
import com.lostandfound.security.UserDetailsImpl;
import com.lostandfound.service.FoundItemService;
import com.lostandfound.service.LostItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final LostItemService lostItemService;
    private final FoundItemService foundItemService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Long userId = getCurrentUserId();

        List<ItemResponse> myLostItems = lostItemService.getUserLostItems(userId);
        List<ItemResponse> myFoundItems = foundItemService.getUserFoundItems(userId);
        List<ItemResponse> myLostClaims = lostItemService.getUserClaimedLostItems(userId);
        List<ItemResponse> myFoundClaims = foundItemService.getUserClaimedFoundItems(userId);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("myLostItems", myLostItems);
        dashboard.put("myFoundItems", myFoundItems);
        dashboard.put("myLostClaims", myLostClaims);
        dashboard.put("myFoundClaims", myFoundClaims);

        // Items where others have claimed my items (showing claimer's email)
        List<ItemResponse> claimedMyItems = myLostItems.stream()
                .filter(item -> "CLAIMED".equals(item.getStatus()))
                .toList();
        dashboard.put("claimedMyItems", claimedMyItems);

        List<ItemResponse> claimedMyFoundItems = myFoundItems.stream()
                .filter(item -> "CLAIMED".equals(item.getStatus()))
                .toList();
        dashboard.put("claimedMyFoundItems", claimedMyFoundItems);

        return ResponseEntity.ok(dashboard);
    }
}
