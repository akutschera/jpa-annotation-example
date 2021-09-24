package com.example.demo.jpa.entity;

import java.io.Serializable;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Andreas Kutschera.
 */
@Entity
@Getter
@Setter
public class Student implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(cascade = { CascadeType.ALL})
    @JoinColumn(name = "masterThesis_id", referencedColumnName = "id")
    private MasterThesis masterThesis;

    @ManyToOne(cascade = { CascadeType.PERSIST })
    @JoinColumn(name = "GRADUATION_CLASS_ID")
    private GraduationClass graduationClass;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "student_course", joinColumns = @JoinColumn(name = "student_id")
            , inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> courses = new HashSet<>( 2 );

    public void setMasterThesis( MasterThesis masterThesis ) {
        if ( sameAsCurrent( masterThesis ) ) {
            return;
        }
        this.masterThesis = masterThesis;
        masterThesis.setStudent( this );
    }

    private boolean sameAsCurrent( MasterThesis masterThesis ) {
        return this.masterThesis == null ?
                masterThesis == null : this.masterThesis.equals( masterThesis );

    }

    public void setGraduationClass( GraduationClass graduationClass ) {
        if ( sameAsCurrent( graduationClass ) ) {
            return;
        }
        this.graduationClass = graduationClass;
        if ( graduationClass != null ) {
            graduationClass.addStudent( this );
        }
    }

    private boolean sameAsCurrent( GraduationClass graduationClass ) {
        return this.graduationClass == null ?
                graduationClass == null : this.graduationClass.equals( graduationClass );
    }

    public void addCourse( Course course ) {
        if ( hasCourseAlready( course ) ) {
            return;
        }
        if ( course != null ) {
            courses.add( course );
            course.addStudent( this );
        }
    }

    private boolean hasCourseAlready( Course course ) {
        return courses.contains( course );
    }

    public void removeCourse( Course course ) {
        boolean wasRemoved = courses.remove( course );
        if ( wasRemoved ) {
            course.removeStudent( this );
        }
    }
}
