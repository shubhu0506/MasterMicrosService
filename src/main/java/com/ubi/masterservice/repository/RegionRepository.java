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

    @Query(
            value = "SELECT * FROM region " +
                    "WHERE region.is_deleted = false",
            nativeQuery = true)
    Page<Region> getAllAvailaibleRegion(Pageable paging);

    @Query(
            value = "SELECT * FROM region " +
                    "WHERE region.is_deleted = false AND name = ?1",
            nativeQuery = true)
    Region getRegionByName(String name);

    @Query(
            value = "SELECT * FROM region " +
                    "WHERE region.is_deleted = false AND code = ?1",
            nativeQuery = true)
    Region getRegionByCode(String code);

//    Set<Region> getReferenceByIdIn(Set<Integer> regionId);

    @Query(
            value = "SELECT * FROM region " +
                    "WHERE region.is_deleted = false AND name = ?1",
            nativeQuery = true)
	Page<Region> findByName(String searchByField,Pageable paging);

    @Query(
            value = "SELECT * FROM region " +
                    "WHERE region.is_deleted = false AND code = ?1",
            nativeQuery = true)
	Page<Region> findByCode(String searchByField,Pageable paging);

    @Query(
            value = "SELECT * FROM region " +
                    "WHERE region.is_deleted = false AND id = ?1 ",
            nativeQuery = true)
	Page<Region> findAllById(int searchByField,Pageable paging);

    @Query(
            value = "SELECT * FROM region " +
                    "WHERE region.is_deleted = false AND admin_id = ?1",
            nativeQuery = true)
    Region findByAdminId(Long adminId);

    @Query(
            value = "SELECT * FROM region " +
                    "WHERE region.is_deleted = false AND region.id IN " +
                    "(SELECT region_id FROM ei_region_table WHERE educational_institution_id = ?1)",
            nativeQuery = true)
    Page<Region> findAllRegionInsideInstitute(Integer instituteId,Pageable page);

    @Query(
            value = "SELECT * FROM region " +
                    "WHERE region.is_deleted = false AND name = ?1 AND region.id IN " +
                    "(SELECT region_id FROM ei_region_table WHERE educational_institution_id = ?2)",
            nativeQuery = true)
    Page<Region> findByNameAndInstituteId(String feildQuery,Integer instituteId,Pageable paging);

    @Query(
            value = "SELECT * FROM region " +
                    "WHERE region.is_deleted = false AND code = ?1 AND region.id IN " +
                    "(SELECT region_id FROM ei_region_table WHERE educational_institution_id = ?2)",
            nativeQuery = true)
    Page<Region> findByCodeAndInstituteId(String feildQuery,Integer instituteId,Pageable paging);
}
