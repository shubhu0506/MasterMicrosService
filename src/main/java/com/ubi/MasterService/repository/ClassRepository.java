package com.ubi.MasterService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ubi.MasterService.entity.ClassDetail;
import org.springframework.stereotype.Repository;


@Repository
public interface ClassRepository extends JpaRepository<ClassDetail, Long> {


    ClassDetail getClassByclassName(String className);

    ClassDetail getClassByclassCode(String classCode);


}
