package com.ubi.masterservice.repository;

import com.ubi.masterservice.entity.StudentPromoteDemote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentPromoteDemoteRepository extends JpaRepository<StudentPromoteDemote,Long> {

//    StudentPromoteDemote save(StudentPromoteDemoteDto studentPromoteDemoteCreationDto);
}
