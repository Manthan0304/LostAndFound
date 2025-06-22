package com.lostandfound.repository;

import com.lostandfound.entity.LostItem;
import com.lostandfound.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LostItemRepository extends JpaRepository<LostItem, Long> {
    Page<LostItem> findByStatusOrderByCreatedAtDesc(LostItem.ItemStatus status, Pageable pageable);
    
    List<LostItem> findByUserOrderByCreatedAtDesc(User user);
    
    List<LostItem> findByClaimedByUserOrderByClaimedAtDesc(User user);
    
    @Query("SELECT l FROM LostItem l WHERE l.status = :status AND " +
           "(LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<LostItem> findByStatusAndKeywordOrderByCreatedAtDesc(
        @Param("status") LostItem.ItemStatus status, 
        @Param("keyword") String keyword, 
        Pageable pageable
    );
}
