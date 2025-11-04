package com.resftul.dscommerce.service;

import com.resftul.dscommerce.dto.user.UserDTO;
import com.resftul.dscommerce.dto.user.UserInsertDTO;
import com.resftul.dscommerce.entity.Role;
import com.resftul.dscommerce.entity.User;
import com.resftul.dscommerce.exception.DuplicateEntryException;
import com.resftul.dscommerce.exception.ResourceNotFoundException;
import com.resftul.dscommerce.projections.UserDetailsProjection;
import com.resftul.dscommerce.repository.RoleRepository;
import com.resftul.dscommerce.repository.UserRepository;
import com.resftul.dscommerce.service.impl.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import jakarta.validation.ValidationException;

import java.time.LocalDate;
import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @AfterEach
    void cleanupSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private static Role role() {
        return new Role(
                1L,
                "ROLE_CLIENT"
        );
    }

    private static User user(Long id, String name, String email) {
        return new User(
                id,
                name,
                email,
                "+5519999999999",
                LocalDate.of(2000, 1, 1),
                "{enc}pwd"
        );
    }

    private static UserInsertDTO insertDTO(String name, String email) {
        return new UserInsertDTO(
                name,
                email,
                "+5519999999999",
                "Aa1!aaaa",
                LocalDate.of(2000, 1, 1)
        );
    }

    private static void setAuthName(String email) {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn(email);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private static JwtAuthenticationToken jwtWithUsernameClaim(String email, String... roles) {
        Jwt jwt = Jwt.withTokenValue("tok")
                .header("alg", "none")
                .subject("ignored-subject")
                .claim("username", email)
                .build();
        List<GrantedAuthority> auths = Arrays.stream(roles)
                .map(SimpleGrantedAuthority::new)
                .collect(toList());
        return new JwtAuthenticationToken(jwt, auths);
    }


    private static void setAuth(Authentication a) {
        SecurityContextHolder.getContext().setAuthentication(a);
    }

    private static Page<User> pageOfUsers() {
        User user1 = user(10L, "Ana", "ana@example.com");
        user1.getRoles().add(role());
        User user2 = user(11L, "Bruno", "bruno@example.com");
        user2.getRoles().add(role());
        return new PageImpl<>(List.of(user1, user2), PageRequest.of(0, 2), 2);
    }

    private record Row(String username, String password, Long roleId, String authority)
            implements UserDetailsProjection {
        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public Long getRoleId() {
            return roleId;
        }

        @Override
        public String getAuthority() {
            return authority;
        }
    }

    @Test
    @DisplayName("loadUserByUsername: monta UserDetails com todas as roles")
    void loadUserByUsername_ok() {
        List<UserDetailsProjection> rows = List.of(
                new Row("carol@example.com", "{enc}secret", 3L, "ROLE_CLIENT"),
                new Row("carol@example.com", "{enc}secret", 4L, "ROLE_ADMIN")
        );
        when(userRepository.searchUserAndRolesByEmail("carol@example.com")).thenReturn(rows);

        UserDetails ud = userService.loadUserByUsername("carol@example.com");

        assertThat(ud.getUsername()).isEqualTo("carol@example.com");
        assertThat(ud.getPassword()).isEqualTo("{enc}secret");
        assertThat(ud.getAuthorities()).extracting("authority")
                .containsExactlyInAnyOrder("ROLE_CLIENT", "ROLE_ADMIN");
        verify(userRepository).searchUserAndRolesByEmail("carol@example.com");
    }

    @Test
    @DisplayName("loadUserByUsername: lança UsernameNotFoundException quando não há linhas")
    void loadUserByUsername_notFound() {
        when(userRepository.searchUserAndRolesByEmail("x@x")).thenReturn(emptyList());
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("x@x"));
    }

    @Test
    @DisplayName("findAllPaged: mapeia Page<User> -> Page<UserDTO>")
    void findAllPaged_ok() {
        Page<User> page = pageOfUsers();
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<UserDTO> out = userService.findAllPaged(PageRequest.of(0, 2));

        assertThat(out.getTotalElements()).isEqualTo(2);
        assertThat(out.getContent()).hasSize(2);
        assertThat(out.getContent().get(0).getId()).isEqualTo(10L);
        assertThat(out.getContent().get(1).getEmail()).isEqualTo("bruno@example.com");
        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("findById: retorna DTO quando id existe")
    void findById_ok() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(user(10L, "Ana", "ana@example.com")));
        UserDTO out = userService.findById(10L);
        assertThat(out.getId()).isEqualTo(10L);
        assertThat(out.getEmail()).isEqualTo("ana@example.com");
    }

    @Test
    @DisplayName("findById: lança ResourceNotFoundException quando id não existe")
    void findById_notFound() {
        when(userRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.findById(404L));
    }

    @Test
    @DisplayName("insert: persiste com email normalizado, senha codificada e ROLE_CLIENT")
    void insert_ok() {
        var in = insertDTO("Carol", "  CAROL@Example.com  ");
        when(userRepository.findByEmail("carol@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Aa1!aaaa")).thenReturn("{bcrypt}hash");
        Role client = role();
        when(roleRepository.findByAuthority("ROLE_CLIENT")).thenReturn(Optional.of(client));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(100L);
            return u;
        });

        UserDTO out = userService.insert(in);

        assertThat(out.getId()).isEqualTo(100L);
        assertThat(out.getEmail()).isEqualTo("carol@example.com");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getPassword()).isEqualTo("{bcrypt}hash");
        assertThat(saved.getRoles()).extracting("authority").containsExactly("ROLE_CLIENT");

        verify(userRepository).findByEmail("carol@example.com");
        verify(passwordEncoder).encode("Aa1!aaaa");
        verify(roleRepository).findByAuthority("ROLE_CLIENT");
    }

    @Test
    @DisplayName("insert: lança DuplicateEntryException quando email já existe (pré-check)")
    void insert_duplicate_precheck() {
        var in = insertDTO("Ana", "ana@example.com");
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user(1L, "x", "ana@example.com")));

        assertThrows(DuplicateEntryException.class, () -> userService.insert(in));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("insert: DataIntegrityViolation é traduzida para DuplicateEntryException")
    void insert_integrity_translated() {
        var in = insertDTO("Dave", "dave@example.com");
        when(userRepository.findByEmail("dave@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("{e}");
        when(roleRepository.findByAuthority("ROLE_CLIENT")).thenReturn(Optional.of(role()));
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("uk"));

        assertThrows(DuplicateEntryException.class, () -> userService.insert(in));
    }

    @Test
    @DisplayName("insert: email nulo -> ValidationException (normalização)")
    void insert_null_email_validation() {
        var in = new UserInsertDTO(
                "X",
                null,
                "+5519999999999",
                "Aa1!aaaa",
                LocalDate.of(2000, 1, 1)
        );
        assertThrows(ValidationException.class, () -> userService.insert(in));
    }

    @Test
    @DisplayName("authenticated: resolve pelo Authentication.getName()")
    void authenticated_byName_ok() {
        setAuthName("ana@example.com");
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user(10L, "Ana", "ana@example.com")));

        User me = userService.authenticated();

        assertThat(me.getEmail()).isEqualTo("ana@example.com");
        verify(userRepository).findByEmail("ana@example.com");
    }

    @Test
    @DisplayName("authenticated: resolve pelo claim 'username' de JwtAuthenticationToken")
    void authenticated_byJwtClaim_ok() {
        setAuth(jwtWithUsernameClaim("bob@example.com", "ROLE_CLIENT"));
        when(userRepository.findByEmail("bob@example.com"))
                .thenReturn(Optional.of(user(20L, "Bob", "bob@example.com")));

        User me = userService.authenticated();

        assertThat(me.getId()).isEqualTo(20L);
        verify(userRepository).findByEmail("bob@example.com");
    }

    @Test
    @DisplayName("authenticated: sem Authentication ou não autenticado -> AuthenticationCredentialsNotFoundException")
    void authenticated_noAuth_throws() {
        SecurityContextHolder.clearContext();
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> userService.authenticated());
    }

    @Test
    @DisplayName("getMe: retorna DTO do usuário autenticado via getName()")
    void getMe_byName_ok() {
        setAuthName("carol@example.com");
        when(userRepository.findByEmail("carol@example.com")).thenReturn(Optional.of(user(30L, "Carol", "carol@example.com")));

        UserDTO out = userService.getMe();

        assertThat(out.getId()).isEqualTo(30L);
        assertThat(out.getEmail()).isEqualTo("carol@example.com");
    }

    @Test
    @DisplayName("getMe: resolve via JwtAuthenticationToken claim 'username'")
    void getMe_byJwtClaim_ok() {
        setAuth(jwtWithUsernameClaim("dave@example.com", "ROLE_CLIENT"));
        when(userRepository.findByEmail("dave@example.com"))
                .thenReturn(Optional.of(user(40L, "Dave", "dave@example.com")));

        UserDTO out = userService.getMe();

        assertThat(out.getName()).isEqualTo("Dave");
        assertThat(out.getEmail()).isEqualTo("dave@example.com");
    }

    @Test
    @DisplayName("getMe: usuário não encontrado após resolver identidade -> ResourceNotFoundException")
    void getMe_notFound() {
        setAuthName("ghost@example.com");
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getMe());
    }
}
