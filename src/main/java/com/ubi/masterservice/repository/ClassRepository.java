package com.ubi.masterservice.repository;

import com.ubi.masterservice.entity.EducationalInstitution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.ubi.masterservice.entity.ClassDetail;

@Repository
public interface ClassRepository extends JpaRepository<ClassDetail, Long> {


    @Query(
            value = "SELECT * FROM class_data " +
                    "WHERE class_data.is_deleted = false " + "AND class_id = ?1",
            nativeQuery = true)
    ClassDetail findByIdIfNotDeleted(Long classId);

    @Query(
            value = "SELECT * FROM class_data " +
                    "WHERE class_data.is_deleted = false ORDER BY modified DESC",
            nativeQuery = true)
    Page<ClassDetail> getAllAvailaibleClassDetails(Pageable paging);

    ClassDetail getClassByclassName(String className);

    ClassDetail getClassByclassCode(String classCode);

    @Query(
            value = "SELECT * FROM class_data " +
                    "WHERE class_data.is_deleted = false AND teacher_id = ?1 ORDER BY modified DESC",
            nativeQuery = true)
    ClassDetail findByTeacherId(Long teacherId);

    @Query(
            value = "SELECT * FROM class_data " +
                    "WHERE class_data.is_deleted = false AND class_name = ?1 ORDER BY modified DESC",
            nativeQuery = true)
    Page<ClassDetail> findByClassNameIgnoreCase(String name,Pageable paging);
    @Query(
            value = "SELECT * FROM class_data " +
                    "WHERE class_data.is_deleted = false AND class_code = ?1 ORDER BY modified DESC",
            nativeQuery = true)
    Page<ClassDetail> findByClassCode(String code,Pageable paging);
    @Query(
            value = "SELECT * FROM class_data " +
                    "WHERE class_data.is_deleted = false AND class_id = ?1 ORDER BY modified DESC",
            nativeQuery = true)
    Page<ClassDetail> findByClassId(Long id,Pageable paging);
}