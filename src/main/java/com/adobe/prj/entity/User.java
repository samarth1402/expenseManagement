package com.adobe.prj.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Table(name = "users")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @NotBlank(message="Name cannot be null")
    @Column(unique = true)
    private String name ;

    @NotBlank(message="email cannot be null")
    @Id
    private String emailId ;

    @Value("password")
    @JsonIgnore
    private String password ;

    @Builder.Default
    @JsonIgnore
    private boolean isNewUser = true;

    private boolean isManager ;

    @Builder.Default
    @JsonIgnore
    private boolean isDeleted = false;
}
