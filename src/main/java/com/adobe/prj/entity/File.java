package com.adobe.prj.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Table(name = "files")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class File {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fileId;

    private String fileName;

    private String fileType;

    @Lob
    @JsonIgnore
    private byte[] data;

}
