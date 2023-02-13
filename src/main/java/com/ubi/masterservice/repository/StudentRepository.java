package com.ubi.masterservice.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ubi.masterservice.entity.EducationalInstitution;
import com.ubi.masterservice.entity.Region;
import com.ubi.masterservice.entity.Student;



@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {
	
	Student getStudentByRollNo(Long rollNo);
	
	List<Student> findByGenderAndCategoryAndMinority(String gender,String category, String minority);

	Page<Student> findByDateOfBirth(Date dateofBirth,Pageable paging);
	Page<Student> findByJoiningDate(Date joiningDate,Pageable paging);
	Page<Student> findByStudentFirstNameIgnoreCase(String studentFirstName, Pageable paging);

	Page<Student> findByStudentLastNameIgnoreCase(String studentLastName, Pageable paging);
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
	Page<Student> findByRollNo(Long rollNo,Pageable paging);

	Page<Student> findBybloodGroup(String bloodGroup, Pageable paging);

	Page <Student> findByaadhaarNo(Long aadhaarNo, Pageable paging);
	Page<Student> findByIsPhysicallyHandicapped(Boolean isPhysicallyHandicapped,Pageable paging);
	Page<Student> findByVerifiedByTeacher(boolean verifiedByTeacher,Pageable paging);
	Page<Student> findByVerifiedByPrincipal(boolean verifiedByPrincipal,Pageable paging);
//	Page<Student> findByStudentStatus(boolean studentStatus,Pageable paging);


	@Query(
			value = "SELECT * FROM student WHERE class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE educational_institute_id = ?1))",
			nativeQuery = true)
	Page<Student> findStudentsByInstituteId(Integer instituteId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE student_first_name = ?1 AND class_detail_class_id IN" +
					"(SELECT class_id FROM class_data WHERE school_id IN" +
					"(SELECT school_id FROM school_details WHERE educational_institute_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByFirstNameAndInstituteId(String fieldQuery,Integer instituteId,Pageable paging);


	@Query(
			value = "SELECT * FROM student WHERE student_last_name = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE educational_institute_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByLastNameAndInstituteId(String fieldQuery,Integer instituteId,Pageable paging);


	@Query(
			value = "SELECT * FROM student WHERE concat_ws(' ',student_first_name,student_last_name) like CONCAT('%', CONCAT(?1, '%'))  AND class_detail_class_id IN " +
					"(SELECT class_id FROM class_data WHERE school_id IN " +
					"(SELECT school_id FROM school_details WHERE educational_institute_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByFullNameAndInstituteId(String fieldQuery,Integer instituteId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE verified_by_teacher = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE educational_institute_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByVerifiedByTeacherAndInstituteId(Boolean fieldQuery,Integer instituteId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE verified_by_principal = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE educational_institute_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByVerifiedByPrincipalAndInstituteId(Boolean fieldQuery,Integer instituteId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE category = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE educational_institute_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByCategoryAndInstituteId(String fieldQuery,Integer instituteId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE minority = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE educational_institute_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByMinorityAndInstituteId(String fieldQuery,Integer instituteId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE father_name = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE educational_institute_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByFatherNameAndInstituteId(String fieldQuery,Integer instituteId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE mother_name = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE educational_institute_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByMotherNameAndInstituteId(String fieldQuery,Integer instituteId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE gender = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE educational_institute_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByGenderAndInstituteId(String fieldQuery,Integer instituteId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE is_activate = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE educational_institute_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByIsActivateAndInstituteId(Boolean fieldQuery,Integer instituteId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE current_status = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE educational_institute_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByCurrentStatusAndInstituteId(String fieldQuery,Integer instituteId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE date_of_birth = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE educational_institute_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByDOBAndInstituteId(Date fieldQuery, Integer instituteId, Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE joining_date = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE educational_institute_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByDOJAndInstituteId(Date fieldQuery, Integer instituteId, Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE region_id = ?1))",
			nativeQuery = true)
	Page<Student> findStudentsByRegionId(Integer regionId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE student_first_name = ?1 AND class_detail_class_id IN" +
					"(SELECT class_id FROM class_data WHERE school_id IN" +
					"(SELECT school_id FROM school_details WHERE region_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByFirstNameAndRegionId(String fieldQuery,Integer regionId,Pageable paging);


	@Query(
			value = "SELECT * FROM student WHERE student_last_name = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE region_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByLastNameAndRegionId(String fieldQuery,Integer regionId,Pageable paging);


	@Query(
			value = "SELECT * FROM student WHERE concat_ws(' ',student_first_name,student_last_name) like CONCAT('%', CONCAT(?1, '%'))  AND class_detail_class_id IN " +
					"(SELECT class_id FROM class_data WHERE school_id IN " +
					"(SELECT school_id FROM school_details WHERE region_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByFullNameAndRegionId(String fieldQuery,Integer regionId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE verified_by_teacher = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE region_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByVerifiedByTeacherAndRegionId(Boolean fieldQuery,Integer regionId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE verified_by_principal = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE region_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByVerifiedByPrincipalAndRegionId(Boolean fieldQuery,Integer regionId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE category = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE region_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByCategoryAndRegionId(String fieldQuery,Integer regionId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE minority = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE region_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByMinorityAndRegionId(String fieldQuery,Integer regionId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE father_name = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE region_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByFatherNameAndRegionId(String fieldQuery,Integer regionId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE mother_name = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE region_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByMotherNameAndRegionId(String fieldQuery,Integer regionId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE gender = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE region_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByGenderAndRegionId(String fieldQuery,Integer regionId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE is_activate = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE region_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByIsActivateAndRegionId(Boolean fieldQuery,Integer regionId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE current_status = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE region_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByCurrentStatusAndRegionId(String fieldQuery,Integer regionId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE date_of_birth = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE region_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByDOBAndRegionId(Date fieldQuery, Integer regionId, Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE joining_date = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id IN\n" +
					"(SELECT school_id FROM school_details WHERE region_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByDOJAndRegionId(Date fieldQuery, Integer regionId, Pageable paging);


	@Query(
			value = "SELECT * FROM student WHERE class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id = ?1)",
			nativeQuery = true)
	Page<Student> findStudentsBySchoolId(Integer schoolId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE student_first_name = ?1 AND class_detail_class_id IN" +
					"(SELECT class_id FROM class_data WHERE school_id = ?2)",
			nativeQuery = true)
	Page<Student> findStudentsByFirstNameAndSchoolId(String fieldQuery,Integer schoolId,Pageable paging);


	@Query(
			value = "SELECT * FROM student WHERE student_last_name = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id = ?2)",
			nativeQuery = true)
	Page<Student> findStudentsByLastNameAndSchoolId(String fieldQuery,Integer schoolId,Pageable paging);


	@Query(
			value = "SELECT * FROM student WHERE concat_ws(' ',student_first_name,student_last_name) like CONCAT('%', CONCAT(?1, '%'))  AND class_detail_class_id IN " +
					"(SELECT class_id FROM class_data WHERE school_id = ?2)",
			nativeQuery = true)
	Page<Student> findStudentsByFullNameAndSchoolId(String fieldQuery,Integer schoolId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE verified_by_teacher = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id = ?2))",
			nativeQuery = true)
	Page<Student> findStudentsByVerifiedByTeacherAndSchoolId(Boolean fieldQuery,Integer schoolId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE verified_by_principal = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id = ?2)",
			nativeQuery = true)
	Page<Student> findStudentsByVerifiedByPrincipalAndSchoolId(Boolean fieldQuery,Integer schoolId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE category = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id = ?2)",
			nativeQuery = true)
	Page<Student> findStudentsByCategoryAndSchoolId(String fieldQuery,Integer schoolId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE minority = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id = ?2)",
			nativeQuery = true)
	Page<Student> findStudentsByMinorityAndSchoolId(String fieldQuery,Integer schoolId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE father_name = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id = ?2)",
			nativeQuery = true)
	Page<Student> findStudentsByFatherNameAndSchoolId(String fieldQuery,Integer schoolId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE mother_name = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id = ?2)",
			nativeQuery = true)
	Page<Student> findStudentsByMotherNameAndSchoolId(String fieldQuery,Integer schoolId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE gender = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id = ?2)",
			nativeQuery = true)
	Page<Student> findStudentsByGenderAndSchoolId(String fieldQuery,Integer schoolId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE is_activate = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id = ?2)",
			nativeQuery = true)
	Page<Student> findStudentsByIsActivateAndSchoolId(Boolean fieldQuery,Integer schoolId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE current_status = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id = ?2)",
			nativeQuery = true)
	Page<Student> findStudentsByCurrentStatusAndSchoolId(String fieldQuery,Integer schoolId,Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE date_of_birth = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id = ?2)",
			nativeQuery = true)
	Page<Student> findStudentsByDOBAndSchoolId(Date fieldQuery, Integer schoolId, Pageable paging);

	@Query(
			value = "SELECT * FROM student WHERE joining_date = ?1 AND class_detail_class_id IN \n" +
					"(SELECT class_id FROM class_data WHERE school_id = ?2)",
			nativeQuery = true)
	Page<Student> findStudentsByDOJAndSchoolId(Date fieldQuery, Integer schoolId, Pageable paging);

}
