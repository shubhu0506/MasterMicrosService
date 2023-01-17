package com.ubi.masterservice.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubi.masterservice.dto.educationalInstitutiondto.*;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.regionDto.RegionAdminDto;
import com.ubi.masterservice.dto.user.UserDto;
import com.ubi.masterservice.externalServices.UserFeignService;
import com.ubi.masterservice.util.PermissionUtil;
import com.ubi.masterservice.dto.studentDto.StudentDetailsDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ubi.masterservice.dto.regionDto.RegionGet;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.entity.EducationalInstitution;
import com.ubi.masterservice.entity.Region;
import com.ubi.masterservice.error.CustomException;
import com.ubi.masterservice.error.HttpStatusCode;
import com.ubi.masterservice.error.Result;
import com.ubi.masterservice.mapper.EducationalInstitutionMapper;
import com.ubi.masterservice.mapper.RegionMapper;
import com.ubi.masterservice.repository.EducationalInstitutionRepository;
import com.ubi.masterservice.repository.RegionRepository;

@Service
public class EducationalInstitutionServiceImpl implements EducationalInstitutionService {

	private static  final Logger LOGGER= LoggerFactory.getLogger(EducationalInstitutionServiceImpl.class);
	@Autowired
	private EducationalInstitutionRepository educationalInstitutionRepository;

	@Autowired
	private EducationalInstitutionMapper educationalInstitutionMapper;

	@Autowired
	private RegionMapper regionMapper;

	@Autowired
	private RegionRepository regionRepository;

	@Autowired
	private PermissionUtil permissionUtil;

	@Autowired
	private UserFeignService userFeignService;

	private String topicName="master_topic_add";

	private String topicDelete="master_delete";

	private String topicUpdateName="master_topic_update";

	private NewTopic topic;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public EducationalInstitutionServiceImpl(NewTopic topic , KafkaTemplate<String, String> kafkaTemplate)
	{
		this.topic=topic;
		this.kafkaTemplate=kafkaTemplate;
	}

	@Override
	public Response<InstituteDto> addEducationalInstitution(
			InstituteCreationDto instituteCreationDto) {
		Result<InstituteDto> res = new Result<>();
		Response<InstituteDto> response = new Response<>();

		EducationalInstitution educationalInstitutionName = educationalInstitutionRepository
				.getEducationalInstitutionByeducationalInstitutionName(
						instituteCreationDto.getEducationalInstitutionName());

		EducationalInstitution educationalInstitutionCode = educationalInstitutionRepository
				.getEducationalInstitutionByeducationalInstitutionCode(
        
						instituteCreationDto.getEducationalInstitutionCode());


		if (educationalInstitutionName != null) {
			throw new CustomException(HttpStatusCode.EDUCATIONAL_INSTITUTION_NAME_ALREADY_EXISTS.getCode(),
					HttpStatusCode.EDUCATIONAL_INSTITUTION_NAME_ALREADY_EXISTS,
					HttpStatusCode.EDUCATIONAL_INSTITUTION_NAME_ALREADY_EXISTS.getMessage(), res);
		}

		if (educationalInstitutionCode != null) {
			throw new CustomException(HttpStatusCode.EDUCATIONAL_INSTITUTION_CODE_ALREADY_EXISTS.getCode(),
					HttpStatusCode.EDUCATIONAL_INSTITUTION_CODE_ALREADY_EXISTS,
					HttpStatusCode.EDUCATIONAL_INSTITUTION_CODE_ALREADY_EXISTS.getMessage(), res);
		}

		if (instituteCreationDto.getRegionId().isEmpty()) {
			throw new CustomException(HttpStatusCode.NO_REGION_ADDED.getCode(), HttpStatusCode.NO_REGION_ADDED,
					HttpStatusCode.NO_REGION_ADDED.getMessage(), res);
		}

		EducationalInstitution educationalInstitution = EducationalInstitution.builder().educationalInstitutionName(instituteCreationDto.getEducationalInstitutionName())
				.educationalInstitutionCode(instituteCreationDto.getEducationalInstitutionCode())
				.educationalInstitutionType(instituteCreationDto.getEducationalInstitutionType())
				.exemptionFlag(instituteCreationDto.getExemptionFlag())
				.state(instituteCreationDto.getState())
				.vvnAccount(instituteCreationDto.getVvnAccount())
				.strength(instituteCreationDto.getStrength())
				.region(new HashSet<>()).build();

		for (Integer regionId : instituteCreationDto.getRegionId()) {
			Region region = regionRepository.getReferenceById(regionId);
			if (region != null) educationalInstitution.getRegion().add(region);
		}

		InstituteAdminDto instituteAdminDto = null;
		if(instituteCreationDto.getAdminId() != null){
			String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
			ResponseEntity<Response<UserDto>> regionAdminResponse = userFeignService.getInstituteAdminById(currJwtToken,instituteCreationDto.getAdminId().toString());
			UserDto userDto = regionAdminResponse.getBody().getResult().getData();
			if(userDto != null) {
				instituteAdminDto = new InstituteAdminDto(userDto.getId(),userDto.getContactInfoDto().getFirstName(),userDto.getContactInfoDto().getLastName());
			}
			educationalInstitution.setAdminId(instituteCreationDto.getAdminId());
		}


		EducationalInstitution savedEducationalInstitution = educationalInstitutionRepository
				.save(educationalInstitution);

		InstituteDto instituteDto = educationalInstitutionMapper
				.toInstituteDto(savedEducationalInstitution);

		res.setData(instituteDto);
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
		kafkaTemplate.send(topicName,1,"Key1",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));

