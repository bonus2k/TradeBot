package com.example.tradebot.domain;

import com.example.tradebot.annotation.PasswordValueMatch;
import com.example.tradebot.annotation.UniqueEmail;
import com.example.tradebot.annotation.UniqueUsername;
import com.example.tradebot.annotation.ValidPassword;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Set;
@PasswordValueMatch.List({
        @PasswordValueMatch(
                field = "password",
                fieldMatch = "confirmPassword",
                message = "Пароли не совподают!!"
        )
})


@Entity
@Table(name = "usr", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})

@Getter @Setter @NoArgsConstructor @ToString
@UniqueEmail
@UniqueUsername
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Length(min=5, max=24, message = "Имя должно содержать от 5 до 24 символов")
    @Column(name = "username", unique = true)
    private String username;

    @ValidPassword
    @NonNull
    @NotBlank
    private String password;

    @ValidPassword
    @NonNull
    @NotBlank
    private String confirmPassword;

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
    @Column(name = "email", unique = true)
    private String email;
    private String activationCode;
    private boolean isCanTrade;

    @Column(length = 2048)
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
