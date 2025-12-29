package ru.mifi.practice.voln.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Entity
@Table(name = "authorities", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "authority"}))
public class AuthorityEntity implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authority_id_seq")
    @SequenceGenerator(name = "authority_id_seq", sequenceName = "authority_id_seq", allocationSize = 1)
    private Long id;
    @Column(name = "authority")
    private String authority;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;
}
