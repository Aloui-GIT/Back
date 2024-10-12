package com.example.generateurformulaire.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@ToString
public class Media implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long mediaId;

    @Column(name = "name")
    String name;

    @Column(name = "imagenUrl")
    String imagenUrl;

    @Column(name = "codeImage")
    String codeImage;

    public Media(String name, String imagenUrl, String imagencode) {

        this.name = name;
        this.imagenUrl = imagenUrl;
        this.codeImage = imagencode;
    }


}
