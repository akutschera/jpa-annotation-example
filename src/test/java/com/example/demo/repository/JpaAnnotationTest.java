package com.example.demo.repository;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.example.demo.jpa.entity.Course;
import com.example.demo.jpa.entity.GraduationClass;
import com.example.demo.jpa.entity.MasterThesis;
import com.example.demo.jpa.entity.Student;
import com.example.demo.jpa.repository.CourseRepository;
import com.example.demo.jpa.repository.GraduationClassRepository;
import com.example.demo.jpa.repository.MasterThesisRepository;
import com.example.demo.jpa.repository.StudentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Created by Andreas Kutschera.
 */
@DataJpaTest
@ExtendWith(SpringExtension.class)
@DisplayName("Jpa Annotations")
class JpaAnnotationTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MasterThesisRepository masterThesisRepository;

    @Autowired
    private GraduationClassRepository graduationClassRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TestEntityManager entityManager;


    @Nested
    @DisplayName("@OneToOne")
    class OneToOne {

        private Student student;
        private MasterThesis masterThesis;

        @BeforeEach
        void init() {
            masterThesis = new MasterThesis();
            student = new Student();
        }

        @Test
        @DisplayName("saving inverse first will result in exception")
        void saveThesis() {
            masterThesis.setStudent( student );
            assertThatExceptionOfType( InvalidDataAccessApiUsageException.class )
                    .as( "we need to save the student field, because the student-id is the foreign field" )
                    .isThrownBy( () ->
                                         masterThesisRepository.save( masterThesis ) );
        }

        private void ensureBothRelationsAreSaved() {
            Optional<MasterThesis> savedThesisList = masterThesisRepository.findById( masterThesis.getId() );
            assertThat( savedThesisList ).isNotEmpty();
            assertThat( savedThesisList.get().getStudent() ).isEqualTo( student );
        }

        @Test
        @DisplayName("saving owner first")
        void saveStudentFirst() {
            masterThesis.setStudent( student );
            student = studentRepository.save( student );

            ensureBothRelationsAreSaved();
        }

        @Test
        @DisplayName("save inverse without setting owner should succeed")
        void saveThesisWithoutSettingStudent() {
            student.setMasterThesis( masterThesis );
            student = studentRepository.save( this.student );

            ensureBothRelationsAreSaved();
        }

        @Test
        @DisplayName("save inverse before setting owner will result in detached entity")
        @Disabled("until https://jira.spring.io/browse/DATAJPA-866 is solved")
        void saveThesisBeforeSetting() {
            masterThesisRepository.save( masterThesis );
            student.setMasterThesis( masterThesis );
            studentRepository.save( student );

            ensureBothRelationsAreSaved();
        }

        @Test
        @DisplayName("delete owner should delete inverse")
        void deleteOwner() {
            student.setMasterThesis( masterThesis );
            student = studentRepository.save( this.student );
            studentRepository.delete( student );

            assertThat( studentRepository.findById( student.getId() ) ).as( "owner repository should be empty" ).isEmpty();
            assertThat( masterThesisRepository.findById( masterThesis.getId() ) ).as( "inverse the repository should be empty" ).isEmpty();

        }

    }

    @Nested
    @DisplayName("@OneToMany")
    class OneToMany {

        private Student student;
        private GraduationClass graduationClass;


        @BeforeEach
        void init() {
            graduationClass = new GraduationClass();
            student = new Student();
        }

        @Test
        @DisplayName("when I add a non-saved student to a graduation class, and save the class, the student is persisted also")
        void setGraduationClass() {
            student.setGraduationClass( graduationClass );
            graduationClass.getStudents().add( student );

            graduationClassRepository.save( graduationClass );

            ensureStudentIsInGraduationClass();
        }

        @Test
        @DisplayName("add student to graduation class, save only graduation class")
        void saveStudent() {
            graduationClass.addStudent( student );
            graduationClassRepository.save( graduationClass );

            ensureStudentIsInGraduationClass();
        }

        @Test
        @DisplayName("add student to graduation class, save only student")
        void saveGraduationClass() {
            graduationClass.addStudent( student );
            studentRepository.save( student );

            ensureStudentIsInGraduationClass();
        }

        private void ensureStudentIsInGraduationClass() {
            Optional<GraduationClass> savedClass = graduationClassRepository.findById( graduationClass.getId() );
            assertThat( savedClass ).isNotEmpty();
            Set<Student> students = savedClass.get().getStudents();
            assertThat( students ).contains( student );
        }

        @Test
        @DisplayName("deleting one of a set should keep single entity intact")
        void deleteOneStudent() {
            graduationClass.addStudent( student );
            graduationClassRepository.save( graduationClass );

            graduationClass.removeStudent( student );
            graduationClassRepository.save( graduationClass );

            Optional<GraduationClass> savedClass = graduationClassRepository.findById( graduationClass.getId() );
            assertThat( savedClass.get().getStudents() ).isEmpty();

            Optional<Student> savedStudent = studentRepository.findById( student.getId() );
            assertThat( savedStudent ).isNotEmpty();
            assertThat( savedStudent.get().getGraduationClass() )
                    .as( "after removing student from graduation class, said student should not have one" ).isNull();
        }

    }

    @Nested
    @DisplayName("@ManyToMany")
    class ManyToMany {

        @BeforeEach
        void clearRepo() {
            courseRepository.deleteAll();
            studentRepository.deleteAll();
        }

        @Test
        @DisplayName("courses should be persisted in students")
        void persistCourseInStudent() {
            Student student = new Student();
            Course course = new Course();
            student.addCourse( course );

            // choose one or the other or both (which is unnecessary)
            studentRepository.save( student );
            courseRepository.save( course );

            List<Course> courses = courseRepository.findAll();
            assertThat( courses ).as("course should have been persisted, too").isNotEmpty();
            assertThat( courses.get( 0 ).getStudents() ).contains( student );
            assertThat( student.getCourses() ).contains( course );
        }
        
        @Test
        @DisplayName("students should be persisted in courses")
        void persistStudentInCourse () {
            Course course = new Course();
            Student student = new Student();
            course.addStudent( student );

            courseRepository.save( course );

            List<Student> students = studentRepository.findAll();
            assertThat( students ).isNotEmpty();
            assertThat( students.get( 0 ).getCourses() ).contains( course );
            assertThat( course.getStudents() ).contains( student );
        }
        
        @Test
        @DisplayName("courses should be taken by more than one student")
        void persistMultipleStudentsInCourse() {
            Course course = new Course();
            Student student = new Student();
            course.addStudent( student );
            courseRepository.save( course );
            Student other = new Student();
            course.addStudent( other );

            courseRepository.save( course );

            List<Course> courses = courseRepository.findAll();
            assertThat( courses.get(0).getStudents() ).as("students need a name or something like that").hasSize(2);
        }
        
        @Test
        @DisplayName("leaving a course should keep all other students in the course")
        void leaveCourse() {
            Course course = new Course();
            Student student = new Student();
            course.addStudent( student );
            Student studentToLeave = new Student();
            course.addStudent( studentToLeave );

            courseRepository.save( course );

            studentToLeave.removeCourse( course );
            // choose one or the other (both will work but are not needed)
            studentRepository.save( studentToLeave );
            courseRepository.save( course );

            List<Course> courses = courseRepository.findAll();
            assertThat( courses ).isNotEmpty();
            assertThat( courses.get( 0 ).getStudents() ).containsOnly( student );
        }

        @Test
        @DisplayName("deleting a course should delete the course from all students")
        void deleteCourse() {
            Course course = new Course();
            Student student = new Student();
            course.addStudent( student );
            courseRepository.save( course );

            assertThat( studentRepository.findAll().get( 0 ).getCourses()).isNotEmpty();
            courseRepository.delete( course );
            assertThat( studentRepository.findAll().get( 0 ).getCourses()).isEmpty();
        }
    }
    //    @Test
