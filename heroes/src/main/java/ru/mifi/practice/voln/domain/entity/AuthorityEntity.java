package ru.mifi.practice.voln.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Entity
@Table(name = "authorities")
@IdClass(AuthorityEntity.PK.class)
public class AuthorityEntity implements GrantedAuthority {
    @Id
    @Column(name = "user_id")
    private UUID userId;
    @Id
    private String authority;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @EqualsAndHashCode
    @Embeddable
    public static final class PK implements Serializable {
        private UUID userId;
        private String authority;
    }
}
