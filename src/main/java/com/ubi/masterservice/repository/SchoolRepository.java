package com.ubi.masterservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ubi.masterservice.entity.EducationalInstitution;
import com.ubi.masterservice.entity.School;

public interface SchoolRepository extends JpaRepository<School, Integer> {

	@Query(value = "FROM School sd  WHERE sd.name = ?1")
	Optional<School> findByName(String name);

	School getSchoolByName(String name);

	School getSchoolByCode(int code);

	Optional<School> findByname(String name);

	Page<School> findByisCollege(boolean flag, Pageable paging);
	
	Page<School> findByCode(Integer schoolCode, Pageable paging);

	Page<School> findByName(String schoolName,Pageable paging);

	Page<School> findByAddress(String schoolAddress,Pageable paging);

	Page<School> findByContact(Long schoolContact,Pageable paging);
	
	Page<School> findByEmail(String schoolEmail, Pageable paging);
	
	Page<School> findByType(String schoolType, Pageable paging);
	
	Page<School> findByStrength(Integer strength, Pageable paging);
	
	Page<School> findByShift(String schoolShift, Pageable paging);
	
	Page<School> findByVvnAccount(Integer vvnAccount, Pageable paging);
	
	Page<School> findByVvnFund(Integer vvnFund, Pageable paging);
	
	Page<School> findByExemptionFlag(boolean exemptionFlag, Pageable paging);
	
	Page<School> findByIsCollege(Boolean isCollege, Pageable paging);
	
	Page<School> findByPrincipalId(Long principalId, Pageable paging);
	
	Page<School> findAllBySchoolId(int id,Pageable paging);


	@Query(
			value = "SELECT * FROM School_Details sch WHERE principal_id = ?1 and is_college = false",
			nativeQuery = true)
	School findByPrincipalId(Long principalId);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE principal_id = ?1 and is_college = true",
			nativeQuery = true)
	School findCollegeByPrincipalId(Long principalId);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE region_id = ?1 and is_college = false",
			nativeQuery = true)
	Set<School> findSchoolByRegionId(Long regionId);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE region_id = ?1 and is_college = true",
			nativeQuery = true)
	Set<School> findCollegeByRegionId(Long regionId);

	Optional<School> findById(int id);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE educational_institute_id = ?1 AND is_college = ?2",
			nativeQuery = true)
	Page<School> getAllSchoolByInstituteId(Integer instituteId,Boolean isCollege,Pageable page);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE school_name = ?1 AND educational_institute_id = ?2 AND is_college = ?3",
			nativeQuery = true)
	Page<School> getAllSchoolByNameAndInstituteId(String fieldQuery,Integer instituteId,Boolean isCollege,Pageable page);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE school_email = ?1 AND educational_institute_id = ?2 AND is_college = ?3",
			nativeQuery = true)
	Page<School> getAllSchoolByEmailAndInstituteId(String fieldQuery,Integer instituteId,Boolean isCollege,Pageable page);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE school_code = ?1 AND educational_institute_id = ?2 AND is_college = ?3",
			nativeQuery = true)
	Page<School> getAllSchoolByCodeAndInstituteId(String fieldQuery,Integer instituteId,Boolean isCollege,Pageable page);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE exemption_flag = ?1 AND educational_institute_id = ?2 AND is_college = ?3",
			nativeQuery = true)
	Page<School> getAllSchoolByExemptionFlagAndInstituteId(Boolean fieldQuery,Integer instituteId,Boolean isCollege,Pageable page);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE school_shift = ?1 AND educational_institute_id = ?2 AND is_college = ?3",
			nativeQuery = true)
	Page<School> getAllSchoolByShiftAndInstituteId(String fieldQuery,Integer instituteId,Boolean isCollege,Pageable page);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE vvn_account = ?1 AND educational_institute_id = ?2 AND is_college = ?3",
			nativeQuery = true)
	Page<School> getAllSchoolByVVNAccountAndInstituteId(Integer fieldQuery,Integer instituteId,Boolean isCollege,Pageable page);

}

