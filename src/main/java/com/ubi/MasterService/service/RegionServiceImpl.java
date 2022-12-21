package com.ubi.MasterService.service;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ubi.MasterService.dto.regionDto.RegionDto;
import com.ubi.MasterService.dto.regionDto.RegionSchoolDto;
import com.ubi.MasterService.dto.regionDto.RegionSchoolMappingDto;
import com.ubi.MasterService.dto.response.Response;
import com.ubi.MasterService.entity.EducationalInstitution;
import com.ubi.MasterService.entity.Region;
import com.ubi.MasterService.entity.School;
import com.ubi.MasterService.error.CustomException;
import com.ubi.MasterService.error.HttpStatusCode;
import com.ubi.MasterService.error.Result;
import com.ubi.MasterService.mapper.RegionMapper;
import com.ubi.MasterService.mapper.SchoolMapper;
import com.ubi.MasterService.repository.EducationalInstitutionRepository;
import com.ubi.MasterService.repository.RegionRepository;
import com.ubi.MasterService.repository.SchoolRepository;


@Service
public class RegionServiceImpl implements RegionService {

	@Autowired
	private RegionRepository regionRepository;

	@Autowired
	private RegionMapper regionMapper;
	
	@Autowired
	private SchoolMapper schoolMapper;
	
	@Autowired
	private SchoolRepository schoolRepository;
	
	@Autowired
	private EducationalInstitutionRepository educationalInstitutionRepository;
	
