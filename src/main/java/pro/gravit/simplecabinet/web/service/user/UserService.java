package pro.gravit.simplecabinet.web.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import pro.gravit.simplecabinet.web.exception.InvalidParametersException;
import pro.gravit.simplecabinet.web.model.user.BasicUser;
import pro.gravit.simplecabinet.web.model.user.HardwareId;
import pro.gravit.simplecabinet.web.model.user.User;
import pro.gravit.simplecabinet.web.model.user.UserGroup;
import pro.gravit.simplecabinet.web.repository.user.UserRepository;
import pro.gravit.simplecabinet.web.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class UserService {
    private final Pattern USERNAME_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");
    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordCheckService passwordCheckService;

    public <S extends User> S save(S entity) {
        return repository.save(entity);
    }


    public Optional<User> findById(Long aLong) {
        return repository.findById(aLong);
    }

    public Optional<User> findByIdFetchAssets(Long id) {
        return repository.findByIdFetchAssets(id);
    }

    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public Optional<User> findByUUID(UUID uuid) {
        return repository.findByUuid(uuid);
    }


    public Optional<User> findByUsernameFetchAssets(String username) {
        return repository.findByUsernameFetchAssets(username);
    }

    public Optional<User> findByUuidFetchAssets(UUID uuid) {
        return repository.findByUuidFetchAssets(uuid);
    }

    public Page<User> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public User getReference(Long aLong) {
        return repository.getById(aLong);
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<User> findByHardwareId(HardwareId hardwareId) {
        return repository.findByHardwareId(hardwareId);
    }

    public List<User> findByHardwareIdFetchAssets(HardwareId hardwareId) {
        return repository.findByHardwareIdFetchAssets(hardwareId);
    }

    public void delete(User entity) {
        repository.delete(entity);
    }

    public User register(String username, String email, String password) {
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new InvalidParametersException("Username contains forbidden characters", 33);
        }
        User user = new User();
        user.setUsername(username);
        user.setUuid(UUID.randomUUID());
        user.setEmail(email);
        passwordCheckService.setPassword(user, password);
        user.setRegistrationDate(LocalDateTime.now());
        user.setGroups(new ArrayList<>());
        repository.save(user);
        return user;
    }

    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return repository.findByUsernameOrEmail(usernameOrEmail);
    }

    public CurrentUser getCurrentUser() {
        var details = SecurityUtils.getUser();
        return new CurrentUser(details);
    }

    public List<UserGroup> getUserGroups(User user) {
        var currentTime = LocalDateTime.now();
        return user.getGroups().stream().filter((g) -> g.getEndDate() == null || g.getEndDate().isAfter(currentTime)).toList();
    }

    public class CurrentUser implements BasicUser {
        private final UserDetailsService.CabinetUserDetails details;
        private User user;

        public CurrentUser(UserDetailsService.CabinetUserDetails details) {
            this.details = details;
        }

        public User getReference() {
            if (user == null) {
                user = repository.getById(getId());
            }
            return user;
        }

        public String getClient() {
            return details.getClient();
        }

        public long getSessionId() {
            return details.getSessionId();
        }

        public String getPermission(String key) {
            var map = details.getPermissions();
            return map.get(key);
        }

        public Collection<? extends GrantedAuthority> getAuthorities() {
            return details.getAuthorities();
        }

        @Override
        public long getId() {
            return details.getUserId();
        }

        public String getUsername() {
            return details.getUsername();
        }

        @Override
        public UUID getUuid() {
            return null;
        }
    }
}
