package com.resftul.dscommerce.repository;

import com.resftul.dscommerce.entity.Users;
import com.resftul.dscommerce.projections.UserDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    @Query(value = """
            SELECT u.email     AS username,
                   u.password  AS password,
                   r.id        AS rolesId,
                   r.authority AS authority
            FROM tb_users u
            JOIN tb_users_roles ur ON u.id = ur.users_id
            JOIN tb_roles r       ON r.id = ur.roles_id
            WHERE u.email = :email
            """, nativeQuery = true)
    List<UserDetailsProjection> searchUserAndRolesByEmail(String email);
}
