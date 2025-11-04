package com.resftul.dscommerce.service;


import com.resftul.dscommerce.dto.user.UserDTO;
import com.resftul.dscommerce.dto.user.UserInsertDTO;
import com.resftul.dscommerce.entity.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

    @Transactional(readOnly = true)
    Page<UserDTO> findAllPaged(Pageable pageable);

    @Transactional(readOnly = true)
    UserDTO findById(Long id);

    @Transactional
    UserDTO insert(@Valid UserInsertDTO userInsertDTO);

    User authenticated();

    UserDTO getMe();
}
