package com.example.demo.jpa.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PreRemove;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Andreas Kutschera.
 */
@Entity
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>( 2 );

    public void addStudent( Student student ) {
        if ( hasStudentAlready( student ) ) {
            return;
        }
        if ( student != null ) {
            students.add( student );
            student.addCourse( this );
        }
    }

    private boolean hasStudentAlready( Student student ) {
        return students.contains( student );
    }

    @PreRemove
    private void deleteCourse() {
        students.forEach( x -> x.removeCourse( this  ) );
    }

    public void removeStudent( Student student ) {
        boolean wasRemoved = students.remove( student );
        if ( wasRemoved ) {
            student.removeCourse( this );
        }
    }
}
