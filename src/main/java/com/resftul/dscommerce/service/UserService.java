package com.resftul.dscommerce.service;


import com.resftul.dscommerce.dto.user.UserDTO;
import com.resftul.dscommerce.dto.user.UserInsertDTO;
import com.resftul.dscommerce.dto.user.UserUpdateDTO;
import com.resftul.dscommerce.entity.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserDTO> findAllPaged(Pageable pageable);

    @Transactional
    UserDTO insert(@Valid UserInsertDTO userInsertDTO);

    UserDTO findById(Long id);

    @Transactional
    UserDTO update(Long id, @Valid UserUpdateDTO userInsertDTO);

    User authenticated();
}
