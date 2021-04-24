package com.example.tradebot.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "usr")
@Getter @Setter @NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank (message = "Логин не может быть пустым")
    @Length(min=5, max=24, message = "Имя должно содержать от 5 до 24 символов")
    private String username;
    @NotBlank (message = "Пароль не может быть пустым")
    private String password;
    private boolean active;
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
    private String key;
    private String secret;
    @Column(name = "is_run")
    private boolean isRun;
    @ElementCollection(targetClass = Symbol.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_symbol", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Symbol> symbol;
    @Email (message = "Некорректный email")
    @NotBlank (message = "Email не может быть пустым")
    private String email;
    private String activationCode;
    private boolean isCanTrade;
    private String comment;
    private Double balance;



    public Double getBalance() {
        return (balance == null) ? 0.0 : balance;
    }
    public String isRunString() {
        return (isRun) ? "Yes" : "No";
    }
    public boolean isAdmin() {
        return roles.contains(Role.ADMIN);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

}
