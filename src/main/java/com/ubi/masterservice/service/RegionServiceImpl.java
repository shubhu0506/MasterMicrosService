package com.ubi.masterservice.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(RegionServiceImpl.class);
	@Autowired
	private RegionRepository regionRepository;

	@Autowired
	private RegionMapper regionMapper;

	private String topicName="master_topic_add";

	private String topicDelete="master_delete";

	private String topicUpdateName="master_topic_update";

	@Autowired
	private SchoolMapper schoolMapper;

	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
	private EducationalInstitutionRepository educationalInstitutionRepository;

	private NewTopic topic;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public RegionServiceImpl(NewTopic topic , KafkaTemplate<String, String> kafkaTemplate)
	{
		this.topic=topic;
		this.kafkaTemplate=kafkaTemplate;
	}

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
		res.setData(regionMapper.toRegionDetails(savedRegion));
		response.setStatusCode(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getMessage());
		response.setResult(res);
		ObjectMapper obj = new ObjectMapper();

		String jsonStr = null;
		try {
			jsonStr = obj.writeValueAsString(res.getData());
			LOGGER.info(jsonStr);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		kafkaTemplate.send(topicName,2,"Key1",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));

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
		res.setData(regionMapper.entityToDto(region));
		response.setMessage(HttpStatusCode.REGION_DELETED_SUCCESSFULLY.getMessage());
		response.setStatusCode(HttpStatusCode.REGION_DELETED_SUCCESSFULLY.getCode());
		response.setResult(res);

		ObjectMapper obj = new ObjectMapper();

		String jsonStr = null;
		try {
			jsonStr = obj.writeValueAsString(res.getData());
			LOGGER.info(jsonStr);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		kafkaTemplate.send(topicDelete,2,"Key3",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));

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
		res.setData(regionMapper.toRegionDetails(updateRegion));
		Response<RegionDetailsDto> response = new Response<>();
		response.setMessage(HttpStatusCode.REGION_UPDATED.getMessage());
		response.setStatusCode(HttpStatusCode.REGION_UPDATED.getCode());
		response.setResult(res);

		ObjectMapper obj = new ObjectMapper();

		String jsonStr = null;
		try {
			jsonStr = obj.writeValueAsString(res.getData());
			System.out.println(jsonStr);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		kafkaTemplate.send(topicUpdateName,2, "Key2",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
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
