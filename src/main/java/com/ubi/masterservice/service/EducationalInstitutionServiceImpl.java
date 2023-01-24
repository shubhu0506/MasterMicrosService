package com.ubi.masterservice.service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubi.masterservice.dto.classDto.TeacherDto;
import com.ubi.masterservice.dto.educationalInstitutiondto.*;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.regionDto.RegionDetailsDto;
import com.ubi.masterservice.dto.schoolDto.PrincipalDto;
import com.ubi.masterservice.dto.schoolDto.SchoolRegionDto;
import com.ubi.masterservice.dto.user.UserDto;
import com.ubi.masterservice.entity.School;
import com.ubi.masterservice.entity.ClassDetail;
import com.ubi.masterservice.entity.Student;
import com.ubi.masterservice.externalServices.UserFeignService;
import com.ubi.masterservice.mapper.SchoolMapper;
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
	private RegionMapper regionMapper;

	@Autowired
	private RegionRepository regionRepository;

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
			Region region = regionRepository.getReferenceById(regionId);
			if (region != null) educationalInstitution.getRegion().add(region);
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
						"Given Education Institute admin id is already mapped with another Insitute",
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

//		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(searchByField).ascending()
//				: Sort.by(searchByField).descending();


		Result<PaginationResponse<List<InstituteDto>>> allEducationalResult = new Result<>();
		Pageable paging = PageRequest.of(pageNumber, pageSize);

		Response<PaginationResponse<List<InstituteDto>>> response = new Response<>();

		Page<EducationalInstitution> list = this.educationalInstitutionRepository.findAll(paging);

		PaginationResponse<List<InstituteDto>> paginationResponse = null;

		List<InstituteDto> instituteDtos;

		Page<EducationalInstitution> eduData = null;

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

			instituteDtos = (eduData.stream().map(education -> educationalInstitutionMapper.toInstituteDto(education)).collect(Collectors.toList()));

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

		InstituteAdminDto instituteAdminDto = null;
		if(instituteCreationDto.getAdminId() != null && existingEducationalInstitution.getAdminId() != instituteCreationDto.getAdminId()){
			EducationalInstitution educationalInstitution1 = educationalInstitutionRepository.findByAdminId(instituteCreationDto.getAdminId());
			if(educationalInstitution1 != null){
				throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
						HttpStatusCode.BAD_REQUEST_EXCEPTION,
						"Given Education Institute admin id is already mapped with another Insitute",
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

	@Override
	public Response<List<RegionDetailsDto>> getAllRegionsByInstituteId(Integer instituteId) {
		Optional<EducationalInstitution> educationalInst = educationalInstitutionRepository.findById(instituteId);

		if (!educationalInst.isPresent()) {
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION,
					"No Insitute Found With Given Institute Id", new Result<>(null));
		}
		List<RegionDetailsDto> regionDetailsSet = new ArrayList<>();
		Set<Region> regions = educationalInst.get().getRegion();
		regionDetailsSet = regions.stream().filter(Objects::nonNull).map(region -> regionMapper.toRegionDetails(region)).collect(Collectors.toList());
		Response<List<RegionDetailsDto>> response = new Response<>();
		if(regionDetailsSet.isEmpty()){
			response.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
			response.setMessage("No Region Found");
			response.setResult(new Result<>(regionDetailsSet));
			return response;
		}
		response.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
		response.setMessage("Regions Retrived Sucessfully");
		response.setResult(new Result<>(regionDetailsSet));
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
	public Response<Set<SchoolRegionDto>> getAllSchoolByInstituteId(Integer instituteId) {
		Optional<EducationalInstitution> educationalInst = educationalInstitutionRepository.findById(instituteId);

		if (!educationalInst.isPresent()) {
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION,
					"No Insitute Found With Given Institute Id", new Result<>(null));
		}

		EducationalInstitution educationalInstitution = educationalInst.get();
		Set<School> schools = educationalInstitution.getSchool();
		Set<SchoolRegionDto> allSchools = schools.stream().filter(Objects::nonNull).map(school -> schoolMapper.toSchoolClassDto(school)).collect(Collectors.toSet());

		Set<Region> regions = educationalInstitution.getRegion();
		for(Region region:regions){
			Set<School> schools1 = region.getSchool();
			Set<SchoolRegionDto> allSchools1 = schools1.stream().filter(Objects::nonNull).map(school -> schoolMapper.toSchoolClassDto(school)).collect(Collectors.toSet());
			if(!allSchools1.isEmpty()){
				allSchools.addAll(allSchools1);
			}
		}

		Response<Set<SchoolRegionDto>> response = new Response<>();
		if(allSchools.isEmpty()){
			response.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
			response.setMessage("No Teachers Found");
			response.setResult(new Result<>(null));
			return response;
		}

		response.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
		response.setMessage("Teachers Retrived Successfully");
		response.setResult(new Result<>(allSchools));
		return response;
	}

}
