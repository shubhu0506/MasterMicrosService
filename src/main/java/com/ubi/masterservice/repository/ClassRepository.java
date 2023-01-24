package com.ubi.masterservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ubi.masterservice.entity.ClassDetail;

import java.awt.print.Pageable;

@Repository
public interface ClassRepository extends JpaRepository<ClassDetail, Long> {

    //ClassDetail getClassByName(String name);

    // ClassDetail getClassByCode(String code);

    ClassDetail getClassByclassName(String className);

    ClassDetail getClassByclassCode(String classCode);

    //Optional<ClassDetail> findByName(String className);

    //ClassDetail getClassByName(String schoolName);

    ClassDetail findByTeacherId(Long teacherId);

}