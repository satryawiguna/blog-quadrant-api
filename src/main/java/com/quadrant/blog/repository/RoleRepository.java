package com.quadrant.blog.repository;

import com.quadrant.blog.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer>  {

    RoleEntity findByName(String name);

}
