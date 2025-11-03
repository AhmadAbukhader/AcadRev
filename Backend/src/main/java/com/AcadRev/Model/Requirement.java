package com.AcadRev.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Requirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    // Each requirement belongs to one section
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    // Optional: If you want to see which documents belong to this requirement
    @JsonManagedReference
    @OneToMany(mappedBy = "requirement", cascade = CascadeType.ALL)
    private List<Document> documents;

}
