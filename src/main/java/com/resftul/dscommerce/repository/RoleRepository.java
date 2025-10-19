package com.resftul.dscommerce.repository;

import com.resftul.dscommerce.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("roleRepository")
public interface RoleRepository extends JpaRepository<Roles, Long> {
}
