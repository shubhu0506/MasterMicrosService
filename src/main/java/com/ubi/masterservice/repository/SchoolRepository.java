package com.ubi.masterservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ubi.masterservice.entity.School;

public interface SchoolRepository extends JpaRepository<School, Integer>{

	@Query(value = "FROM School sd  WHERE sd.name = ?1")
	Optional<School> findByName(String name);

	School getSchoolByName(String name);

	School getSchoolByCode(int code);

	Optional<School> findByname(String name);

}
