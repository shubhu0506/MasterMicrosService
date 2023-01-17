package com.ubi.masterservice.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubi.masterservice.dto.classDto.ClassDto;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.schoolDto.PrincipalDto;
import com.ubi.masterservice.dto.schoolDto.SchoolDto;
import com.ubi.masterservice.dto.schoolDto.SchoolRegionDto;
import com.ubi.masterservice.dto.user.UserDto;
import com.ubi.masterservice.entity.ClassDetail;
import com.ubi.masterservice.entity.EducationalInstitution;
import com.ubi.masterservice.entity.Region;
import com.ubi.masterservice.entity.School;
import com.ubi.masterservice.error.CustomException;
import com.ubi.masterservice.error.HttpStatusCode;
import com.ubi.masterservice.error.Result;
import com.ubi.masterservice.externalServices.UserFeignService;
import com.ubi.masterservice.mapper.ClassMapper;
import com.ubi.masterservice.mapper.EducationalInstitutionMapper;
import com.ubi.masterservice.mapper.RegionMapper;
import com.ubi.masterservice.mapper.SchoolMapper;
import com.ubi.masterservice.repository.ClassRepository;
import com.ubi.masterservice.repository.EducationalInstitutionRepository;
import com.ubi.masterservice.repository.RegionRepository;
import com.ubi.masterservice.repository.SchoolRepository;
import com.ubi.masterservice.util.PermissionUtil;

@Service
public class SchoolServiceImpl implements SchoolService {

	private static  final Logger LOGGER = LoggerFactory.getLogger(SchoolServiceImpl.class);

	@Autowired
	private SchoolMapper schoolMapper;
	
	@Autowired
	private PermissionUtil permissionUtil;

	@Autowired
    UserFeignService userFeignService;

	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
	private ClassRepository classRepository;

	@Autowired
	private EducationalInstitutionRepository educationalRepository;

	@Autowired
	private EducationalInstitutionMapper educationalMapper;

	@Autowired
	private ClassMapper classMapper;

	@Autowired
	private RegionMapper regionMapper;

	private String topicName="master_topic_add";

	private String topicDelete="master_delete";

	private String topicUpdateName="master_topic_update";

	private NewTopic topic;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public SchoolServiceImpl(NewTopic topic , KafkaTemplate<String, String> kafkaTemplate)
	{
		this.topic=topic;
		this.kafkaTemplate=kafkaTemplate;
	}

