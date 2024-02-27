package com.springboot.compiler.Models;

import java.util.Date;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="submissions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Submissions {
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private int id;

    @Column(name="submission_id",length=50,unique = true,nullable = false)
    private String submissionId;

    @Column(length = 30)
    private String language;

    @Temporal(TemporalType.TIMESTAMP)
    private Date submissionDate;

    @Enumerated(EnumType.STRING)
    private Status status; 
    
    @Column(length = 20)
    private String runTime;
    

    @ManyToOne(
        cascade = CascadeType.ALL,
        fetch = FetchType.EAGER
    )
    @JoinColumn(
        name="username",
        referencedColumnName = "username"
    )
    private User username;

}
