package com.ubi.masterservice.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubi.masterservice.entity.Student;



@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {

	List<Student> findByGenderAndCategoryAndMinority(String gender,String category, String minority);

	List<Student> findByDateOfBirth(Date dateofBirth);
	List<Student> findByJoiningDate(Date dateofBirth);
	List<Student> findByStudentName (String studentName);
	List<Student> findByGender (String gender);
	List<Student> findByCategory (String category);
	List<Student> findByMinority (String minority);
	List<Student> findByFatherName (String fatherName);
	List<Student> findByFatherOccupation (String fatherOccupation);
	List<Student> findByMotherName (String motherName);
	List<Student> findByCurrentStatus (String currentStatus);
	List<Student> findByLastVerifiedByTeacher(Long lastVerifiedByTeacher);
	List<Student> findByLastVerifiedByPrincipal(Long lastVerifiedByPrincipal);

	List<Student> findByVerifiedByTeacher(Boolean verifiedByTeacher);
	List<Student> findByStudentId(Long studentId);
	List<Student> findByVerifiedByTeacher(boolean verifiedByTeacher);
	List<Student> findByVerifiedByPrincipal(boolean verifiedByPrincipal);
	List<Student> findByStudentStatus(boolean studentStatus);
}