	@Override
	public Response<SchoolRegionDto> addSchool(SchoolDto schoolDto) {

		Result<SchoolRegionDto> res = new Result<>();

		Response<SchoolRegionDto> response = new Response<>();

		School schoolName = schoolRepository.getSchoolByName(schoolDto.getName());
		School schoolCode = schoolRepository.getSchoolByCode(schoolDto.getCode());

		if (schoolName != null) {
			throw new CustomException(HttpStatusCode.SCHOOL_NAME_ALREADY_EXISTS.getCode(),
					HttpStatusCode.SCHOOL_NAME_ALREADY_EXISTS, HttpStatusCode.SCHOOL_NAME_ALREADY_EXISTS.getMessage(),
					res);
		}

		if (schoolCode != null) {
			throw new CustomException(HttpStatusCode.SCHOOL_CODE_ALREADY_EXISTS.getCode(),
					HttpStatusCode.SCHOOL_CODE_ALREADY_EXISTS, HttpStatusCode.SCHOOL_CODE_ALREADY_EXISTS.getMessage(),
					res);
		}

		School school = new School();
		school.setCode(schoolDto.getCode());
		school.setName(schoolDto.getName());
		school.setEmail(schoolDto.getEmail());
		school.setContact(schoolDto.getContact());
		school.setAddress(schoolDto.getAddress());
		school.setType(schoolDto.getType());
		school.setStrength(schoolDto.getStrength());
		school.setShift(schoolDto.getShift());
		school.setIsCollege(schoolDto.getIsCollege());
		school.setExemptionFlag(schoolDto.isExemptionFlag());
		school.setVvnAccount(schoolDto.getVvnAccount());
		school.setVvnFund(schoolDto.getVvnFund());
		
		String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
		
		
		ResponseEntity<Response<UserDto>> principalResponse = userFeignService.getPrincipalById(currJwtToken,schoolDto.getPrincipalId().toString());
		PrincipalDto principalDto = null;
		UserDto userDto = principalResponse.getBody().getResult().getData();
		if(userDto != null) {
			principalDto = new PrincipalDto(userDto.getId(),userDto.getContactInfoDto().getFirstName(),userDto.getContactInfoDto().getLastName());
		}
		
		school.setPrincipalId(schoolDto.getPrincipalId());
		
		school.setRegion(regionRepository.getReferenceById(schoolDto.getRegionId()));
		
		school.setClassDetail(new HashSet<>());
		
		for (Long classId : schoolDto.getClassId()) {
			// System.out.println(classId);
			ClassDetail classDetail = classRepository.getReferenceById(classId);
			if (classDetail != null) {
				school.getClassDetail().add(classDetail);
				classDetail.setSchool(school);
			}
		}
		if (schoolDto.getEducationalInstitutionId() != 0) {
			EducationalInstitution educationalInstitution = educationalRepository
					.getReferenceById(schoolDto.getEducationalInstitutionId());
			if (educationalInstitution != null) {
				school.setEducationalInstitution(educationalInstitution);
			}
		}

		School savedSchool = schoolRepository.save(school);

		SchoolRegionDto schoolRegionDto = schoolMapper.toSchoolClassDto(savedSchool);
		schoolRegionDto.setPrincipalDto(principalDto);
		res.setData(schoolRegionDto);
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
		kafkaTemplate.send(topicName,3,"Key1",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
		return response;
	}

	@Override
	public Response<PaginationResponse<List<SchoolRegionDto>>> getAllSchools(Integer PageNumber, Integer PageSize) {

		Result<PaginationResponse<List<SchoolRegionDto>>> allSchoolResult = new Result<>();
		Pageable paging = PageRequest.of(PageNumber, PageSize);
		Response<PaginationResponse<List<SchoolRegionDto>>> getListofSchools = new Response<>();

		//Page<School> list = this.schoolRepository.findAll(paging);
		Page<School> list = this.schoolRepository.findByisCollege(false, paging);
		
		List<SchoolRegionDto> schoolDtos = new ArrayList<>();
		for (School school : list) {
			SchoolRegionDto schoolRegionDto = new SchoolRegionDto();
			schoolRegionDto.setSchoolDto(schoolMapper.entityToDtos(school));
	
			String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
			
			ResponseEntity<Response<UserDto>> principalResponse = userFeignService.getPrincipalById(currJwtToken,school.getPrincipalId().toString());
			PrincipalDto principalDto = null;
			UserDto userDto = principalResponse.getBody().getResult().getData();
			if(userDto != null) {
				principalDto = new PrincipalDto(userDto.getId(),userDto.getContactInfoDto().getFirstName(),userDto.getContactInfoDto().getLastName());
			}
			
			schoolRegionDto.setRegionDto(regionMapper.toDto(school.getRegion()));
			schoolRegionDto.setPrincipalDto(principalDto);
			Set<ClassDto> classDto = school.getClassDetail().stream()
					.map(classDetail -> classMapper.entityToDto(classDetail)).collect(Collectors.toSet());

			schoolRegionDto.setClassDto(classDto);
			schoolDtos.add(schoolRegionDto);

			schoolRegionDto.setEducationalInstitutionDto(educationalMapper.toDto(school.getEducationalInstitution()));
		}

		if (list.isEmpty()) {
			throw new CustomException(HttpStatusCode.NO_SCHOOL_FOUND.getCode(), HttpStatusCode.NO_SCHOOL_FOUND,
					HttpStatusCode.NO_SCHOOL_FOUND.getMessage(), allSchoolResult);
		}

		PaginationResponse paginationResponse = new PaginationResponse<List<SchoolRegionDto>>(schoolDtos,
				list.getTotalPages(), list.getTotalElements());

		allSchoolResult.setData(paginationResponse);
		getListofSchools.setStatusCode(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getCode());
		getListofSchools.setMessage(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getMessage());
		getListofSchools.setResult(allSchoolResult);
		return getListofSchools;
	}

	@Override
	public Response<PaginationResponse<List<SchoolRegionDto>>> getAllColleges(Integer PageNumber, Integer PageSize) {

		Result<PaginationResponse<List<SchoolRegionDto>>> allCollegeResult = new Result<>();
		Response<PaginationResponse<List<SchoolRegionDto>>> getListofColleges = new Response<>();

		Pageable paging = PageRequest.of(PageNumber, PageSize);
		Response<PaginationResponse<List<SchoolRegionDto>>> getListofSchools = new Response<>();

		Page<School> list = this.schoolRepository.findByisCollege(true, paging);
		List<SchoolRegionDto> schoolDtos = new ArrayList<>();

		for (School school : list) {
			SchoolRegionDto schoolRegionDto = new SchoolRegionDto();
			schoolRegionDto.setSchoolDto(schoolMapper.entityToDtos(school));
			
            String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
			
			ResponseEntity<Response<UserDto>> principalResponse = userFeignService.getPrincipalById(currJwtToken,school.getPrincipalId().toString());
			PrincipalDto principalDto = null;
			UserDto userDto = principalResponse.getBody().getResult().getData();
			if(userDto != null) {
				principalDto = new PrincipalDto(userDto.getId(),userDto.getContactInfoDto().getFirstName(),userDto.getContactInfoDto().getLastName());
			}
			
			

			schoolRegionDto.setRegionDto(regionMapper.toDto(school.getRegion()));
			schoolRegionDto.setPrincipalDto(principalDto);
			Set<ClassDto> classDto = school.getClassDetail().stream()
					.map(classDetail -> classMapper.entityToDto(classDetail)).collect(Collectors.toSet());

			schoolRegionDto.setClassDto(classDto);
			schoolDtos.add(schoolRegionDto);

			schoolRegionDto.setEducationalInstitutionDto(educationalMapper.toDto(school.getEducationalInstitution()));
		}

		if (list.isEmpty()) {
			throw new CustomException(HttpStatusCode.NO_COLLEGE_FOUND.getCode(), HttpStatusCode.NO_COLLEGE_FOUND,
					HttpStatusCode.NO_COLLEGE_FOUND.getMessage(), allCollegeResult);
		}

		PaginationResponse paginationResponse = new PaginationResponse<List<SchoolRegionDto>>(schoolDtos,
				list.getTotalPages(), list.getTotalElements());

		allCollegeResult.setData(paginationResponse);
		getListofColleges.setStatusCode(HttpStatusCode.COLLEGE_RETRIVED_SUCCESSFULLY.getCode());
		getListofColleges.setMessage(HttpStatusCode.COLLEGE_RETRIVED_SUCCESSFULLY.getMessage());
		getListofColleges.setResult(allCollegeResult);
		return getListofColleges;
	}

	@Override
	public Response<SchoolRegionDto> getSchoolById(int schoolId) {

		Response<SchoolRegionDto> getSchool = new Response<>();
		Optional<School> sch = this.schoolRepository.findById(schoolId);
		Result<SchoolRegionDto> schoolResult = new Result<>();
		if (!sch.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_SCHOOL_MATCH_WITH_ID.getCode(),
					HttpStatusCode.NO_SCHOOL_MATCH_WITH_ID, HttpStatusCode.NO_SCHOOL_MATCH_WITH_ID.getMessage(),
					schoolResult);
		}

		SchoolRegionDto schoolRegionDto = new SchoolRegionDto();
		schoolRegionDto.setSchoolDto(schoolMapper.entityToDto(sch.get()));
		
		String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
		
		ResponseEntity<Response<UserDto>> principalResponse = userFeignService.getPrincipalById(currJwtToken,sch.get().getPrincipalId().toString());
		PrincipalDto principalDto = null;
		UserDto userDto = principalResponse.getBody().getResult().getData();
		if(userDto != null) {
			principalDto = new PrincipalDto(userDto.getId(),userDto.getContactInfoDto().getFirstName(),userDto.getContactInfoDto().getLastName());
		}
		
		
		schoolRegionDto.setRegionDto(regionMapper.toDto(sch.get().getRegion()));
		schoolRegionDto.setClassDto(classMapper.entitiesToDto(sch.get().getClassDetail()));
		schoolRegionDto.setEducationalInstitutionDto(educationalMapper.toDto(sch.get().getEducationalInstitution()));
		schoolRegionDto.setPrincipalDto(principalDto);
		schoolResult.setData(schoolRegionDto);
		getSchool.setStatusCode(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getCode());
		getSchool.setMessage(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getMessage());
		getSchool.setResult(schoolResult);
		return getSchool;

	}

