package com.example.projectschedulehaircutserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee")
public class Employee extends Account {
    public enum EmployeeType {
        HAIR_STYLIST_STAFF,
        SPA_STAFF
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeType employeeType;

    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToMany(mappedBy = "employees", fetch = FetchType.LAZY)
    private Set<Orders> orders = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee")
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee")
    private Set<WorkDone> workDone = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "employee_time",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "time_id")
    )
    private Set<Time> times = new HashSet<>();
}
