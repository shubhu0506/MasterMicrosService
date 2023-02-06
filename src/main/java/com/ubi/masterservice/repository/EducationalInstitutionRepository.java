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


	Optional<EducationalInstitution>findByeducationalInstitutionName(String educationalInstitutionName);

	EducationalInstitution getEducationalInstitutionByeducationalInstitutionName(String educationalInstitutionName);

	EducationalInstitution getEducationalInstitutionByeducationalInstitutionCode(String educationalInstitutionCode);

	Page<EducationalInstitution> findByEducationalInstitutionCode(String educationalInstitutionCode, Pageable paging);

	Page<EducationalInstitution> findByEducationalInstitutionName(String educationalInstitutionName,Pageable paging);

	Page<EducationalInstitution> findByEducationalInstitutionType(String educationalInstitutionType,Pageable paging);

	Page<EducationalInstitution> findByStrength(Long strength,Pageable paging);

	Page<EducationalInstitution> findByState(String state,Pageable paging);

	Page<EducationalInstitution> findByExemptionFlag(String exemptionFlag,Pageable paging);

	Page<EducationalInstitution> findByVvnAccount(Long vvnAccount,Pageable paging);

	Page<EducationalInstitution> findAllById(int id,Pageable paging);

	Page<EducationalInstitution> findAllByAdminId(Long adminId, Pageable paging);

	EducationalInstitution findByAdminId(Long adminId);

}