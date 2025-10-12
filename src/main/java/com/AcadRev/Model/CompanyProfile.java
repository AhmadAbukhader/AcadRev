package com.AcadRev.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "companyProfile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "userId", nullable = false, unique = true)
    private Users user;

    @Column(nullable = false)
    private String name;

    private String address;

    private String industry;

    private String phone;

}
