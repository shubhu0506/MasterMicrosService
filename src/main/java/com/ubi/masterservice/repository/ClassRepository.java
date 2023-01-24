package com.ubi.masterservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ubi.masterservice.entity.ClassDetail;

import java.awt.print.Pageable;

@Repository
public interface ClassRepository extends JpaRepository<ClassDetail, Long> {

    ClassDetail getClassByclassName(String className);

    ClassDetail getClassByclassCode(String classCode);

    ClassDetail findByTeacherId(Long teacherId);
    
    Page<ClassDetail> findByClassNameIgnoreCase(String name,Pageable paging);
    Page<ClassDetail> findByClassCode(String code,Pageable paging);
    Page<ClassDetail> findByClassId(Long id,Pageable paging);
}