package com.quadrant.blog.repository;

import com.quadrant.blog.entity.BlogEntity;
import com.quadrant.blog.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BlogRepository extends JpaRepository<BlogEntity, Long> {

    @Query("SELECT b FROM BlogEntity b WHERE b.title LIKE '%:search%' OR b.description LIKE '%:search%'")
    Page<BlogEntity> findByTitleOrDescriptionContains(String search, Pageable pageable);

}
