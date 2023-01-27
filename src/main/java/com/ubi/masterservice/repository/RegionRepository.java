package com.ubi.masterservice.repository;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ubi.masterservice.entity.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region,Integer> {
    Region getRegionByName(String name);

    Region getRegionByCode(String code);

    Set<Region> getReferenceByIdIn(Set<Integer> regionId);

	Page<Region> findByName(String searchByField,Pageable paging);
	Page<Region> findByCode(String searchByField,Pageable paging);

	Page<Region> findAllById(int searchByField,Pageable paging);

    Region findByAdminId(Long adminId);

    @Query(
            value = "SELECT * FROM region " +
                    "WHERE region.id IN " +
                    "(SELECT region_id FROM ei_region_table WHERE educational_institution_id = ?1)",
            nativeQuery = true)
    Page<Region> findAllRegionInsideInstitute(Integer instituteId,Pageable page);

    @Query(
            value = "SELECT * FROM region " +
                    "WHERE name = ?1 AND region.id IN " +
                    "(SELECT region_id FROM ei_region_table WHERE educational_institution_id = ?2)",
            nativeQuery = true)
    Page<Region> findByNameAndInstituteId(String feildQuery,Integer instituteId,Pageable paging);

    @Query(
            value = "SELECT * FROM region " +
                    "WHERE code = ?1 AND region.id IN " +
                    "(SELECT region_id FROM ei_region_table WHERE educational_institution_id = ?2)",
            nativeQuery = true)
    Page<Region> findByCodeAndInstituteId(String feildQuery,Integer instituteId,Pageable paging);
}