	@Override
	public Response<RegionDto> addRegion(RegionDto regionDto) {
		Result<RegionDto> res = new Result<>();
		Response<RegionDto> response = new Response<>();
		Optional<Region> tempRegion = regionRepository.findById(regionDto.getId());
		Region regionName=regionRepository.getRegionByName(regionDto.getName());
		Region regionCode=regionRepository.getRegionByCode(regionDto.getCode());
		if (tempRegion.isPresent()){
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
		}
		if(regionName!=null)
		{
			throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_EXISTS.getCode(),
					HttpStatusCode.RESOURCE_ALREADY_EXISTS,HttpStatusCode.RESOURCE_ALREADY_EXISTS.getMessage(),
					res);
		}
		if(regionCode!=null)
		{
			throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_EXISTS.getCode(),
					HttpStatusCode.RESOURCE_ALREADY_EXISTS,HttpStatusCode.RESOURCE_ALREADY_EXISTS.getMessage(),
					res);
		}
		Region saveRegion = regionRepository.save(regionMapper.dtoToEntity(regionDto));
		response.setStatusCode(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getMessage());
		response.setResult(new Result<RegionDto>(regionMapper.entityToDto(saveRegion)));
		return response;
	}

	@Override
	public Response<List<RegionDto>> getRegionDetails(Integer PageNumber, Integer PageSize) {
		Result<List<RegionDto>> allRegion = new Result<>();
		Pageable paging = PageRequest.of(PageNumber, PageSize);
		Response<List<RegionDto>> getListofRegion = new Response<List<RegionDto>>();
		
		Page<Region> list = this.regionRepository.findAll(paging);
		List<RegionDto> paymentDtos = regionMapper.entitiesToDtos(list.toList());
		if (list.isEmpty()) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), allRegion);
		}
		allRegion.setData(paymentDtos);
		getListofRegion.setStatusCode(HttpStatusCode.REGION_RETREIVED_SUCCESSFULLY.getCode());
		getListofRegion.setMessage(HttpStatusCode.REGION_RETREIVED_SUCCESSFULLY.getMessage());
		getListofRegion.setResult(allRegion);
		return getListofRegion;
	}

	@Override
	public Response<RegionDto> getRegionById(int id) {
		Response<RegionDto> getRegion = new Response<RegionDto>();
		Optional<Region> region = this.regionRepository.findById(id);
		Result<RegionDto> regionResult = new Result<>();
		if (!region.isPresent()) {
			throw new CustomException(HttpStatusCode.REGION_NOT_FOUND.getCode(),
					HttpStatusCode.REGION_NOT_FOUND, HttpStatusCode.REGION_NOT_FOUND.getMessage(),
					regionResult);
		}
		regionResult.setData(regionMapper.entityToDto(region.get()));
		getRegion.setStatusCode(HttpStatusCode.REGION_RETREIVED_SUCCESSFULLY.getCode());
		getRegion.setMessage(HttpStatusCode.REGION_RETREIVED_SUCCESSFULLY.getMessage());
		getRegion.setResult(regionResult);
		return getRegion;
	}

	@Override
	public Response<RegionDto> deleteRegionById(int id) {
		Result<RegionDto> res = new Result<>();
		res.setData(null);
		Optional<Region> region = regionRepository.findById(id);
		if (!region.isPresent()) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
		}
		
		for(EducationalInstitution eduInsti:region.get().getEducationalInstitiute()) {
			eduInsti.getRegion().remove(region.get());
			educationalInstitutionRepository.save(eduInsti);
		}
		region.get().setEducationalInstitiute(new HashSet<>());
		regionRepository.save(region.get());
		regionRepository.deleteById(id);	
		Response<RegionDto> response = new Response<>();
		response.setMessage(HttpStatusCode.REGION_DELETED_SUCCESSFULLY.getMessage());
		response.setStatusCode(HttpStatusCode.REGION_DELETED_SUCCESSFULLY.getCode());
		response.setResult(new Result<RegionDto>(regionMapper.entityToDto(region.get())));
		return response;
	}

	@Override
	public Response<RegionDto> updateRegionDetails(RegionDto regionDto) {
		Result<RegionDto> res = new Result<>();

		res.setData(null);
		Optional<Region> existingRegionContainer = regionRepository.findById(regionDto.getId());
		if (!existingRegionContainer.isPresent()) {
			throw new CustomException(HttpStatusCode.REGION_NOT_FOUND.getCode(), HttpStatusCode.REGION_NOT_FOUND,
					HttpStatusCode.REGION_NOT_FOUND.getMessage(), res);
		}
		RegionDto existingRegion = regionMapper.entityToDto(existingRegionContainer.get());
		existingRegion.setCode(regionDto.getCode());
		existingRegion.setName(regionDto.getName());

		Region updateRegion = regionRepository.save(regionMapper.dtoToEntity(existingRegion));
		Response<RegionDto> response = new Response<>();
		response.setMessage(HttpStatusCode.REGION_UPDATED.getMessage());
		response.setStatusCode(HttpStatusCode.REGION_UPDATED.getCode());
		response.setResult(new Result<>(regionMapper.entityToDto(updateRegion)));
		return response;
	}


//	@Override
//	public ByteArrayInputStream load() {
//		List<Region> region=regionRepository.findAll();
//        ByteArrayInputStream out = RegionEducationalCsvHelper.regionCSV(region);
//	    return out;
//	  }
//	
//	@Override
//	public ByteArrayInputStream Regionload() {
//		List<Region> region=regionRepository.findAll();
//        ByteArrayInputStream out = RegionSchoolCsvHelper.regionSchoolCSV(region);
//	    return out;
//	  }


	@Override
	public Response<RegionDto> getRegionByName(String name) {
		Response<RegionDto> getRegion = new Response<RegionDto>();
		Region region = regionRepository.getRegionByName(name);
		Result<RegionDto> regionResult = new Result<>();
		if (region==null) {
			throw new CustomException(HttpStatusCode.REGION_NOT_FOUND.getCode(),
					HttpStatusCode.REGION_NOT_FOUND, HttpStatusCode.REGION_NOT_FOUND.getMessage(),
					regionResult);
		}
		
		regionResult.setData(regionMapper.toDto(region));
		getRegion.setStatusCode(HttpStatusCode.REGION_RETREIVED_SUCCESSFULLY.getCode());
		getRegion.setMessage(HttpStatusCode.REGION_RETREIVED_SUCCESSFULLY.getMessage());
		getRegion.setResult(regionResult);
		return getRegion;
	}
	
