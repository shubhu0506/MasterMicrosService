package com.ubi.masterservice.repository;

import java.util.List;
import java.util.Optional;

import com.sun.prism.shader.Solid_LinearGradient_REFLECT_AlphaTest_Loader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ubi.masterservice.entity.EducationalInstitution;

@Repository
public interface EducationalInstitutionRepository extends JpaRepository<EducationalInstitution, Integer>{



	@Query(
			value = "SELECT * FROM educational_institution " +
					"WHERE educational_institution.is_deleted = false",
			nativeQuery = true)
	Page<EducationalInstitution> getAllAvailaibleEducationalInstitution(Pageable paging);


	@Query(
			value = "SELECT * FROM educational_institution " +
					"WHERE educational_institution.is_deleted = false AND educational_institution_name = ?1",
			nativeQuery = true)
	Optional<EducationalInstitution>findByeducationalInstitutionName(String educationalInstitutionName);

	@Query(
			value = "SELECT * FROM educational_institution " +
					"WHERE educational_institution.is_deleted = false AND educational_institution_name = ?1",
			nativeQuery = true)
	EducationalInstitution getEducationalInstitutionByeducationalInstitutionName(String educationalInstitutionName);

	@Query(
			value = "SELECT * FROM educational_institution " +
					"WHERE educational_institution.is_deleted = false AND educational_institution_code = ?1",
			nativeQuery = true)
	EducationalInstitution getEducationalInstitutionByeducationalInstitutionCode(String educationalInstitutionCode);

	@Query(
			value = "SELECT * FROM educational_institution " +
					"WHERE educational_institution.is_deleted = false AND educational_institution_code = ?1",
			nativeQuery = true)
	Page<EducationalInstitution> findByEducationalInstitutionCode(String educationalInstitutionCode, Pageable paging);

	@Query(
			value = "SELECT * FROM educational_institution " +
					"WHERE educational_institution.is_deleted = false AND educational_institution_name = ?1",
			nativeQuery = true)
	Page<EducationalInstitution> findByEducationalInstitutionName(String educationalInstitutionName,Pageable paging);

	@Query(
			value = "SELECT * FROM educational_institution " +
					"WHERE educational_institution.is_deleted = false AND educational_institution_type = ?1",
			nativeQuery = true)
	Page<EducationalInstitution> findByEducationalInstitutionType(String educationalInstitutionType,Pageable paging);


	@Query(
			value = "SELECT * FROM educational_institution " +
					"WHERE educational_institution.is_deleted = false AND educational_institution_strength = ?1",
			nativeQuery = true)
	Page<EducationalInstitution> findByStrength(Long strength,Pageable paging);


	@Query(
			value = "SELECT * FROM educational_institution " +
					"WHERE educational_institution.is_deleted = false AND state = ?1",
			nativeQuery = true)
	Page<EducationalInstitution> findByState(String state,Pageable paging);

	@Query(
			value = "SELECT * FROM educational_institution " +
					"WHERE educational_institution.is_deleted = false AND educational_institution_exemption_flag = ?1",
			nativeQuery = true)
	Page<EducationalInstitution> findByExemptionFlag(String exemptionFlag,Pageable paging);

	@Query(
			value = "SELECT * FROM educational_institution " +
					"WHERE educational_institution.is_deleted = false AND educational_institution_vvn_account = ?1",
			nativeQuery = true)
	Page<EducationalInstitution> findByVvnAccount(Long vvnAccount,Pageable paging);

	@Query(
			value = "SELECT * FROM educational_institution " +
					"WHERE educational_institution.is_deleted = false AND id = ?1",
			nativeQuery = true)
	Page<EducationalInstitution> findAllById(int id,Pageable paging);

	@Query(
			value = "SELECT * FROM educational_institution " +
					"WHERE educational_institution.is_deleted = false AND educational_admin_id = ?1",
			nativeQuery = true)
	Page<EducationalInstitution> findAllByAdminId(Long adminId, Pageable paging);

	@Query(
			value = "SELECT * FROM educational_institution " +
					"WHERE educational_institution.is_deleted = false AND educational_admin_id = ?1",
			nativeQuery = true)
	EducationalInstitution findByAdminId(Long adminId);

}