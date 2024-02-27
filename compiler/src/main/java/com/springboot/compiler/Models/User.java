

package com.springboot.compiler.Models;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
@DynamicInsert
@DynamicUpdate
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails{
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private int userId;


    @Column(name="username",length = 50,unique = true,nullable = false)
    private String username;

    @Column(nullable = false,unique = true)
    private String email;

    @JsonIgnore
    private String password;

    @Column(name="created_date")
    @Temporal(TemporalType.DATE)
    private Date createdDate;

    @Column(length = 20)
    private String gender;
    

    @Column(name="first_name",nullable = false,length = 70)
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="faviourate_language",length=20)
    private String favLanguage;

    @Column(name="mark_for_deletion",columnDefinition = "tinyint(1) DEFAULT 0")
    private int makrForDeletion;

    //user submmisions
    @OneToMany(
        cascade =CascadeType.ALL,
        fetch = FetchType.EAGER,
        mappedBy = "username" 
    )
    private Set<Submissions> submissions;


    //user submmisions
    @ManyToMany(
        cascade = CascadeType.REMOVE,
        fetch = FetchType.EAGER
    )
    @JoinTable(
        name="user_roles",
        joinColumns = {
            @JoinColumn(
                name="username",
                referencedColumnName = "username"
            )
        },
        inverseJoinColumns = {
            @JoinColumn(
                name="role_id",
                referencedColumnName ="role_id"
            )
        }
    )
    private Set<Roles> roles;


    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getSimpleGrantedAuthorities();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
    public Set<SimpleGrantedAuthority> getSimpleGrantedAuthorities(){
        return authorities;
    }
    @Transient
    Set<SimpleGrantedAuthority> authorities;
}