		return response;

	}

	@Override
	public Response<InstituteDto> getEducationalInstituteByName(String educationalInstitutionName) {
		Result<InstituteDto> res = new Result<>();
		res.setData(null);

		Response<InstituteDto> response = new Response<>();
		Optional<EducationalInstitution> educationalInst = this.educationalInstitutionRepository
				.findByeducationalInstitutionName(educationalInstitutionName);
		Result<InstituteDto> educationalInstitutionResult = new Result<>();

		if (!educationalInst.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_NAME_FOUND.getCode(),
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_NAME_FOUND,
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_NAME_FOUND.getMessage(), res);
		}
		InstituteDto instituteDto = educationalInstitutionMapper.toInstituteDto(educationalInst.get());
		res.setData(instituteDto);

		response.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getMessage());
		response.setResult(res);
		return response;
	}

	@Override
	public Response<PaginationResponse<List<InstituteDto>>> getAllEducationalInstitutions(Integer pageNumber,Integer pageSize) {

		Result<PaginationResponse<List<InstituteDto>>> allEducationalResult = new Result<>();
		Pageable paging = PageRequest.of(pageNumber, pageSize);

		Response<PaginationResponse<List<InstituteDto>>> response = new Response<>();

		List<InstituteDto> instituteDtos= new ArrayList<>();
		for (EducationalInstitution eduInsti : list) {
			instituteDtos.add(educationalInstitutionMapper.toInstituteDto(eduInsti));
		}

		PaginationResponse paginationResponse=new PaginationResponse<List<InstituteDto>>(instituteDtos,list.getTotalPages(),list.getTotalElements());

		if (list.isEmpty()) {
			throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getCode(),
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND,
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getMessage(), allEducationalResult);
		}


		allEducationalResult.setData(paginationResponse);
		response.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getMessage());
		response.setResult(allEducationalResult);
		return response;
	}

	@Override
	public Response<InstituteDto> deleteEducationalInstitution(int id) {
		Result<InstituteDto> res = new Result<>();
		res.setData(null);

		Optional<EducationalInstitution> educationalInst = educationalInstitutionRepository.findById(id);
		if (!educationalInst.isPresent()) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
		}

		for (Region region : educationalInst.get().getRegion()) {
			region.getEducationalInstitiute().remove(educationalInst.get());
			regionRepository.save(region);
		}

		educationalInst.get().setRegion(new HashSet<>());
		educationalInstitutionRepository.save(educationalInst.get());
		educationalInstitutionRepository.deleteById(id);

		res.setData(educationalInstitutionMapper.toInstituteDto(educationalInst.get()));
		Response<InstituteDto> response = new Response<>();
		response.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_DELETED.getMessage());
		response.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_DELETED.getCode());
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
		kafkaTemplate.send(topicDelete,1,"Key3",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));

		return response;
	}

	@Override
	public Response<InstituteDto> updateEducationalInstitution(
			InstituteCreationDto instituteCreationDto,Long instituteId) {
		Result<InstituteDto> res = new Result<>();

		res.setData(null);
		Optional<EducationalInstitution> existingEducationalContainer = educationalInstitutionRepository
				.findById(Integer.parseInt(instituteId.toString()));

		EducationalInstitution educationalInstitutionName = educationalInstitutionRepository
				.getEducationalInstitutionByeducationalInstitutionName(
						instituteCreationDto.getEducationalInstitutionName());

		EducationalInstitution educationalInstitutionCode = educationalInstitutionRepository
				.getEducationalInstitutionByeducationalInstitutionCode(
						instituteCreationDto.getEducationalInstitutionCode());



		if (!existingEducationalContainer.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getCode(),
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND,
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getMessage(), res);
		}

		EducationalInstitution existingEducationalInstitution = existingEducationalContainer.get();

		if (educationalInstitutionName != null && !existingEducationalInstitution.getEducationalInstitutionName().equals(instituteCreationDto.getEducationalInstitutionName())) {
			throw new CustomException(HttpStatusCode.EDUCATIONAL_INSTITUTION_NAME_ALREADY_EXISTS.getCode(),
					HttpStatusCode.EDUCATIONAL_INSTITUTION_NAME_ALREADY_EXISTS,
					HttpStatusCode.EDUCATIONAL_INSTITUTION_NAME_ALREADY_EXISTS.getMessage(), res);
		}

		if (educationalInstitutionCode != null && !existingEducationalInstitution.getEducationalInstitutionCode().equals(instituteCreationDto.getEducationalInstitutionCode())) {
			throw new CustomException(HttpStatusCode.EDUCATIONAL_INSTITUTION_CODE_ALREADY_EXISTS.getCode(),
					HttpStatusCode.EDUCATIONAL_INSTITUTION_CODE_ALREADY_EXISTS,
					HttpStatusCode.EDUCATIONAL_INSTITUTION_CODE_ALREADY_EXISTS.getMessage(), res);
		}

		EducationalInstitution educationalInstitution = EducationalInstitution.builder().id(Integer.parseInt(instituteId.toString()))
				.educationalInstitutionName(instituteCreationDto.getEducationalInstitutionName())
				.educationalInstitutionCode(instituteCreationDto.getEducationalInstitutionCode())
				.educationalInstitutionType(instituteCreationDto.getEducationalInstitutionType())
				.exemptionFlag(instituteCreationDto.getExemptionFlag())
				.state(instituteCreationDto.getState())
				.vvnAccount(instituteCreationDto.getVvnAccount())
				.strength(instituteCreationDto.getStrength())
				.region(new HashSet<>()).build();

		for (Integer regionId : instituteCreationDto.getRegionId()) {
			Region region = regionRepository.getReferenceById(regionId);
			if(region != null) {
				educationalInstitution.getRegion().add(region);
				region.getEducationalInstitiute().add(educationalInstitution);
				regionRepository.save(region);
			}
		}

		if (educationalInstitution.getRegion().isEmpty()) {
			throw new CustomException(HttpStatusCode.NO_REGION_ADDED.getCode(), HttpStatusCode.NO_REGION_ADDED,
					HttpStatusCode.NO_REGION_ADDED.getMessage(), res);
		}

		if(instituteCreationDto.getAdminId() != null){
			String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
			ResponseEntity<Response<UserDto>> regionAdminResponse = userFeignService.getInstituteAdminById(currJwtToken,instituteCreationDto.getAdminId().toString());
		}

		educationalInstitution.setAdminId(instituteCreationDto.getAdminId());

		EducationalInstitution updateEducationalInst = educationalInstitutionRepository.save(educationalInstitution);
		InstituteDto instituteDto = educationalInstitutionMapper.toInstituteDto(updateEducationalInst);

		Response<InstituteDto> response = new Response<>();
		res.setData(instituteDto);
		response.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_UPDATED.getMessage());
		response.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_UPDATED.getCode());
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
		kafkaTemplate.send(topicUpdateName,1, "Key2",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));

		return response;
	}

	@Override
	public Response<InstituteDto> getEduInstwithRegion(int id) {

		Response<InstituteDto> response = new Response<>();
		Result<InstituteDto> res = new Result<>();

		Optional<EducationalInstitution> educationalInst = this.educationalInstitutionRepository.findById(id);

		if (!educationalInst.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_MATCH_WITH_ID.getCode(),
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_MATCH_WITH_ID,
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_MATCH_WITH_ID.getMessage(), res);
		}

		InstituteDto instituteDto = educationalInstitutionMapper.toInstituteDto(educationalInst.get());
		res.setData(instituteDto);

		response.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getMessage());
		response.setResult(new Result<>(instituteDto));
		return response;

	}

//	@Override
//	public ByteArrayInputStream load() {
//		List<EducationalInstitution> eduInst = educationalInstitutionRepository.findAll();
//		ByteArrayInputStream out = EducationalInstitutionCsvHelper.educationCSV(eduInst);
//		return out;
//	}

	@Override
	public Response<List<InstituteDto>> getEduInstwithSort(String field) {

		Result<List<InstituteDto>> allEducationalResult = new Result<>();

		Response<List<InstituteDto>> response = new Response<>();

		List<EducationalInstitution> list = this.educationalInstitutionRepository
				.findAll(Sort.by(Sort.Direction.ASC, field));

		List<InstituteDto> instituteDtos = list.stream().filter(Objects::nonNull).map(eduInst -> educationalInstitutionMapper.toInstituteDto(eduInst)).collect(Collectors.toList());

		allEducationalResult.setData(instituteDtos);
		response.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getMessage());
		response.setResult(allEducationalResult);
		return response;
	}

}