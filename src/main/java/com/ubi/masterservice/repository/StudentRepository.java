package com.ubi.masterservice.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubi.masterservice.entity.Student;



@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {

	List<Student> findByGenderAndCategoryAndMinority(String gender,String category, String minority);

	Page<Student> findByDateOfBirth(Date dateofBirth,Pageable paging);
	Page<Student> findByJoiningDate(Date joiningDate,Pageable paging);
	Page<Student> findByStudentNameIgnoreCase(String studentName, Pageable paging);
	Page<Student> findByGenderIgnoreCase(String gender,Pageable paging);
	Page<Student> findByCategoryIgnoreCase (String category,Pageable paging);
	Page<Student> findByMinorityIgnoreCase (String minority,Pageable paging);
	Page<Student> findByFatherNameIgnoreCase (String fatherName,Pageable paging);
	Page<Student> findByFatherOccupationIgnoreCase (String fatherOccupatatus,Pageable paging);
	Page<Student> findByLastVerifiedByTeacher(Long lastVerifiedByTeacher,Pageable paging);
	Page<Student> findByLastVerifiedByPrincipal(Long lastVerifiedByPrincipal,Pageable paging);
	Page<Student> findByMotherNameIgnoreCase (String motherName,Pageable paging);
	Page<Student> findByCurrentStatus (String currentStastVerifiedByPrincipal,Pageable paging);

	Page<Student> findByVerifiedByTeacher(Boolean verifiedByTeacher,Pageable paging);
	Page<Student> findByStudentId(Long studentId,Pageable paging);
	Page<Student> findByVerifiedByTeacher(boolean verifiedByTeacher,Pageable paging);
	Page<Student> findByVerifiedByPrincipal(boolean verifiedByPrincipal,Pageable paging);
//	Page<Student> findByStudentStatus(boolean studentStatus,Pageable paging);
}
