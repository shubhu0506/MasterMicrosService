package com.ubi.masterservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ubi.masterservice.entity.School;

public interface SchoolRepository extends JpaRepository<School, Integer> {

	@Query(value = "FROM School sd  WHERE sd.name = ?1")
	Optional<School> findByName(String name);

	School getSchoolByName(String name);

	School getSchoolByCode(int code);

	Optional<School> findByname(String name);

	Page<School> findByisCollege(boolean flag, Pageable paging);

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
}