package com.ubi.masterservice.repository;


import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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

}