// Mapping
	@Override
	public Response<RegionSchoolDto> addSchool(RegionSchoolMappingDto regionSchoolMappingDto) {
		int regionId =regionSchoolMappingDto.getRegionId();
		int schoolId = regionSchoolMappingDto.getSchoolId();
		Response<RegionSchoolDto> response = new Response<>();
		Result<RegionSchoolDto> res = new Result<>();
		Region region = regionRepository.getReferenceById(regionId);
		School school = schoolRepository.getReferenceById(schoolId);
		Set<School> setOfSchool = region.getSchool();
		for (School currSchool : setOfSchool) {
			if (currSchool.getSchoolId() == region.getId()) {
				throw new CustomException(HttpStatusCode.MAPPING_ALREADY_EXIST.getCode(),
						HttpStatusCode.MAPPING_ALREADY_EXIST, HttpStatusCode.MAPPING_ALREADY_EXIST.getMessage(), res);
			}
		}
		region.getSchool().add(school);
		school.setRegion(region);
	
			schoolRepository.save(school);
		regionRepository.save(region);
		RegionSchoolDto regionSchoolDto = regionMapper.toRegionSchoolDto(region);
		response.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
		response.setMessage(HttpStatusCode.SUCCESSFUL.getMessage());
		response.setResult(new Result<>(regionSchoolDto));
		return response;
	}

@Override
public Response<RegionSchoolDto> getRegionwithSchool(int id) {
	
	Response<RegionSchoolDto> response = new Response<>();
	Result<RegionSchoolDto> res = new Result<>();

	Optional<Region> region = this.regionRepository.findById(id);

	if (!region.isPresent()) {
		throw new CustomException(HttpStatusCode.NO_REGION_MATCH_WITH_ID.getCode(),
				HttpStatusCode.NO_REGION_MATCH_WITH_ID,
				HttpStatusCode.NO_REGION_MATCH_WITH_ID.getMessage(), res);
	}
	
	RegionSchoolDto regionSchoolDto = new RegionSchoolDto();
	
	regionSchoolDto.setRegionDto(regionMapper.entityToDto(region.get()));
	regionSchoolDto.setSchoolDto(schoolMapper.entitiesToDtos(region.get().getSchool()));

	res.setData(regionSchoolDto);

	response.setStatusCode(HttpStatusCode.REGION_RETRIEVED_SUCCESSFULLY.getCode());
	response.setMessage(HttpStatusCode.REGION_RETRIEVED_SUCCESSFULLY.getMessage());
	response.setResult(new Result<>(regionSchoolDto));
	return response;

}
	
@Override
public Response<List<RegionDto>> getRegionwithSort(String field) {

	Result<List<RegionDto>> allRegionResult = new Result<>();

	Response<List<RegionDto>> getListofRegion = new Response<>();

	List<Region> list = this.regionRepository.findAll(Sort.by(Sort.Direction.ASC,field));
	List<RegionDto> regionDtos = regionMapper
			.entitiesToDtos(list);

	if (list.size() == 0) {
		throw new CustomException(HttpStatusCode.NO_REGION_FOUND.getCode(), HttpStatusCode.NO_REGION_FOUND,
				HttpStatusCode.NO_REGION_FOUND.getMessage(), allRegionResult);
	}
	allRegionResult.setData(regionDtos);
	getListofRegion.setStatusCode(HttpStatusCode.REGION_RETRIEVED_SUCCESSFULLY.getCode());
	getListofRegion.setMessage(HttpStatusCode.REGION_RETRIEVED_SUCCESSFULLY.getMessage());
	getListofRegion.setResult(allRegionResult);
	return getListofRegion;
}


}
