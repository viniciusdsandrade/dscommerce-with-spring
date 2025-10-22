package com.resftul.dscommerce.service;


import com.resftul.dscommerce.dto.user.UserDTO;
import com.resftul.dscommerce.dto.user.UserInsertDTO;
import com.resftul.dscommerce.dto.user.UserUpdateDTO;
import com.resftul.dscommerce.entity.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

    // ============ Queries ============
    Page<UserDTO> findAllPaged(Pageable pageable);

    UserDTO findById(Long id);

    // ============ Commands ============
    @Transactional
    UserDTO insert(@Valid UserInsertDTO dto);

    User authenticated();

    UserDTO getMe();
}
