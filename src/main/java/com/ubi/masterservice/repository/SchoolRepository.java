package com.ubi.masterservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.ubi.masterservice.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ubi.masterservice.entity.EducationalInstitution;
import com.ubi.masterservice.entity.School;

public interface SchoolRepository extends JpaRepository<School, Integer> {


	@Query(
			value = "SELECT * FROM School_Details sch " +
					"WHERE is_deleted = false AND school_id = ?1",
			nativeQuery = true)
	School findByIdIfNotDeleted(Integer id);
	Optional<School> findById(int id);

	School getSchoolByName(String name);

	School getSchoolByCode(int code);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE is_college = ?1 AND is_deleted = false ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> findByisCollege(boolean flag, Pageable paging);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE school_code = ?1 AND is_deleted = false ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> findByCode(Integer schoolCode, Pageable paging);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE school_name = ?1 AND is_deleted = false ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> findByName(String schoolName,Pageable paging);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE school_address = ?1 AND is_deleted = false ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> findByAddress(String schoolAddress,Pageable paging);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE school_contact = ?1 AND is_deleted = false ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> findByContact(Long schoolContact,Pageable paging);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE school_email = ?1 AND is_deleted = false ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> findByEmail(String schoolEmail, Pageable paging);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE school_type = ?1 AND is_deleted = false ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> findByType(String schoolType, Pageable paging);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE strength = ?1 AND is_deleted = false ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> findByStrength(Integer strength, Pageable paging);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE school_shift = ?1 AND is_deleted = false ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> findByShift(String schoolShift, Pageable paging);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE vvn_account = ?1 AND is_deleted = false ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> findByVvnAccount(Integer vvnAccount, Pageable paging);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE vvn_fund = ?1 AND is_deleted = false ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> findByVvnFund(Integer vvnFund, Pageable paging);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE exemption_flag = ?1 AND is_deleted = false ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> findByExemptionFlag(boolean exemptionFlag, Pageable paging);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE is_college = ?1 AND is_deleted = false ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> findByIsCollege(Boolean isCollege, Pageable paging);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE principal_id = ?1 AND is_deleted = false ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> findByPrincipalId(Long principalId, Pageable paging);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE school_id= ?1 AND is_deleted = false ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> findAllBySchoolId(int id,Pageable paging);


	@Query(
			value = "SELECT * FROM School_Details sch WHERE is_deleted = false AND principal_id = ?1 and is_college = false",
			nativeQuery = true)
	School findByPrincipalId(Long principalId);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE is_deleted = false AND principal_id = ?1 and is_college = true",
			nativeQuery = true)
	School findCollegeByPrincipalId(Long principalId);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE is_deleted = false AND region_id = ?1 and is_college = false ORDER BY modified DESC",
			nativeQuery = true)
	Set<School> findSchoolByRegionId(Long regionId);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE is_deleted = false AND region_id = ?1 and is_college = true ORDER BY modified DESC",
			nativeQuery = true)
	Set<School> findCollegeByRegionId(Long regionId);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE is_deleted = false AND  educational_institute_id = ?1 AND is_college = ?2 ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> getAllSchoolByInstituteId(Integer instituteId,Boolean isCollege,Pageable page);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE is_deleted = false AND school_name = ?1 AND educational_institute_id = ?2 AND is_college = ?3 ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> getAllSchoolByNameAndInstituteId(String fieldQuery,Integer instituteId,Boolean isCollege,Pageable page);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE is_deleted = false AND school_email = ?1 AND educational_institute_id = ?2 AND is_college = ?3 ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> getAllSchoolByEmailAndInstituteId(String fieldQuery,Integer instituteId,Boolean isCollege,Pageable page);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE is_deleted = false AND school_code = ?1 AND educational_institute_id = ?2 AND is_college = ?3 ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> getAllSchoolByCodeAndInstituteId(String fieldQuery,Integer instituteId,Boolean isCollege,Pageable page);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE is_deleted = false AND exemption_flag = ?1 AND educational_institute_id = ?2 AND is_college = ?3 ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> getAllSchoolByExemptionFlagAndInstituteId(Boolean fieldQuery,Integer instituteId,Boolean isCollege,Pageable page);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE is_deleted = false AND school_shift = ?1 AND educational_institute_id = ?2 AND is_college = ?3 ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> getAllSchoolByShiftAndInstituteId(String fieldQuery,Integer instituteId,Boolean isCollege,Pageable page);

	@Query(
			value = "SELECT * FROM School_Details sch WHERE is_deleted = false AND vvn_account = ?1 AND educational_institute_id = ?2 AND is_college = ?3 ORDER BY modified DESC",
			nativeQuery = true)
	Page<School> getAllSchoolByVVNAccountAndInstituteId(Integer fieldQuery,Integer instituteId,Boolean isCollege,Pageable page);

}

