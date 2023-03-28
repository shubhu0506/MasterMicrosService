package com.ubi.masterservice.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.ubi.masterservice.dto.studentDto.StudentDetailsDto;
import com.ubi.masterservice.entity.*;
import com.ubi.masterservice.mapper.*;
import com.ubi.masterservice.repository.*;
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
import com.ubi.masterservice.dto.classDto.TeacherDto;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.schoolDto.PrincipalDto;
import com.ubi.masterservice.dto.schoolDto.SchoolCreationDto;
import com.ubi.masterservice.dto.schoolDto.SchoolDto;
import com.ubi.masterservice.dto.schoolDto.SchoolRegionDto;
import com.ubi.masterservice.dto.user.UserDto;
import com.ubi.masterservice.error.CustomException;
import com.ubi.masterservice.error.HttpStatusCode;
import com.ubi.masterservice.error.Result;
import com.ubi.masterservice.externalServices.UserFeignService;
import com.ubi.masterservice.util.PermissionUtil;

@Service
public class SchoolServiceImpl implements SchoolService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SchoolServiceImpl.class);

	@Autowired
	private SchoolMapper schoolMapper;

	@Autowired
	private PermissionUtil permissionUtil;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private StudentMapper studentMapper;

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

	@Autowired
	private ClassService classService;

	private String topicName = "master_topic_add";

	private String topicDelete = "master_delete";

	private String topicUpdateName = "master_topic_update";

	private NewTopic topic;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public SchoolServiceImpl(NewTopic topic, KafkaTemplate<String, String> kafkaTemplate) {
		this.topic = topic;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Override
	public Response<SchoolRegionDto> addSchool(SchoolCreationDto schoolDto) {

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

		if (schoolDto.getRegionId() == null || schoolDto.getRegionId() == 0) {
			throw new CustomException(HttpStatusCode.REGION_NOT_FOUND.getCode(),
					HttpStatusCode.REGION_NOT_FOUND, "Add Region for this school",
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
		school.setFeesCollectionType(schoolDto.getFeesCollectionType());
		school.setFeesCollectionPeriod(schoolDto.getFeesCollectionPeriod());

		PrincipalDto principalDto = null;
		if (school.getPrincipalId() != null) {
			School school1 = schoolRepository.findByPrincipalId(schoolDto.getPrincipalId());
			School school2 = schoolRepository.findCollegeByPrincipalId(schoolDto.getPrincipalId());
			if(school1 != null || school2 != null){
				throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
						HttpStatusCode.BAD_REQUEST_EXCEPTION,
						"Given Principal Id is already mapped with another School/college",
						res);
			}


			String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
			ResponseEntity<Response<UserDto>> principalResponse = userFeignService.getPrincipalById(currJwtToken,
					school.getPrincipalId().toString());
			UserDto userDto = principalResponse.getBody().getResult().getData();
			if (userDto != null) {
				principalDto = new PrincipalDto(userDto.getId(), userDto.getContactInfoDto().getFirstName(),
						userDto.getContactInfoDto().getLastName(),school.getSchoolId());
			}
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		kafkaTemplate.send(topicName, 3, "Key1", jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
		return response;
	}

	@Override
	public Response<PaginationResponse<List<SchoolRegionDto>>> getAllSchools(String fieldName, String searchByField,Integer PageNumber, Integer PageSize) {
		
		Result<PaginationResponse<List<SchoolRegionDto>>> allSchoolResult = new Result<>();
		Pageable paging = PageRequest.of(PageNumber, PageSize);
		Response<PaginationResponse<List<SchoolRegionDto>>> getListofSchools = new Response<PaginationResponse<List<SchoolRegionDto>>>();
        SchoolRegionDto schoolRegionDto = null;
        
		List<SchoolRegionDto> schoolRegionDtos;
		PaginationResponse paginationResponse;
		

		Page<School> schoolData = null;
		
//		if (!fieldName.equals("*") && !searchByField.equals("*")) {
			
			if (fieldName.equalsIgnoreCase("code")) {
				schoolData = schoolRepository.findByCode(Integer.parseInt(searchByField), paging);
			}

			else if (fieldName.equalsIgnoreCase("name")) {
				schoolData = schoolRepository.findByName(searchByField, paging);
			}

			else if (fieldName.equalsIgnoreCase("email")) {
				schoolData = schoolRepository.findByEmail(searchByField,paging);
			}

			else if (fieldName.equalsIgnoreCase("contact")) {
				schoolData = schoolRepository.findByContact(Long.parseLong(searchByField),paging);
			}

			else if (fieldName.equalsIgnoreCase("address")) {
				schoolData = schoolRepository.findByAddress(searchByField,paging);
			}

			else if (fieldName.equalsIgnoreCase("type")) {
				schoolData = schoolRepository.findByType(searchByField, paging);
			}

			else if (fieldName.equalsIgnoreCase("strength")) {
				schoolData = schoolRepository.findByStrength(Integer.parseInt(searchByField), paging);
			}

			else if (fieldName.equalsIgnoreCase("shift")) {
				schoolData = schoolRepository.findByShift((searchByField), paging);
			}

			else if (fieldName.equalsIgnoreCase("isCollege")) {
				schoolData = schoolRepository.findByIsCollege(Boolean.parseBoolean(searchByField), paging);
			}

			else if (fieldName.equalsIgnoreCase("exemptionFlag")) {
				schoolData = schoolRepository.findByExemptionFlag(Boolean.parseBoolean(searchByField), paging);
			}

			
			else if (fieldName.equalsIgnoreCase("vvnAccount")) {
				schoolData = schoolRepository.findByVvnAccount(Integer.parseInt(searchByField), paging);
			}
			
			
			else if (fieldName.equalsIgnoreCase("vvnFund")) {
				schoolData = schoolRepository.findByVvnFund(Integer.parseInt(searchByField), paging);
			}

			else if (fieldName.equalsIgnoreCase("feesCollectionType")) {
				schoolData = schoolRepository.findByFeesCollectionType(searchByField, paging);
			}
			
			else if (fieldName.equalsIgnoreCase("feesCollectionPeriod")) {
				schoolData = schoolRepository.findByFeesCollectionPeriod(Integer.parseInt(searchByField), paging);
			}
			
			else if (fieldName.equalsIgnoreCase("principalId")) {
				schoolData = schoolRepository.findByPrincipalId(Long.parseLong(searchByField), paging);
			}
			
			else if (fieldName.equalsIgnoreCase("id")) {
				schoolData = schoolRepository.findAllBySchoolId(Integer.parseInt(searchByField), paging);
			}
			 else {
				 
				 schoolData = this.schoolRepository.findByisCollege(false, paging);					
				}
			
		    //Page<School> listOfSchool = (Page<School>) schoolData.stream().filter(m-> m.equals(true));
			//Page<School> schoolDataList = (Page<School>) schoolData.filter(m -> m.getIsCollege(false));
			
			
			
			schoolRegionDtos = schoolData.toList().stream().map(school -> schoolMapper.toSchoolClassDto(school)).collect(Collectors.toList());		
			paginationResponse = new PaginationResponse<List<SchoolRegionDto>>(schoolRegionDtos, schoolData.getTotalPages(), schoolData.getTotalElements());
		
		// List<SchoolRegionDto> schoolDto = new ArrayList<>();
		for (School school : schoolData) {
			SchoolRegionDto schoolRegionDtoss = new SchoolRegionDto();
			schoolRegionDtoss.setSchoolDto(schoolMapper.entityToDtos(school));

			String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();

			PrincipalDto principalDto = null;

			if (school.getPrincipalId() != null) {
				ResponseEntity<Response<UserDto>> principalResponse = userFeignService.getPrincipalById(currJwtToken,
						school.getPrincipalId().toString());
				UserDto userDto = principalResponse.getBody().getResult().getData();
				if (userDto != null) {
					principalDto = new PrincipalDto(userDto.getId(), userDto.getContactInfoDto().getFirstName(),
							userDto.getContactInfoDto().getLastName(),school.getSchoolId());
				}
			}

			schoolRegionDtoss.setRegionDto(regionMapper.toDto(school.getRegion()));
			schoolRegionDtoss.setPrincipalDto(principalDto);
			Set<ClassDto> classDto = school.getClassDetail().stream()
					.map(classDetail -> classMapper.entityToDto(classDetail)).collect(Collectors.toSet());

			schoolRegionDtoss.setClassDto(classDto);
//			schoolDto.add(schoolRegionDto);

			schoolRegionDtoss.setEducationalInstitutionDto(educationalMapper.toDto(school.getEducationalInstitution()));
		}

		if (schoolData.isEmpty()) {
			throw new CustomException(HttpStatusCode.NO_SCHOOL_FOUND.getCode(), HttpStatusCode.NO_SCHOOL_FOUND,
					HttpStatusCode.NO_SCHOOL_FOUND.getMessage(), allSchoolResult);
		}

//		PaginationResponse paginationResponse = new PaginationResponse<List<SchoolRegionDto>>(schoolDtos,
//				list.getTotalPages(), list.getTotalElements());

		allSchoolResult.setData(paginationResponse);
		getListofSchools.setStatusCode(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getCode());
		getListofSchools.setMessage(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getMessage());
		getListofSchools.setResult(allSchoolResult);
		return getListofSchools;
	}

	@Override
	public Response<PaginationResponse<List<SchoolRegionDto>>> getAllColleges(String fieldName, String searchByField,Integer PageNumber, Integer PageSize) {
		
		Result<PaginationResponse<List<SchoolRegionDto>>> allSchoolResult = new Result<>();
		Pageable paging = PageRequest.of(PageNumber, PageSize);
		Response<PaginationResponse<List<SchoolRegionDto>>> getListofSchools = new Response<PaginationResponse<List<SchoolRegionDto>>>();
        SchoolRegionDto schoolRegionDto = null;
        
		List<SchoolRegionDto> schoolRegionDtos;
		PaginationResponse paginationResponse;
		

		Page<School> schoolData = null;
		
//		if (!fieldName.equals("*") && !searchByField.equals("*")) {
			
			if (fieldName.equalsIgnoreCase("code")) {
				schoolData = schoolRepository.findByCode(Integer.parseInt(searchByField), paging);
			}

			else if (fieldName.equalsIgnoreCase("name")) {
				schoolData = schoolRepository.findByName(searchByField, paging);
			}

			else if (fieldName.equalsIgnoreCase("email")) {
				schoolData = schoolRepository.findByEmail(searchByField,paging);
			}

			else if (fieldName.equalsIgnoreCase("contact")) {
				schoolData = schoolRepository.findByContact(Long.parseLong(searchByField),paging);
			}

			else if (fieldName.equalsIgnoreCase("address")) {
				schoolData = schoolRepository.findByAddress(searchByField,paging);
			}

			else if (fieldName.equalsIgnoreCase("type")) {
				schoolData = schoolRepository.findByType(searchByField, paging);
			}

			else if (fieldName.equalsIgnoreCase("strength")) {
				schoolData = schoolRepository.findByStrength(Integer.parseInt(searchByField), paging);
			}

			else if (fieldName.equalsIgnoreCase("shift")) {
				schoolData = schoolRepository.findByShift((searchByField), paging);
			}

			else if (fieldName.equalsIgnoreCase("isCollege")) {
				schoolData = schoolRepository.findByIsCollege(Boolean.parseBoolean(searchByField), paging);
			}

			else if (fieldName.equalsIgnoreCase("exemptionFlag")) {
				schoolData = schoolRepository.findByExemptionFlag(Boolean.parseBoolean(searchByField), paging);
			}
			
			else if (fieldName.equalsIgnoreCase("vvnAccount")) {
				schoolData = schoolRepository.findByVvnAccount(Integer.parseInt(searchByField), paging);
			}
			
			
			else if (fieldName.equalsIgnoreCase("vvnFund")) {
				schoolData = schoolRepository.findByVvnFund(Integer.parseInt(searchByField), paging);
			}

			else if (fieldName.equalsIgnoreCase("feesCollectionType")) {
				schoolData = schoolRepository.findByFeesCollectionType(searchByField, paging);
			}
			else if (fieldName.equalsIgnoreCase("feesCollectionPeriod")) {
				schoolData = schoolRepository.findByFeesCollectionPeriod(Integer.parseInt(searchByField), paging);
			}
			
			else if (fieldName.equalsIgnoreCase("principalId")) {
				schoolData = schoolRepository.findByPrincipalId(Long.parseLong(searchByField), paging);
			}
			
			else if (fieldName.equalsIgnoreCase("id")) {
				schoolData = schoolRepository.findAllBySchoolId(Integer.parseInt(searchByField), paging);
			}
			 else {
				 
				 schoolData = this.schoolRepository.findByisCollege(true, paging);					
				}
			
			schoolRegionDtos = schoolData.toList().stream().map(school -> schoolMapper.toSchoolClassDto(school)).collect(Collectors.toList());
			paginationResponse = new PaginationResponse<List<SchoolRegionDto>>(schoolRegionDtos, schoolData.getTotalPages(), schoolData.getTotalElements());
		
		// List<SchoolRegionDto> schoolDto = new ArrayList<>();
		for (School school : schoolData) {
			SchoolRegionDto schoolRegionDtoss = new SchoolRegionDto();
			schoolRegionDtoss.setSchoolDto(schoolMapper.entityToDtos(school));

			String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();

			PrincipalDto principalDto = null;

			if (school.getPrincipalId() != null) {
				ResponseEntity<Response<UserDto>> principalResponse = userFeignService.getPrincipalById(currJwtToken,
						school.getPrincipalId().toString());
				UserDto userDto = principalResponse.getBody().getResult().getData();
				if (userDto != null) {
					principalDto = new PrincipalDto(userDto.getId(), userDto.getContactInfoDto().getFirstName(),
							userDto.getContactInfoDto().getLastName(),school.getSchoolId());
				}
			}

			schoolRegionDtoss.setRegionDto(regionMapper.toDto(school.getRegion()));
			schoolRegionDtoss.setPrincipalDto(principalDto);
			Set<ClassDto> classDto = school.getClassDetail().stream()
					.map(classDetail -> classMapper.entityToDto(classDetail)).collect(Collectors.toSet());

			schoolRegionDtoss.setClassDto(classDto);
//			schoolDto.add(schoolRegionDto);

			schoolRegionDtoss.setEducationalInstitutionDto(educationalMapper.toDto(school.getEducationalInstitution()));
		}

		if (schoolData.isEmpty()) {
			throw new CustomException(HttpStatusCode.NO_COLLEGES_FOUND.getCode(), HttpStatusCode.NO_COLLEGES_FOUND,
					HttpStatusCode.NO_COLLEGES_FOUND.getMessage(), allSchoolResult);
		}

//		PaginationResponse paginationResponse = new PaginationResponse<List<SchoolRegionDto>>(schoolDtos,
//				list.getTotalPages(), list.getTotalElements());

		allSchoolResult.setData(paginationResponse);
		getListofSchools.setStatusCode(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getCode());
		getListofSchools.setMessage(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getMessage());
		getListofSchools.setResult(allSchoolResult);
		return getListofSchools;
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
		if(sch.get().getIsDeleted()){
			throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_DELETED.getCode(), HttpStatusCode.RESOURCE_ALREADY_DELETED,
					"Given School/College is deleted", new Result<>(null));
		}

		SchoolRegionDto schoolRegionDto = new SchoolRegionDto();
		schoolRegionDto.setSchoolDto(schoolMapper.entityToDto(sch.get()));

		String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();

		PrincipalDto principalDto = null;

		if (sch.get().getPrincipalId() != null) {
			ResponseEntity<Response<UserDto>> principalResponse = userFeignService.getPrincipalById(currJwtToken,
					sch.get().getPrincipalId().toString());
			UserDto userDto = principalResponse.getBody().getResult().getData();
			if (userDto != null) {
				principalDto = new PrincipalDto(userDto.getId(), userDto.getContactInfoDto().getFirstName(),
						userDto.getContactInfoDto().getLastName(),sch.get().getSchoolId());
			}
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
	public Response<SchoolDto> deleteSchoolById(int schoolId) {

		Result<SchoolDto> res = new Result<>();
		res.setData(null);
		Optional<School> school = schoolRepository.findById(schoolId);
		if (!school.isPresent()) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
		}

		School school1 = school.get();
		if(school1.getIsDeleted()){
			throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_DELETED.getCode(), HttpStatusCode.RESOURCE_ALREADY_DELETED,
					"Given School/College is already deleted", new Result<>(null));
		}

		if(school1.getClassDetail() != null && school1.getClassDetail().size() > 0){
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), HttpStatusCode.BAD_REQUEST_EXCEPTION,
					"School is mapped with class, hence it can not be deleted", res);
		}

		school1.setRegion(null);
		school1.setEducationalInstitution(null);
		school1.setPrincipalId(null);
		school1.setIsDeleted(true);
		school1 = schoolRepository.save(school1);

		Response<SchoolDto> response = new Response<>();
		res.setData(schoolMapper.entityToDto(school1));
		response.setMessage("School Deleted Successfully");
		response.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
		response.setResult(res);
		ObjectMapper obj = new ObjectMapper();

		String jsonStr = null;
		try {
			jsonStr = obj.writeValueAsString(res.getData());
			LOGGER.info(jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		kafkaTemplate.send(topicDelete, 3, "Key3", jsonStr);
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

	@Override
	public Response<SchoolRegionDto> getSchoolByPrincipalId(Long principalId) {
		School school = schoolRepository.findByPrincipalId(principalId);
		School school1 = schoolRepository.findCollegeByPrincipalId(principalId);
		Response<SchoolRegionDto> response = new Response<>();
		if(school == null && school1 == null){
			response.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
			response.setMessage("No School/college Found With Given Principal Id");
			response.setResult(new Result<>(null));
			return response;
		}
		SchoolRegionDto schoolRegionDto = new SchoolRegionDto();
		if(school != null) {
			schoolRegionDto = schoolMapper.toSchoolClassDto(school);
		}
		else schoolRegionDto = schoolMapper.toSchoolClassDto(school1);

		response.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
		response.setMessage(HttpStatusCode.SUCCESSFUL.getMessage());
		response.setResult(new Result<>(schoolRegionDto));
		return response;
	}

	@Override
	public Response<SchoolRegionDto> getCollegeByPrincipalId(Long principalId) {
		School school = schoolRepository.findCollegeByPrincipalId(principalId);
		Response<SchoolRegionDto> response = new Response<>();
		if(school == null){
			response.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
			response.setMessage("No College Found With Given Principal Id");
			response.setResult(new Result<>(null));
			return response;
		}
		SchoolRegionDto schoolRegionDto = schoolMapper.toSchoolClassDto(school);
		response.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
		response.setMessage(HttpStatusCode.SUCCESSFUL.getMessage());
		response.setResult(new Result<>(schoolRegionDto));
		return response;
	}

	@Override
	public Response<Set<TeacherDto>> getAllTeacherBySchoolId(int schoolId) {
		Optional<School> schoolOptional = schoolRepository.findById(schoolId);
		if(!schoolOptional.isPresent()) {
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION,
					"School Not Exists With Given School Id", new Result<>(null));
		}
		if(schoolOptional.get().getIsDeleted()){
			throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_DELETED.getCode(), HttpStatusCode.RESOURCE_ALREADY_DELETED,
					"Given School/College is deleted", new Result<>(null));
		}

		School school = schoolOptional.get();
		Set<TeacherDto> teachers = new HashSet<>();
		Set<ClassDetail> classes = new HashSet<>();
		if(!school.getClassDetail().isEmpty()) classes = school.getClassDetail();
		for(ClassDetail classDetail:classes){
			if(classDetail.getTeacherId() != null) {
				String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
				ResponseEntity<Response<UserDto>> teacherResponse = userFeignService.getTeacherById(currJwtToken,
						classDetail.getTeacherId().toString());
				UserDto userDto = teacherResponse.getBody().getResult().getData();
				if (userDto != null) {
					TeacherDto teacherDto = new TeacherDto(userDto.getId(), userDto.getContactInfoDto().getFirstName(),
							userDto.getContactInfoDto().getLastName(),classDetail.getClassId(),classDetail.getSchool().getSchoolId());
					teachers.add(teacherDto);
				}
			}
		}

		Response<Set<TeacherDto>> response = new Response<>(new Result<>(teachers));
		return response;
	}

	public Response<PaginationResponse<List<StudentDetailsDto>>> getStudentsBySchoolId(Integer schoolId, String fieldName, String searchByField, Integer PageNumber, Integer PageSize) throws ParseException {

		Optional<School> schoolOptional = schoolRepository.findById(schoolId);
		if(!schoolOptional.isPresent()) {
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION,
					"School Not Exists With Given School Id", new Result<>(null));
		}
		if(schoolOptional.get().getIsDeleted()){
			throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_DELETED.getCode(), HttpStatusCode.RESOURCE_ALREADY_DELETED,
					"Given School/College is deleted", new Result<>(null));
		}

		Pageable paging = PageRequest.of(PageNumber, PageSize);
		Page<Student> students = null;

		String strDateRegEx ="^((?:19|20)[0-9][0-9])-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$";
		if(!fieldName.equals("*") && !searchByField.equals("*"))
		{
			if(searchByField.matches(strDateRegEx)) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
				Date localDate = formatter.parse(searchByField);
				if(fieldName.equalsIgnoreCase("dateOfBirth")) students = studentRepository.findStudentsByDOBAndSchoolId(localDate,schoolId,paging);
				else students = studentRepository.findStudentsByDOJAndSchoolId(localDate,schoolId,paging);
			} else {
				if(fieldName.equalsIgnoreCase("studentFirstName")) {
					students = studentRepository.findStudentsByFirstNameAndSchoolId(searchByField,schoolId, paging);
				}
				if(fieldName.equalsIgnoreCase("studentLastName")) {
					students = studentRepository.findStudentsByLastNameAndSchoolId(searchByField,schoolId, paging);
				}
				if(fieldName.equalsIgnoreCase("fullName")) {
					students = studentRepository.findStudentsByFullNameAndSchoolId(searchByField,schoolId, paging);
				}
				if(fieldName.equalsIgnoreCase("category")) {
					students = studentRepository.findStudentsByCategoryAndSchoolId(searchByField,schoolId, paging);
				}
				if(fieldName.equalsIgnoreCase("minority")) {
					students = studentRepository.findStudentsByMinorityAndSchoolId(searchByField,schoolId, paging);
				}
				if(fieldName.equalsIgnoreCase("fatherName")) {
					students = studentRepository.findStudentsByFatherNameAndSchoolId(searchByField,schoolId, paging);
				}
				if(fieldName.equalsIgnoreCase("motherName")) {
					students = studentRepository.findStudentsByMotherNameAndSchoolId(searchByField,schoolId, paging);
				}
				if(fieldName.equalsIgnoreCase("gender")) {
					students = studentRepository.findStudentsByGenderAndSchoolId(searchByField,schoolId, paging);
				}
				if(fieldName.equalsIgnoreCase("verifiedByTeacher")) {
					students = studentRepository.findStudentsByVerifiedByTeacherAndSchoolId(Boolean.parseBoolean(searchByField),schoolId,paging);
				}
				if(fieldName.equalsIgnoreCase("currentStatus")) {
					students = studentRepository.findStudentsByCurrentStatusAndSchoolId(searchByField,schoolId,paging);
				}
				if(fieldName.equalsIgnoreCase("verifiedByPrincipal")) {
					students = studentRepository.findStudentsByVerifiedByPrincipalAndSchoolId(Boolean.parseBoolean(searchByField),schoolId,paging);
				}
				if(fieldName.equalsIgnoreCase("isActivate")) {
					students = studentRepository.findStudentsByIsActivateAndSchoolId(Boolean.parseBoolean(searchByField),schoolId,paging);
				}
			}
		} else students = studentRepository.findStudentsBySchoolId(schoolId,paging);

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
		response.setMessage("Students retrived");
		response.setResult(result);

		return response;
	}

	@Override
	public Response<SchoolRegionDto> updateSchool(SchoolCreationDto schoolCreationDto, int schoolId) {
		
			Result<SchoolRegionDto> res = new Result<>();

			res.setData(null);
			Optional<School> existingSchool = schoolRepository.findById(schoolId);
			if (!existingSchool.isPresent()) {  
				throw new CustomException(HttpStatusCode.NO_SCHOOL_FOUND.getCode(), HttpStatusCode.NO_SCHOOL_FOUND,
						HttpStatusCode.NO_SCHOOL_FOUND.getMessage(), res);
			}
			if(existingSchool.get().getIsDeleted()){
				throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_DELETED.getCode(), HttpStatusCode.RESOURCE_ALREADY_DELETED,
						"Given School/College is deleted", res);
			}
			School school = existingSchool.get();

			SchoolDto existingSchools = schoolMapper.entityToDto(existingSchool.get());
			school.setCode(schoolCreationDto.getCode());
			school.setName(schoolCreationDto.getName());
			school.setEmail(schoolCreationDto.getEmail());
			school.setContact(schoolCreationDto.getContact());
			school.setAddress(schoolCreationDto.getAddress());
			school.setType(schoolCreationDto.getType());
			school.setStrength(schoolCreationDto.getStrength());
			school.setShift(schoolCreationDto.getShift());
			school.setIsCollege(schoolCreationDto.getIsCollege());
			school.setExemptionFlag(schoolCreationDto.isExemptionFlag());
			school.setVvnAccount(schoolCreationDto.getVvnAccount());
			school.setVvnFund(schoolCreationDto.getVvnFund());
			school.setFeesCollectionType(schoolCreationDto.getFeesCollectionType());
			school.setFeesCollectionPeriod(schoolCreationDto.getFeesCollectionPeriod());
			school.setPrincipalId(schoolCreationDto.getPrincipalId());

			
			PrincipalDto principalDto = null;
			if (schoolCreationDto.getPrincipalId() != null && existingSchool.get().getPrincipalId() != schoolCreationDto.getPrincipalId()) {
				School school1 = schoolRepository.findByPrincipalId(schoolCreationDto.getPrincipalId());
				School school2 = schoolRepository.findCollegeByPrincipalId(schoolCreationDto.getPrincipalId());
				if(school1 != null || school2 != null){
					throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
							HttpStatusCode.BAD_REQUEST_EXCEPTION,
							"Given Principal Id is already mapped with another School/college",
							res);
				}


				String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
				ResponseEntity<Response<UserDto>> principalResponse = userFeignService.getPrincipalById(currJwtToken,
						schoolCreationDto.getPrincipalId().toString());
				UserDto userDto = principalResponse.getBody().getResult().getData();
				if (userDto != null) {
					principalDto = new PrincipalDto(userDto.getId(), userDto.getContactInfoDto().getFirstName(),
							userDto.getContactInfoDto().getLastName(),school.getSchoolId());
				}
			}

			school.setPrincipalId(schoolCreationDto.getPrincipalId());

			Region region = regionRepository.getReferenceById(schoolCreationDto.getRegionId());
			regionRepository.save(region);
			school.setRegion(region);

			for (Long classId : schoolCreationDto.getClassId()) {
				ClassDetail classDetail = classRepository.getReferenceById(classId);
				classDetail.setSchool(school);
				classRepository.save(classDetail);
				school.getClassDetail().add(classDetail);
			}

			EducationalInstitution educationalInstitution = educationalRepository
					.getReferenceById(schoolCreationDto.getEducationalInstitutionId());
			educationalRepository.save(educationalInstitution);
			school.setEducationalInstitution(educationalInstitution);

			School updatedSchool = schoolRepository.save(school);

			SchoolRegionDto schoolRegionDto = schoolMapper.toSchoolClassDto(updatedSchool);
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
			} catch (IOException e) {
				e.printStackTrace();
			}
			kafkaTemplate.send(topicUpdateName, 3, "Key2", jsonStr);
			LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
			return response;
		
	}

	@Override
	public Response<SchoolRegionDto> removeSchoolPrincipal(String schoolId) {
		Result<SchoolRegionDto> res = new Result<>();

		res.setData(null);
		Optional<School> existingSchool = schoolRepository.findById(Integer.parseInt(schoolId));
		if (!existingSchool.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_SCHOOL_FOUND.getCode(), HttpStatusCode.NO_SCHOOL_FOUND,
					HttpStatusCode.NO_SCHOOL_FOUND.getMessage(), res);
		}
		if(existingSchool.get().getIsDeleted()){
			throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_DELETED.getCode(), HttpStatusCode.RESOURCE_ALREADY_DELETED,
					"Given School/College is deleted", res);
		}
		School school = existingSchool.get();
		school.setPrincipalId(null);
		School updatedSchool = schoolRepository.save(school);

		SchoolRegionDto schoolRegionDto = schoolMapper.toSchoolClassDto(updatedSchool);
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		kafkaTemplate.send(topicUpdateName, 3, "Key2", jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
		return response;
	}


}
