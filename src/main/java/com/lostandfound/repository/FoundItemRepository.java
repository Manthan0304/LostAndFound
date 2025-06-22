package com.lostandfound.repository;

import com.lostandfound.entity.FoundItem;
import com.lostandfound.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoundItemRepository extends JpaRepository<FoundItem, Long> {
    Page<FoundItem> findByStatusOrderByCreatedAtDesc(FoundItem.ItemStatus status, Pageable pageable);
    
    List<FoundItem> findByUserOrderByCreatedAtDesc(User user);
    
    List<FoundItem> findByClaimedByUserOrderByClaimedAtDesc(User user);
    
    @Query("SELECT f FROM FoundItem f WHERE f.status = :status AND " +
           "(LOWER(f.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(f.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<FoundItem> findByStatusAndKeywordOrderByCreatedAtDesc(
        @Param("status") FoundItem.ItemStatus status, 
        @Param("keyword") String keyword, 
        Pageable pageable
    );
}
