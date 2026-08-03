package ru.mifi.practice.voln.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@ToString(exclude = "authorities")
@Builder(toBuilder = true)
@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID id;
    @Column(name = "username", unique = true, updatable = false)
    private String username;
    private String password;
    private String email;
    private String phone;
    private String token;
    private boolean enabled;

    @Column(name = "telegram_id", unique = true)
    private Long telegramId;
    @Column(name = "telegram_username")
    private String telegramUsername;
    @Column(name = "telegram_phone")
    private String telegramPhone;
    @Column(name = "telegram_first_name")
    private String telegramFirstName;
    @Column(name = "telegram_last_name")
    private String telegramLastName;
    @OneToMany(fetch = FetchType.LAZY)
    private List<AuthorityEntity> authorities;

    public boolean isTelegramRegistered() {
        return StringUtils.hasText(telegramPhone);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
