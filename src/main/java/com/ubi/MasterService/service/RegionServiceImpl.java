package com.ubi.MasterService.service;

import java.io.ByteArrayInputStream;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ubi.MasterService.dto.regionDto.RegionCreationDto;
import com.ubi.MasterService.dto.regionDto.RegionDetailsDto;
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
	public Response<RegionDetailsDto> addRegion(RegionCreationDto regionCreationDto) {
		Result<RegionDetailsDto> res = new Result<>();
		Response<RegionDetailsDto> response = new Response<>();
		
		Region regionName = regionRepository.getRegionByName(regionCreationDto.getName());
		Region regionCode = regionRepository.getRegionByCode(regionCreationDto.getCode());
		
		
		if (regionName != null) {
			throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_EXISTS.getCode(),
					HttpStatusCode.RESOURCE_ALREADY_EXISTS, HttpStatusCode.RESOURCE_ALREADY_EXISTS.getMessage(), res);
		}
		if (regionCode != null) {
			throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_EXISTS.getCode(),
					HttpStatusCode.RESOURCE_ALREADY_EXISTS, HttpStatusCode.RESOURCE_ALREADY_EXISTS.getMessage(), res);
		}
		
		Region savedRegion = new Region();
		savedRegion.setCode(regionCreationDto.getCode());
		savedRegion.setName(regionCreationDto.getName());
		savedRegion.setEducationalInstitiute(new HashSet<>());
		savedRegion.setSchool(new HashSet<>());
		savedRegion = regionRepository.save(savedRegion);
		
		for(Integer schoolId : regionCreationDto.getSchoollId()) {
			School school = schoolRepository.getReferenceById(schoolId);
			school.setRegion(savedRegion);
			schoolRepository.save(school);
			savedRegion.getSchool().add(school);
		}
		
		for(Integer eduInstiId : regionCreationDto.getEduInstId()) {
			EducationalInstitution eduInsti = educationalInstitutionRepository.getReferenceById(eduInstiId);
			eduInsti.getRegion().add(savedRegion);
			educationalInstitutionRepository.save(eduInsti);
			savedRegion.getEducationalInstitiute().add(eduInsti);
		}
		savedRegion = regionRepository.save(savedRegion);
		
//		Region saveRegion = regionRepository.save(regionMapper.dtoToEntity(regionDto));
		response.setStatusCode(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getMessage());
		response.setResult(new Result<RegionDetailsDto>(regionMapper.toRegionDetails(savedRegion)));
		return response;
	}

	@Override
	public Response<List<RegionDetailsDto>> getRegionDetails(Integer PageNumber, Integer PageSize) {
		Result<List<RegionDetailsDto>> allRegion = new Result<>();
		Pageable paging = PageRequest.of(PageNumber, PageSize);
		Response<List<RegionDetailsDto>> getListofRegion = new Response<List<RegionDetailsDto>>();

		Page<Region> list = this.regionRepository.findAll(paging);
		List<RegionDetailsDto> regionDtos = list.toList().stream().map(region -> regionMapper.toRegionDetails(region)).collect(Collectors.toList());
		if (list.isEmpty()) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), allRegion);
		}
		allRegion.setData(regionDtos);
		getListofRegion.setStatusCode(HttpStatusCode.REGION_RETREIVED_SUCCESSFULLY.getCode());
		getListofRegion.setMessage(HttpStatusCode.REGION_RETREIVED_SUCCESSFULLY.getMessage());
		getListofRegion.setResult(allRegion);
		return getListofRegion;
	}

	@Override
	public Response<RegionDetailsDto> getRegionById(int id) {
		Response<RegionDetailsDto> getRegion = new Response<RegionDetailsDto>();
		Optional<Region> region = this.regionRepository.findById(id);
		Result<RegionDetailsDto> regionResult = new Result<>();
		if (!region.isPresent()) {
			throw new CustomException(HttpStatusCode.REGION_NOT_FOUND.getCode(), HttpStatusCode.REGION_NOT_FOUND,
					HttpStatusCode.REGION_NOT_FOUND.getMessage(), regionResult);
		}
		regionResult.setData(regionMapper.toRegionDetails(region.get()));
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

		for (EducationalInstitution eduInsti : region.get().getEducationalInstitiute()) {
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
//		List<Region> region = regionRepository.findAll();
//		ByteArrayInputStream out = RegionEducationalCsvHelper.regionCSV(region);
//		return out;
//	}
//
//	@Override
//	public ByteArrayInputStream Regionload() {
//		List<Region> region = regionRepository.findAll();
//		ByteArrayInputStream out = RegionSchoolCsvHelper.regionSchoolCSV(region);
//		return out;
//	}

	@Override
	public Response<RegionDto> getRegionByName(String name) {
		Response<RegionDto> getRegion = new Response<RegionDto>();
		Region region = regionRepository.getRegionByName(name);
		Result<RegionDto> regionResult = new Result<>();
		if (region == null) {
			throw new CustomException(HttpStatusCode.REGION_NOT_FOUND.getCode(), HttpStatusCode.REGION_NOT_FOUND,
					HttpStatusCode.REGION_NOT_FOUND.getMessage(), regionResult);
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
		int regionId = regionSchoolMappingDto.getRegionId();
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
					HttpStatusCode.NO_REGION_MATCH_WITH_ID, HttpStatusCode.NO_REGION_MATCH_WITH_ID.getMessage(), res);
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
	public Response<List<RegionDetailsDto>> getRegionwithSort(String field) {

		Result<List<RegionDetailsDto>> allRegionResult = new Result<>();

		Response<List<RegionDetailsDto>> getListofRegion = new Response<>();

		List<Region> list = this.regionRepository.findAll(Sort.by(Sort.Direction.ASC, field));
		List<RegionDetailsDto> regionDtos = list.stream().map(region -> regionMapper.toRegionDetails(region)).collect(Collectors.toList());

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
