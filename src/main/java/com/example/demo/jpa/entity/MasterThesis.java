package com.example.demo.jpa.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Andreas Kutschera.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of= {"id"})
public class MasterThesis implements Serializable{

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(mappedBy = "masterThesis")
    private Student student;

    private boolean alreadySetting = false;

    public void setStudent( Student student) {
        if( sameAsCurrent(student)) {
            return;
        }
        this.student = student;
        student.setMasterThesis( this );
    }
    private boolean sameAsCurrent( Student student ) {
        return this.student == null ?
                student == null : this.student.equals(student);

    }

}
