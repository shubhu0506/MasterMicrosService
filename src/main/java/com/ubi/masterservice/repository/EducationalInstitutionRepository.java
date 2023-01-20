package com.ubi.masterservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubi.masterservice.entity.EducationalInstitution;

@Repository
public interface EducationalInstitutionRepository extends JpaRepository<EducationalInstitution, Integer>{


	Optional<EducationalInstitution>findByeducationalInstitutionName(String educationalInstitutionName);

	EducationalInstitution getEducationalInstitutionByeducationalInstitutionName(String educationalInstitutionName);

	EducationalInstitution getEducationalInstitutionByeducationalInstitutionCode(String educationalInstitutionCode);

	List<EducationalInstitution> findByEducationalInstitutionCode(String educationalInstitutionCode);

	List<EducationalInstitution> findByEducationalInstitutionName(String educationalInstitutionName);

	List<EducationalInstitution> findByEducationalInstitutionType(String educationalInstitutionType);

	List<EducationalInstitution> findByStrength(Long strength);

	List<EducationalInstitution> findByState(String state);

	List<EducationalInstitution> findByExemptionFlag(String exemptionFlag);

	List<EducationalInstitution> findByVvnAccount(Long vvnAccount);

	List<EducationalInstitution> findAllById(int id);

	EducationalInstitution findByAdminId(Long adminId);

}