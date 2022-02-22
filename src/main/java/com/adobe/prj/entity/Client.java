package com.adobe.prj.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Table(name = "clients")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Client {
    @NotBlank(message="Name cannot be null")
    @Column(unique = true)
    private String name;

    private String nick;

    @NotBlank(message="email cannot be null")
    @Column(unique = true)
    @Id
    private String email;

    private String website;

    @Min(value=0)
    @Builder.Default
    private double billingRate = 0.0;

    private String address;
    private String country;
    private String city;
    private String state;
    private String zipCode;
    private String telephone;
    private String fax;

    @Builder.Default
    private boolean isActive = true;

    @Builder.Default
    @JsonIgnore
    private boolean isDeleted = false;
}
