package com.ubi.MasterService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ubi.MasterService.entity.EducationalInstitution;

@Repository
public interface EducationalInstitutionRepository extends JpaRepository<EducationalInstitution, Integer> {

	Optional<EducationalInstitution> findByeducationalInstitutionName(String educationalInstitutionName);

	EducationalInstitution getEducationalInstitutionByeducationalInstitutionName(String educationalInstitutionName);

	EducationalInstitution getEducationalInstitutionByeducationalInstitutionCode(String educationalInstitutionCode);

}
