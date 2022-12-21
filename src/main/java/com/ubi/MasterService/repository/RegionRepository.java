package com.ubi.MasterService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubi.MasterService.entity.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {
	Region getRegionByName(String name);

	Region getRegionByCode(String code);

}
