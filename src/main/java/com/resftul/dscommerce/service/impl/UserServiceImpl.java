package com.resftul.dscommerce.service.impl;

import com.resftul.dscommerce.dto.user.UserDTO;
import com.resftul.dscommerce.dto.user.UserInsertDTO;
import com.resftul.dscommerce.entity.Role;
import com.resftul.dscommerce.entity.User;
import com.resftul.dscommerce.exception.DuplicateEntryException;
import com.resftul.dscommerce.exception.ResourceNotFoundException;
import com.resftul.dscommerce.projections.UserDetailsProjection;
import com.resftul.dscommerce.repository.RoleRepository;
import com.resftul.dscommerce.repository.UserRepository;
import com.resftul.dscommerce.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static java.util.Locale.ROOT;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private static final String ROLE_CLIENT_AUTHORITY = "ROLE_CLIENT";

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            RoleRepository roleRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository   = userRepository;
        this.roleRepository   = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> results = userRepository.searchUserAndRolesByEmail(username);
        if (results.isEmpty()) throw new UsernameNotFoundException("Email not found: " + username);

        User user = new User();
        user.setEmail(results.get(0).getUsername());
        user.setPassword(results.get(0).getPassword());
        results.forEach(p -> user.addRole(new Role(p.getRoleId(), p.getAuthority())));
        return user;
    }

    @Override
    public Page<UserDTO> findAllPaged(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserDTO::new);
    }

    @Override
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new UserDTO(user);
    }

    @Transactional
    @Override
    public UserDTO insert(@Valid UserInsertDTO userInsertDTO) {
        final String normalizedEmail = normalizeEmail(userInsertDTO.email());

        if (userRepository.findByEmail(normalizedEmail).isPresent())
            throw new DuplicateEntryException("Email already exists: " + normalizedEmail);

        try {
            User entity = new User();
            entity.setName(userInsertDTO.name());
            entity.setEmail(normalizedEmail);
            entity.setPhone(userInsertDTO.phone());
            entity.setBirthDate(userInsertDTO.birthDate());
            entity.setPassword(passwordEncoder.encode(userInsertDTO.password())); // senhas sempre codificadas

            Role client = roleRepository.findByAuthority(ROLE_CLIENT_AUTHORITY)
                    .orElseThrow(() -> new ResourceNotFoundException("Default role not found: " + ROLE_CLIENT_AUTHORITY));
            entity.getRoles().clear();
            entity.getRoles().add(client);

            userRepository.save(entity);
            return new UserDTO(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntryException("Email already exists: " + normalizedEmail);
        }
    }

//    @Override
//    @Transactional
//    public UserDTO update(final Long id, final UserUpdateDTO dto) {
//        final User entity = userRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//        final Authentication auth = requireAuthenticated();
//        final String requester = resolveRequesterIdentity(auth);
//        assertOwner(entity, requester);
//
//        final String normalizedEmail = normalizeEmail(dto.email());
//
//        validateEmailChange(entity, normalizedEmail, id);
//
//        entity.setName(dto.name());
//        entity.setEmail(normalizedEmail);
//        entity.setPhone(dto.phone());
//        entity.setBirthDate(dto.birthDate());
//
//        userRepository.save(entity);
//        return new UserDTO(entity);
//    }

    @Override
    public User authenticated() {
        final Authentication auth = requireAuthenticated();
        final String requester = resolveRequesterIdentity(auth);
        return userRepository.findByEmail(requester)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public UserDTO getMe() {
        final Authentication auth = requireAuthenticated();
        final String requester = resolveRequesterIdentity(auth);
        final User entity = userRepository.findByEmail(requester)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new UserDTO(entity);
    }

    private Authentication requireAuthenticated() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated())
            throw new AuthenticationCredentialsNotFoundException("No authentication");
        return auth;
    }

    private String resolveRequesterIdentity(Authentication auth) {
        if (auth instanceof JwtAuthenticationToken jwt) {
            Object username = jwt.getTokenAttributes().get("username");
            return (username != null) ? username.toString() : jwt.getName();
        }
        return auth.getName();
    }

    private void assertOwner(User user, String requesterIdentity) {
        String ownerEmail = user.getEmail();
        boolean isOwner = ownerEmail != null && ownerEmail.equalsIgnoreCase(requesterIdentity);
        if (!isOwner) throw new AccessDeniedException("You are not allowed to update this user");
    }

    private String normalizeEmail(String raw) {
        if (raw == null) throw new ValidationException("Email must not be null");
        return raw.trim().toLowerCase(ROOT);
    }

    private void validateEmailChange(User entity, String newEmail, Long id) {
        if (entity.getEmail() != null && entity.getEmail().equalsIgnoreCase(newEmail))
            throw new DuplicateEntryException("New email must be different from current");
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(newEmail, id))
            throw new DuplicateEntryException("Email already exists: " + newEmail);
    }
}
