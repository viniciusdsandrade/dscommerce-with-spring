package com.resftul.dscommerce.repository;

import com.resftul.dscommerce.entity.User;
import com.resftul.dscommerce.projections.UserDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query(value = """
            SELECT u.email     AS username,
                   u.password  AS password,
                   r.id        AS rolesId,
                   r.authority AS authority
            FROM tb_user u
            JOIN tb_user_role ur ON u.id = ur.user_id
            JOIN tb_role r       ON r.id = ur.role_id
            WHERE u.email = :email
            """, nativeQuery = true)
    List<UserDetailsProjection> searchUserAndRolesByEmail(String email);
}
