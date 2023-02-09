package com.ubi.masterservice.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubi.masterservice.dto.classDto.TeacherDto;
import com.ubi.masterservice.dto.educationalInstitutiondto.*;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.regionDto.RegionDetailsDto;
import com.ubi.masterservice.dto.regionDto.RegionGet;
import com.ubi.masterservice.dto.schoolDto.SchoolRegionDto;
import com.ubi.masterservice.dto.studentDto.StudentDetailsDto;
import com.ubi.masterservice.dto.user.UserDto;
import com.ubi.masterservice.entity.School;
import com.ubi.masterservice.entity.Student;
import com.ubi.masterservice.externalServices.UserFeignService;
import com.ubi.masterservice.mapper.SchoolMapper;
import com.ubi.masterservice.mapper.StudentMapper;
import com.ubi.masterservice.repository.SchoolRepository;
import com.ubi.masterservice.repository.StudentRepository;
import com.ubi.masterservice.util.PermissionUtil;
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
	private StudentRepository studentRepository;

	@Autowired
	private StudentMapper studentMapper;

	@Autowired
	private RegionMapper regionMapper;

	@Autowired
	private RegionRepository regionRepository;

	@Autowired
	SchoolRepository schoolRepository;

	@Autowired
	private SchoolServiceImpl schoolService;

	@Autowired
	private PermissionUtil permissionUtil;

	@Autowired
	private UserFeignService userFeignService;

	@Autowired
	private SchoolMapper schoolMapper;

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
			Region region = regionRepository.findByIdIfNotDeleted(regionId);
			if (region != null){
				educationalInstitution.getRegion().add(region);
				region.getEducationalInstitiute().add(educationalInstitution);
				regionRepository.save(region);
				System.out.println("mapped region Id is --- " + region.getId());
			}
			else{
				throw new CustomException(HttpStatusCode.NO_REGION_ADDED.getCode(),
						HttpStatusCode.NO_REGION_ADDED,
						"Invalid region is being sent to map with institute", res);
			}
		}

		InstituteAdminDto instituteAdminDto = null;
		if(instituteCreationDto.getAdminId() != null){
			EducationalInstitution educationalInstitution1 = educationalInstitutionRepository.findByAdminId(instituteCreationDto.getAdminId());
			if(educationalInstitution1 != null){
				throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
						HttpStatusCode.BAD_REQUEST_EXCEPTION,
						"Given Education Institute admin id is already mapped with another Institute",
						res);
			}

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
	public Response<PaginationResponse<List<InstituteDto>>> getAllEducationalInstitutions(String fieldName,String searchByField,Integer pageNumber,Integer pageSize) {
//
//		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(fieldName).ascending()
//				: Sort.by(fieldName).descending();


		Result<PaginationResponse<List<InstituteDto>>> allEducationalResult = new Result<>();
		Pageable paging = PageRequest.of(pageNumber, pageSize);

		Response<PaginationResponse<List<InstituteDto>>> response = new Response<>();

		Page<EducationalInstitution> list = this.educationalInstitutionRepository.getAllAvailaibleEducationalInstitution(paging);

		PaginationResponse<List<InstituteDto>> paginationResponse = null;

		List<InstituteDto> instituteDtos;


		Page<EducationalInstitution> eduData =  this.educationalInstitutionRepository.findAll(paging);

		if (!fieldName.equals("*") && !searchByField.equals("*")) {
			if (fieldName.equalsIgnoreCase("educationalInstitutionCode")) {
				eduData = educationalInstitutionRepository.findByEducationalInstitutionCode(searchByField, paging);
			}

			if (fieldName.equalsIgnoreCase("educationalInstitutionName")) {
				eduData = educationalInstitutionRepository.findByEducationalInstitutionName(searchByField, paging);
			}

			if (fieldName.equalsIgnoreCase("educationalInstitutionType")) {
				eduData = educationalInstitutionRepository.findByEducationalInstitutionType(searchByField,paging);
			}

			if (fieldName.equalsIgnoreCase("strength")) {
				eduData = educationalInstitutionRepository.findByStrength(Long.parseLong(searchByField),paging);
			}

			if (fieldName.equalsIgnoreCase("state")) {
				eduData = educationalInstitutionRepository.findByState(searchByField,paging);
			}

			if (fieldName.equalsIgnoreCase("exemptionFlag")) {
				eduData = educationalInstitutionRepository.findByExemptionFlag(searchByField, paging);
			}

			if (fieldName.equalsIgnoreCase("vvnAccount")) {
				eduData = educationalInstitutionRepository.findByVvnAccount(Long.parseLong(searchByField), paging);
			}

			if (fieldName.equalsIgnoreCase("adminId")) {
				eduData = educationalInstitutionRepository.findAllByAdminId(Long.parseLong(searchByField), paging);
			}

			if (fieldName.equalsIgnoreCase("id")) {
				eduData = educationalInstitutionRepository.findAllById(Integer.parseInt(searchByField), paging);
			}
			instituteDtos = (eduData.toList().stream().map(education -> educationalInstitutionMapper.toInstituteDto(education)).collect(Collectors.toList()));

			 paginationResponse = new PaginationResponse<List<InstituteDto>>(instituteDtos, eduData.getTotalPages(), eduData.getTotalElements());
		}
	 else {
			instituteDtos = list.toList().stream().map(education -> educationalInstitutionMapper.toInstituteDto(education)).collect(Collectors.toList());

			 paginationResponse = new PaginationResponse<List<InstituteDto>>(instituteDtos, list.getTotalPages(), list.getTotalElements());
		}

//		for (EducationalInstitution eduInsti : list) {
//			instituteDtos.add(educationalInstitutionMapper.toInstituteDto(eduInsti));
//		}

	//	PaginationResponse paginationResponse=new PaginationResponse<List<InstituteDto>>(instituteDtos,list.getTotalPages(),list.getTotalElements());

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

		EducationalInstitution eduInst=educationalInst.get();

		if (eduInst.getIsDeleted() == true) {
			throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_DELETED.getCode(), HttpStatusCode.RESOURCE_ALREADY_DELETED,
					"Educational Institution with given Id is already deleted", res);
		}
		EducationalInstitution eduInstitution = new EducationalInstitution();
		eduInstitution = eduInst;

		if(eduInst.getIsDeleted() == true){
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
		}

		for (Region region : educationalInst.get().getRegion()) {
			region.getEducationalInstitiute().remove(educationalInst.get());
			regionRepository.save(region);
		}

		eduInst.setRegion(new HashSet<>());
		eduInst.setAdminId(null);
		eduInst.setIsDeleted(true);
		educationalInstitutionRepository.save(educationalInst.get());
		//educationalInstitutionRepository.deleteById(id);

		Response<InstituteDto> response = new Response<>();
		res.setData(educationalInstitutionMapper.toInstituteDto(eduInstitution));
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

		InstituteAdminDto instituteAdminDto = null;

		if(instituteCreationDto.getAdminId() != null && existingEducationalInstitution.getAdminId() != instituteCreationDto.getAdminId()){
			EducationalInstitution educationalInstitution1 = educationalInstitutionRepository.findByAdminId(instituteCreationDto.getAdminId());
			if(educationalInstitution1 != null){
				throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
						HttpStatusCode.BAD_REQUEST_EXCEPTION,
						"Given Education Institute admin id is already mapped with another Institute",
						res);
			}

			String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
			ResponseEntity<Response<UserDto>> regionAdminResponse = userFeignService.getInstituteAdminById(currJwtToken,instituteCreationDto.getAdminId().toString());
			UserDto userDto = regionAdminResponse.getBody().getResult().getData();
			if(userDto != null) {
				instituteAdminDto = new InstituteAdminDto(userDto.getId(),userDto.getContactInfoDto().getFirstName(),userDto.getContactInfoDto().getLastName());
			}

		}

		existingEducationalInstitution.setAdminId(instituteCreationDto.getAdminId());
		existingEducationalInstitution.setEducationalInstitutionCode(instituteCreationDto.getEducationalInstitutionCode());
		existingEducationalInstitution.setEducationalInstitutionName(instituteCreationDto.getEducationalInstitutionName());
		existingEducationalInstitution.setEducationalInstitutionType(instituteCreationDto.getEducationalInstitutionType());
		existingEducationalInstitution.setExemptionFlag(instituteCreationDto.getExemptionFlag());
		existingEducationalInstitution.setStrength(instituteCreationDto.getStrength());
		existingEducationalInstitution.setState(instituteCreationDto.getState());
		existingEducationalInstitution.setVvnAccount(instituteCreationDto.getVvnAccount());
		existingEducationalInstitution.setRegion(new HashSet<>());


		for (Integer regionId : instituteCreationDto.getRegionId()) {
			Region region = regionRepository.getReferenceById(regionId);
			if(region != null) {
				region.getEducationalInstitiute().add(existingEducationalInstitution);
				existingEducationalInstitution.getRegion().add(region);
				regionRepository.save(region);
			}
		}

		if (existingEducationalInstitution.getRegion().isEmpty()) {
			throw new CustomException(HttpStatusCode.NO_REGION_ADDED.getCode(), HttpStatusCode.NO_REGION_ADDED,
					HttpStatusCode.NO_REGION_ADDED.getMessage(), res);
		}


		EducationalInstitution updateEducationalInst = educationalInstitutionRepository.save(existingEducationalInstitution);
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

	@Override
	public Response<InstituteDto> getInstituteByAdminId(Long adminId) {
		EducationalInstitution educationalInstitution = educationalInstitutionRepository.findByAdminId(adminId);
		Response<InstituteDto> response = new Response<>();
		if(educationalInstitution == null){
			response.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
			response.setMessage("No Institute Found With Given Admin Id");
			response.setResult(new Result<>(null));
			return response;
		}
		InstituteDto instituteDto = educationalInstitutionMapper.toInstituteDto(educationalInstitution);
		response.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
		response.setMessage("Institute Retrived Successfully");
		response.setResult(new Result<>(instituteDto));
		return response;
	}

	@Override public Response<PaginationResponse<List<RegionDetailsDto>>> getAllRegionsByInstituteId(Integer instituteId,String fieldName,String fieldQuery,Integer pageNumber,Integer pageSize) {
		Optional<EducationalInstitution> educationalInst = educationalInstitutionRepository.findById(instituteId);

		if (!educationalInst.isPresent()) {
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION,
					"No Insitute Found With Given Institute Id", new Result<>(null));
		}

		Pageable paging = PageRequest.of(pageNumber, pageSize);
		Response<PaginationResponse<List<RegionDetailsDto>>> response = new Response<PaginationResponse<List<RegionDetailsDto>>>();
		Page<Region> regions = regionRepository.findAllRegionInsideInstitute(instituteId,paging);

		if(!fieldName.equals("*") && !fieldQuery.equals("*")) {
			if (fieldName.equalsIgnoreCase("code")) {
				regions = regionRepository.findByCodeAndInstituteId(fieldQuery, instituteId, paging);
			}
			if (fieldName.equalsIgnoreCase("name")) {
				regions = regionRepository.findByNameAndInstituteId(fieldQuery, instituteId, paging);
			}
		}

		List<RegionDetailsDto> regionDetailsDtos = (regions.stream().map(region -> regionMapper.toRegionDetails(region)).collect(Collectors.toList()));
		if(regionDetailsDtos.isEmpty()){
			response.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
			response.setMessage("No Region Found");
			response.setResult(new Result<>(null));
			return response;
		}
		PaginationResponse paginationResponse = new PaginationResponse<List<RegionDetailsDto>>(regionDetailsDtos,regions.getTotalPages(),regions.getTotalElements());

		response.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
		response.setMessage("Regions Retrived Sucessfully");
		response.setResult(new Result<>(paginationResponse));
		return response;
	}

	@Override
	public Response<Set<TeacherDto>> getAllTeacherByInstituteId(Integer instituteId) {
		Optional<EducationalInstitution> educationalInst = educationalInstitutionRepository.findById(instituteId);

		if (!educationalInst.isPresent()) {
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION,
					"No Insitute Found With Given Institute Id", new Result<>(null));
		}

		EducationalInstitution educationalInstitution = educationalInst.get();
		Set<School> schools = educationalInstitution.getSchool();
		Set<TeacherDto> allTeachers = new HashSet<>();
		for(School school:schools){
			Response<Set<TeacherDto>> teachers = schoolService.getAllTeacherBySchoolId(school.getSchoolId());
			if(teachers.getResult().getData() != null){
				for(TeacherDto teacherDto : teachers.getResult().getData()){
					allTeachers.add(teacherDto);
				}
			}
		}

		for(Region region : educationalInstitution.getRegion()){
			Set<School> school1 = region.getSchool();
			for(School school:school1){
				Response<Set<TeacherDto>> teachers = schoolService.getAllTeacherBySchoolId(school.getSchoolId());
				if(teachers.getResult().getData() != null){
					for(TeacherDto teacherDto : teachers.getResult().getData()){
						allTeachers.add(teacherDto);
					}
				}
			}
		}

		Response<Set<TeacherDto>> response = new Response<>();
		if(schools.isEmpty()){
			response.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
			response.setMessage("No Teachers Found");
			response.setResult(new Result<>(null));
			return response;
		}

		response.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
		response.setMessage("Teachers Retrived Successfully");
		response.setResult(new Result<>(allTeachers));
		return response;
	}

	@Override
	public Response<PaginationResponse<Set<SchoolRegionDto>>> getAllSchoolByInstituteId(Integer instituteId,Boolean isCollege,String fieldName,String fieldQuery,Integer pageNumber,Integer pageSize) {

		Optional<EducationalInstitution> educationalInst = educationalInstitutionRepository.findById(instituteId);

		if (!educationalInst.isPresent()) {
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION,
					"No Insitute Found With Given Institute Id", new Result<>(null));
		}

		Pageable paging = PageRequest.of(pageNumber, pageSize);
		Page<School> schools = null;

		if (!fieldName.equals("*") && !fieldQuery.equals("*")) {
			if (fieldName.equalsIgnoreCase("code")) {
				schools = schoolRepository.getAllSchoolByCodeAndInstituteId(fieldQuery,instituteId,isCollege, paging);
			}

			if (fieldName.equalsIgnoreCase("name")) {
				schools = schoolRepository.getAllSchoolByNameAndInstituteId(fieldQuery,instituteId,isCollege, paging);
			}

			if (fieldName.equalsIgnoreCase("email")) {
				schools = schoolRepository.getAllSchoolByEmailAndInstituteId(fieldQuery,instituteId,isCollege, paging);
			}

			if (fieldName.equalsIgnoreCase("exemptionFlag")) {
				if(fieldQuery.equalsIgnoreCase("true")){
					schools = schoolRepository.getAllSchoolByExemptionFlagAndInstituteId(true,instituteId,isCollege, paging);
				}
				else schools = schoolRepository.getAllSchoolByExemptionFlagAndInstituteId(false,instituteId,isCollege, paging);
			}

			if (fieldName.equalsIgnoreCase("vvnAccount")) {
				schools = schoolRepository.getAllSchoolByVVNAccountAndInstituteId(Integer.parseInt(fieldQuery),instituteId,isCollege, paging);
			}

			if (fieldName.equalsIgnoreCase("shift")) {
				schools = schoolRepository.getAllSchoolByShiftAndInstituteId(fieldQuery,instituteId,isCollege, paging);
			}
		}

		if(schools == null) schools = schoolRepository.getAllSchoolByInstituteId(instituteId,isCollege,paging);
		Set<SchoolRegionDto> schoolRegionDtos = (schools.toList().stream().filter(Objects::nonNull).map(school -> schoolMapper.toSchoolClassDto(school)).collect(Collectors.toSet()));

		PaginationResponse<Set<SchoolRegionDto>> paginationResponse = new PaginationResponse<Set<SchoolRegionDto>>(schoolRegionDtos, schools.getTotalPages(), schools.getTotalElements());
		Result<PaginationResponse<Set<SchoolRegionDto>>> result = new Result<>(paginationResponse);
		Response<PaginationResponse<Set<SchoolRegionDto>>> response = new Response<>();

		if(schoolRegionDtos.isEmpty()){
			response.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
			response.setMessage("No Schools Found");
			response.setResult(new Result<>(null));
			return response;
		}

		response.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
		response.setMessage("Schools Retrived Successfully");
		response.setResult(result);
		return response;
	}


	public Response<PaginationResponse<List<StudentDetailsDto>>> getStudentsByInstituteId(Integer instituteId,String fieldName, String searchByField, Integer PageNumber, Integer PageSize) throws ParseException {

		Optional<EducationalInstitution> educationalInst = educationalInstitutionRepository.findById(instituteId);

		if (!educationalInst.isPresent()) {
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION,
					"No Insitute Found With Given Institute Id", new Result<>(null));
		}

		Pageable paging = PageRequest.of(PageNumber, PageSize);
		Page<Student> students = null;

		String strDateRegEx ="^((?:19|20)[0-9][0-9])-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$";
		if(!fieldName.equals("*") && !searchByField.equals("*"))
		{
			if(searchByField.matches(strDateRegEx)) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
				Date localDate = formatter.parse(searchByField);
				if(fieldName.equalsIgnoreCase("dateOfBirth")) students = studentRepository.findStudentsByDOBAndInstituteId(localDate,instituteId,paging);
				else students = studentRepository.findStudentsByDOJAndInstituteId(localDate,instituteId,paging);
			} else {
				if(fieldName.equalsIgnoreCase("studentFirstName")) {
					students = studentRepository.findStudentsByFirstNameAndInstituteId(searchByField,instituteId, paging);
				}
				if(fieldName.equalsIgnoreCase("studentLastName")) {
					students = studentRepository.findStudentsByLastNameAndInstituteId(searchByField,instituteId, paging);
				}
				if(fieldName.equalsIgnoreCase("fullName")) {
					students = studentRepository.findStudentsByFullNameAndInstituteId(searchByField,instituteId, paging);
				}
				if(fieldName.equalsIgnoreCase("category")) {
					students = studentRepository.findStudentsByCategoryAndInstituteId(searchByField,instituteId, paging);
				}
				if(fieldName.equalsIgnoreCase("minority")) {
					students = studentRepository.findStudentsByMinorityAndInstituteId(searchByField,instituteId, paging);
				}
				if(fieldName.equalsIgnoreCase("fatherName")) {
					students = studentRepository.findStudentsByFatherNameAndInstituteId(searchByField,instituteId, paging);
				}
				if(fieldName.equalsIgnoreCase("motherName")) {
					students = studentRepository.findStudentsByMotherNameAndInstituteId(searchByField,instituteId, paging);
				}
				if(fieldName.equalsIgnoreCase("gender")) {
					students = studentRepository.findStudentsByGenderAndInstituteId(searchByField,instituteId, paging);
				}
				if(fieldName.equalsIgnoreCase("verifiedByTeacher")) {
					students = studentRepository.findStudentsByVerifiedByTeacherAndInstituteId(Boolean.parseBoolean(searchByField),instituteId,paging);
				}
				if(fieldName.equalsIgnoreCase("currentStatus")) {
					students = studentRepository.findStudentsByCurrentStatusAndInstituteId(searchByField,instituteId,paging);
				}
				if(fieldName.equalsIgnoreCase("verifiedByPrincipal")) {
					students = studentRepository.findStudentsByVerifiedByPrincipalAndInstituteId(Boolean.parseBoolean(searchByField),instituteId,paging);
				}
				if(fieldName.equalsIgnoreCase("isActivate")) {
					students = studentRepository.findStudentsByIsActivateAndInstituteId(Boolean.parseBoolean(searchByField),instituteId,paging);
				}
			}
		} else students = studentRepository.findStudentsByInstituteId(instituteId,paging);

		Response<PaginationResponse<List<StudentDetailsDto>>> response = new Response<>();
		if (students == null || students.isEmpty()) {
			response.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
			response.setMessage("No Student Found");
			response.setResult( new Result(null) );
			return response;
		}

		List<StudentDetailsDto> studentList = students.toList().stream().filter(Objects::nonNull).map(student -> studentMapper.toStudentDetails(student)).collect(Collectors.toList());

		PaginationResponse<List<StudentDetailsDto>> paginationResponse = new PaginationResponse<>(studentList,students.getTotalPages(),students.getTotalElements());

		Result<PaginationResponse<List<StudentDetailsDto>>> result = new Result<>();
		result.setData(paginationResponse);

		response.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
		response.setMessage("Student retrived");
		response.setResult(result);

		return response;
	}
}
