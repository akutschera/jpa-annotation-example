package com.example.demo.jpa.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Andreas Kutschera.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public class GraduationClass {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "graduationClass", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<Student> students = new HashSet<>( 2 );

    public void addStudent( Student student ) {
        students.add( student );
        student.setGraduationClass( this );
    }

    // FIXME: hibernate persistentSet remove does not work right now.
    public void removeStudent( Student student ) {
        boolean wasRemoved = students.remove( student );
        System.out.println("was " + wasRemoved);
        student.setGraduationClass( null );
    }
}