//    @DisplayName( "saving a graduation class with one masterthesis" )
//    void saveGraduationClass() {
//        Student2 masterthesis = new Student2();
//        Course course = new Course();
//        course.getStudents().add( masterthesis );
//        masterthesis.getCourses().add( course );
//
//        Course savedCourse = graduationClassRepository.save( course );
//
//        assertThat( savedCourse.getStudents() ).contains( masterthesis );
//        assertThat( studentRepository.count() ).isEqualTo( 1L );
//        Student2 savedStudent = savedCourse.getStudents().iterator().next();
//        assertThat( studentRepository.findById( savedStudent.getId() ).get().getCourses() ).isEqualTo( savedCourse );
//    }
//
//    @Test
//    @DisplayName( "saving a graduation class with one previously saved masterthesis" )
//    void saveGraduationWithExisting() {
//        Student2 masterthesis = studentRepository.save( new Student2() );
//        Course course = new Course();
//        course.getStudents().add( masterthesis );
//        masterthesis.getCourses().add( course );
//
//        Course savedCourse = graduationClassRepository.save( course );
//
//        assertThat( savedCourse.getStudents() ).contains( masterthesis );
//        assertThat( studentRepository.count() ).isEqualTo( 1L );
//        Student2 savedStudent = savedCourse.getStudents().iterator().next();
//        assertThat( studentRepository.findById( savedStudent.getId() ).get().getCourses() ).isEqualTo( savedCourse );
//
//    }
}
