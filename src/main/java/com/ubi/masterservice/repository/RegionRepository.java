package com.ubi.masterservice.repository;


import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubi.masterservice.entity.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region,Integer> {
    Region getRegionByName(String name);

    Region getRegionByCode(String code);

    Set<Region> getReferenceByIdIn(Set<Integer> regionId);

    List<Region> findByName(String searchByField);
    List<Region> findByCode(String searchByField);

    List<Region> findAllById(int searchByField);

}
