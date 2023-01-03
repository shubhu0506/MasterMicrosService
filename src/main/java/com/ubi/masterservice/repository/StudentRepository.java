package com.ubi.masterservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubi.masterservice.entity.Student;



@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {

	List<Student> findByGenderAndCategoryAndMinority(String gender,String category, String minority);

}
