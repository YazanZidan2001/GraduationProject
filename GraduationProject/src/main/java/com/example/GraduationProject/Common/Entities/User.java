package com.example.GraduationProject.Common.Entities;

import com.example.GraduationProject.Common.Enums.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @Column(name = "UserID")
    @JsonProperty("userID") // Maps the JSON property to the Java field
    private Long UserID;

    @Column(name = "email", unique = true, nullable = false)
    @NotNull(message = "Email cannot be blank")
    private String email;

    @Column(name = "password", nullable = false)
    @NotNull(message = "Password cannot be blank")
    private String password;

    @Column(name = "firstName", nullable = false)
    @NotNull(message = "First name cannot be blank")
    private String firstName;

    @Column(name = "lastName", nullable = false)
    @NotNull(message = "Last name cannot be blank")
    private String lastName;

    @Column(name = "phone", nullable = false)
    @NotNull(message = "Phone cannot be blank")
    private String phone;

    @Column(name = "dateOfBirth", nullable = false)
    @NotNull(message = "Date of birth cannot be blank")
    private LocalDate dateOfBirth;

    @Column(name = "isActive")
    @JsonIgnore
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "isDeleted")
    @JsonIgnore
    @Builder.Default
    private boolean isDeleted = false;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<VerificationCode> verificationCodes;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Token> tokens;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference("doctorUser")
    private Doctor doctor;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference("patientUser")
    private Patient patient;

    // New field for 2FA preference
    @Column(name = "preferred2faMethod", nullable = true)
    private String preferred2faMethod; // "email" or "phone"

    @Column(name = "photo_path", nullable = true)
    private String photoPath;


    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