	@Override
	public Response<SchoolRegionDto> getSchoolByName(String name) {

		Result<SchoolRegionDto> res = new Result<>();
		Response<SchoolRegionDto> getSchoolName = new Response<>();
		Optional<School> sch = this.schoolRepository.findByname(name);
		Result<SchoolRegionDto> schoolResult = new Result<>();
		if (!sch.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_SCHOOL_NAME_FOUND.getCode(),
					HttpStatusCode.NO_SCHOOL_NAME_FOUND, HttpStatusCode.NO_SCHOOL_NAME_FOUND.getMessage(), res);
		}
		SchoolRegionDto schoolRegionDto = new SchoolRegionDto();
		schoolRegionDto.setSchoolDto(schoolMapper.entityToDto(sch.get()));
        String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
		
		ResponseEntity<Response<UserDto>> principalResponse = userFeignService.getPrincipalById(currJwtToken,sch.get().getPrincipalId().toString());
		PrincipalDto principalDto = null;
		UserDto userDto = principalResponse.getBody().getResult().getData();
		if(userDto != null) {
			principalDto = new PrincipalDto(userDto.getId(),userDto.getContactInfoDto().getFirstName(),userDto.getContactInfoDto().getLastName());
		}
		schoolRegionDto.setRegionDto(regionMapper.toDto(sch.get().getRegion()));
		schoolRegionDto.setClassDto(classMapper.entitiesToDto(sch.get().getClassDetail()));
		schoolRegionDto.setEducationalInstitutionDto(educationalMapper.toDto(sch.get().getEducationalInstitution()));
		schoolRegionDto.setPrincipalDto(principalDto);
		schoolResult.setData(schoolRegionDto);
		getSchoolName.setStatusCode(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getCode());
		getSchoolName.setMessage(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getMessage());
		getSchoolName.setResult(schoolResult);
		return getSchoolName;
	}

	@Override
	public Response<SchoolDto> deleteSchoolById(int schoolId) {
		Result<SchoolDto> res = new Result<>();
		res.setData(null);
		Optional<School> school = schoolRepository.findById(schoolId);
		if (!school.isPresent()) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
		}
		Region region = school.get().getRegion();
		region.getSchool().remove(school.get());
		regionRepository.save(region);
		for (ClassDetail classDetail : school.get().getClassDetail()) {
			classDetail.setSchool(null);
			classRepository.save(classDetail);

			EducationalInstitution educationalInstitution = school.get().getEducationalInstitution();
			educationalInstitution.getSchool().remove(school.get());
			educationalRepository.save(educationalInstitution);

		}

		schoolRepository.deleteById(schoolId);
		Response<SchoolDto> response = new Response<>();
		res.setData(schoolMapper.entityToDto(school.get()));
		response.setMessage(HttpStatusCode.SCHOOL_DELETED.getMessage());
		response.setStatusCode(HttpStatusCode.SCHOOL_DELETED.getCode());
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
		kafkaTemplate.send(topicDelete,3,"Key3",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
		return response;
	}

	@Override
	public Response<SchoolRegionDto> updateSchool(SchoolDto schoolDto) {

		Result<SchoolRegionDto> res = new Result<>();

		res.setData(null);
		Optional<School> existingSchool = schoolRepository.findById(schoolDto.getSchoolId());
		if (!existingSchool.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_SCHOOL_FOUND.getCode(), HttpStatusCode.NO_SCHOOL_FOUND,
					HttpStatusCode.NO_SCHOOL_FOUND.getMessage(), res);
		}
		School school = existingSchool.get();

		SchoolDto existingSchools = schoolMapper.entityToDto(existingSchool.get());
		school.setCode(schoolDto.getCode());
		school.setContact(schoolDto.getContact());
		school.setAddress(schoolDto.getAddress());
		school.setIsCollege(schoolDto.getIsCollege());
		school.setExemptionFlag(schoolDto.isExemptionFlag());
		school.setName(schoolDto.getName());
		school.setStrength(schoolDto.getStrength());
		school.setVvnAccount(schoolDto.getVvnAccount());
		school.setVvnFund(schoolDto.getVvnFund());
		school.setStrength(schoolDto.getStrength());
		school.setShift(schoolDto.getShift());
		school.setType(schoolDto.getType());
		school.setEmail(schoolDto.getEmail());
		school.setSchoolId(schoolDto.getSchoolId());
		
		String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
		
		ResponseEntity<Response<UserDto>> principalResponse = userFeignService.getPrincipalById(currJwtToken,schoolDto.getPrincipalId().toString());
		PrincipalDto principalDto = null;
		UserDto userDto = principalResponse.getBody().getResult().getData();
		if(userDto != null) {
			principalDto = new PrincipalDto(userDto.getId(),userDto.getContactInfoDto().getFirstName(),userDto.getContactInfoDto().getLastName());
		}
		
		
		school.setPrincipalId(schoolDto.getPrincipalId());

		Region region = regionRepository.getReferenceById(schoolDto.getRegionId());
		regionRepository.save(region);
		school.setRegion(region);

		for (Long classId : schoolDto.getClassId()) {
			ClassDetail classDetail = classRepository.getReferenceById(classId);
			classDetail.setSchool(school);
			classRepository.save(classDetail);
			school.getClassDetail().add(classDetail);
		}

		EducationalInstitution educationalInstitution = educationalRepository
				.getReferenceById(schoolDto.getEducationalInstitutionId());
		educationalRepository.save(educationalInstitution);
		school.setEducationalInstitution(educationalInstitution);

		School updatedSchool = schoolRepository.save(school);

		SchoolRegionDto schoolRegionDto = schoolMapper.toSchoolClassDto(updatedSchool);
		schoolRegionDto.setPrincipalDto(principalDto);
		res.setData(schoolRegionDto);
		Response<SchoolRegionDto> response = new Response<>();
		response.setMessage(HttpStatusCode.SCHOOL_UPDATED.getMessage());
		response.setStatusCode(HttpStatusCode.SCHOOL_UPDATED.getCode());
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
		kafkaTemplate.send(topicUpdateName,3, "Key2",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
		return response;
	}

	@Override
	public Response<List<SchoolDto>> getSchoolwithSort(String field) {

		Result<List<SchoolDto>> allSchoolResult = new Result<>();
		Response<List<SchoolDto>> getListofSchools = new Response<>();

		List<School> list = this.schoolRepository.findAll(Sort.by(Sort.Direction.ASC, field));
		List<SchoolDto> schoolDtos = schoolMapper.entitiesToDtos(list);

		if (list.size() == 0) {
			throw new CustomException(HttpStatusCode.NO_SCHOOL_FOUND.getCode(), HttpStatusCode.NO_SCHOOL_FOUND,
					HttpStatusCode.NO_SCHOOL_FOUND.getMessage(), allSchoolResult);
		}
		allSchoolResult.setData(schoolDtos);
		getListofSchools.setStatusCode(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getCode());
		getListofSchools.setMessage(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getMessage());
		getListofSchools.setResult(allSchoolResult);
		return getListofSchools;
	}

}
