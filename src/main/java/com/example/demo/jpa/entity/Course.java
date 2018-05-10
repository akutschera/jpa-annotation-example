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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Andreas Kutschera.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = { "id" })
public class Course {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "student_course", joinColumns = @JoinColumn(name = "student_id")
            , inverseJoinColumns = @JoinColumn(name = "course_id"))
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

    public void removeStudent( Student student ) {
        boolean wasRemoved = students.remove( student );
        if ( wasRemoved ) {
            student.removeCourse( this );
        }
    }
}
