package com.baha.security.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data // This would create getters ,setters and a required args constructor
@Builder // This makes the creation of an object of this class easier
@NoArgsConstructor
@AllArgsConstructor
@Entity // This makes the current class an entity in our database
@Table(name = "_user") /* By default, the Entity annotation would take the name of the class as the name of the entity
                        But in our case , 'user' is reserved in postgresql so we use this annotation to change the
                        name of the entity to a custom one .*/

public class User implements UserDetails {
    @Id // This would set the id as the primary key
    @GeneratedValue // This would make the id auto generated it take by default 'auto' parameter
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return true;
    }
}
