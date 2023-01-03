package com.ubi.masterservice.service;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.ubi.masterservice.dto.pagination.PaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ubi.masterservice.dto.regionDto.RegionCreationDto;
import com.ubi.masterservice.dto.regionDto.RegionDetailsDto;
import com.ubi.masterservice.dto.regionDto.RegionDto;
import com.ubi.masterservice.dto.regionDto.RegionSchoolDto;
import com.ubi.masterservice.dto.regionDto.RegionSchoolMappingDto;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.entity.EducationalInstitution;
import com.ubi.masterservice.entity.Region;
import com.ubi.masterservice.entity.School;
import com.ubi.masterservice.error.CustomException;
import com.ubi.masterservice.error.HttpStatusCode;
import com.ubi.masterservice.error.Result;
import com.ubi.masterservice.mapper.RegionMapper;
import com.ubi.masterservice.mapper.SchoolMapper;
import com.ubi.masterservice.repository.EducationalInstitutionRepository;
import com.ubi.masterservice.repository.RegionRepository;
import com.ubi.masterservice.repository.SchoolRepository;


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
			throw new CustomException(HttpStatusCode.REGION_NAME_DUPLICATE.getCode(),
					HttpStatusCode.REGION_NAME_DUPLICATE, HttpStatusCode.REGION_NAME_DUPLICATE.getMessage(), res);
		}
		if (regionCode != null) {
			throw new CustomException(HttpStatusCode.REGION_CODE_DUPLICATE.getCode(),
					HttpStatusCode.REGION_CODE_DUPLICATE, HttpStatusCode.REGION_CODE_DUPLICATE.getMessage(), res);
		}

		Region savedRegion = new Region();
		savedRegion.setCode(regionCreationDto.getCode());
		savedRegion.setName(regionCreationDto.getName());
		savedRegion.setEducationalInstitiute(new HashSet<>());
		savedRegion.setSchool(new HashSet<>());
		savedRegion = regionRepository.save(savedRegion);

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
	public Response<PaginationResponse<List<RegionDetailsDto>>> getRegionDetails(Integer PageNumber, Integer PageSize) {
		Result<PaginationResponse<List<RegionDetailsDto>>> allRegion = new Result<>();
		Pageable paging = PageRequest.of(PageNumber, PageSize);
		Response<PaginationResponse<List<RegionDetailsDto>>> getListofRegion = new Response<PaginationResponse<List<RegionDetailsDto>>>();

		Page<Region> list = this.regionRepository.findAll(paging);
		List<RegionDetailsDto> regionDtos = list.toList().stream().map(region -> regionMapper.toRegionDetails(region)).collect(Collectors.toList());
		if (list.isEmpty()) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), allRegion);
		}

		PaginationResponse paginationResponse = new PaginationResponse<List<RegionDetailsDto>>(regionDtos,list.getTotalPages(),list.getTotalElements());

		allRegion.setData(paginationResponse);
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
		Optional<Region> regionTemp = regionRepository.findById(id);
		if (!regionTemp.isPresent()) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
		}
		Region region = regionTemp.get();
		Set<EducationalInstitution> educationalInstitutionSet = region.getEducationalInstitiute();
		for (EducationalInstitution eduInsti : educationalInstitutionSet) {
			eduInsti.getRegion().remove(region);
			educationalInstitutionRepository.save(eduInsti);
		}

		Set<School> schoolSet = region.getSchool();
		for(School school:schoolSet){
			region.getSchool().remove(school);
			school.setRegion(null);
			schoolRepository.save(school);
		}

		region.setEducationalInstitiute(new HashSet<>());
		regionRepository.save(region);
		regionRepository.deleteById(id);
		Response<RegionDto> response = new Response<>();
		response.setMessage(HttpStatusCode.REGION_DELETED_SUCCESSFULLY.getMessage());
		response.setStatusCode(HttpStatusCode.REGION_DELETED_SUCCESSFULLY.getCode());
		response.setResult(new Result<RegionDto>(regionMapper.entityToDto(region)));
		return response;
	}

	@Override
	public Response<RegionDetailsDto> updateRegionDetails(RegionDto regionDto) {
		Result<RegionDetailsDto> res = new Result<>();

		res.setData(null);
		Optional<Region> existingRegionContainer = regionRepository.findById(regionDto.getId());
		if (!existingRegionContainer.isPresent()) {
			throw new CustomException(HttpStatusCode.REGION_NOT_FOUND.getCode(), HttpStatusCode.REGION_NOT_FOUND,
					HttpStatusCode.REGION_NOT_FOUND.getMessage(), res);
		}
		Region region = existingRegionContainer.get();

		if(!region.getCode().equals(regionDto.getCode())){
			System.out.println(region.getCode() + " --- " + regionDto.getCode());
			Region regionWithSameCode = regionRepository.getRegionByCode(regionDto.getCode());
			if(regionWithSameCode != null) {
				throw new CustomException(HttpStatusCode.REGION_CODE_DUPLICATE.getCode(), HttpStatusCode.REGION_CODE_DUPLICATE,
						HttpStatusCode.REGION_CODE_DUPLICATE.getMessage(), res);
			}
		}

		if(!region.getName().equals(regionDto.getName())){
			Region regionWithSameName = regionRepository.getRegionByName(regionDto.getName());
			if(regionWithSameName != null) {
				throw new CustomException(HttpStatusCode.REGION_NAME_DUPLICATE.getCode(), HttpStatusCode.REGION_NAME_DUPLICATE,
						HttpStatusCode.REGION_NAME_DUPLICATE.getMessage(), res);
			}
		}

		region.setCode(regionDto.getCode());
		region.setName(regionDto.getName());
		Set<EducationalInstitution> educationalInstitutionSet = region.getEducationalInstitiute();
		for(EducationalInstitution educationalInstitution:educationalInstitutionSet){
			educationalInstitution.getRegion().remove(region);
			region.getEducationalInstitiute().remove(educationalInstitution);
			educationalInstitutionRepository.save(educationalInstitution);
		}
		Region updateRegion = regionRepository.save(region);

		for(Integer educationId:regionDto.getEduInstId()){
			EducationalInstitution educationalInstitution = educationalInstitutionRepository.getReferenceById(educationId);
			educationalInstitution.getRegion().add(region);
			educationalInstitutionRepository.save(educationalInstitution);
			region.getEducationalInstitiute().add(educationalInstitution);
		}

		updateRegion = regionRepository.save(region);
		Response<RegionDetailsDto> response = new Response<>();
		response.setMessage(HttpStatusCode.REGION_UPDATED.getMessage());
		response.setStatusCode(HttpStatusCode.REGION_UPDATED.getCode());
		response.setResult(new Result<>(regionMapper.toRegionDetails(updateRegion)));
		return response;
	}

//	@Override
//	public ByteArrayInputStream load() {
//		List<Region> region = regionRepository.findAll();
//		ByteArrayInputStream out = RegionEducationalCsvHelper.regionCSV(region);
//		return out;
//	}

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
