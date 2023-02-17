package com.ubi.masterservice.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.ubi.masterservice.dto.regionDto.RegionAdminDto;
import com.ubi.masterservice.dto.schoolDto.SchoolRegionDto;
import com.ubi.masterservice.dto.studentDto.StudentDetailsDto;
import com.ubi.masterservice.dto.user.UserDto;
import com.ubi.masterservice.entity.Student;
import com.ubi.masterservice.externalServices.UserFeignService;
import com.ubi.masterservice.mapper.StudentMapper;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.regionDto.RegionCreationDto;
import com.ubi.masterservice.dto.regionDto.RegionDetailsDto;
import com.ubi.masterservice.dto.regionDto.RegionDto;
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
	public StudentRepository studentRepository;

	@Autowired
	StudentMapper studentMapper;

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

	@Autowired
	private PermissionUtil permissionUtil;

	@Autowired
	private UserFeignService userFeignService;

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

		//savedRegion = regionRepository.save(savedRegion);

		for(Integer eduInstiId : regionCreationDto.getEduInstId()) {
			EducationalInstitution eduInsti = educationalInstitutionRepository.findByIdIfNotDeleted(eduInstiId);
			if (eduInsti != null){
				savedRegion.getEducationalInstitiute().add(eduInsti);
				System.out.println("mapped institute Id is --- " + eduInsti.getId());
			}
			else{
				throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTE_ADDED.getCode(),
						HttpStatusCode.NO_EDUCATIONAL_INSTITUTE_ADDED,
						"Invalid institute is being sent to map with region", res);
			}
		}
		RegionAdminDto regionAdminDto = null;
		if(regionCreationDto.getAdminId() != null){
			Region region = regionRepository.findByAdminId(regionCreationDto.getAdminId());
			if(region != null){
				throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
						HttpStatusCode.BAD_REQUEST_EXCEPTION,
						"Given region admin id is already mapped with another Region",
						res);
			}
			String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
			ResponseEntity<Response<UserDto>> regionAdminResponse = userFeignService.getRegionAdminById(currJwtToken,regionCreationDto.getAdminId().toString());
			System.out.println(regionAdminResponse.getBody().getResult().getData().toString());
			UserDto userDto = regionAdminResponse.getBody().getResult().getData();
			System.out.println(userDto.toString());
			if(userDto != null) {
				regionAdminDto = new RegionAdminDto(userDto.getId(),userDto.getContactInfoDto().getFirstName(),userDto.getContactInfoDto().getLastName());
				savedRegion.setAdminId(regionCreationDto.getAdminId());
			}
		}

		Region saveRegion = regionRepository.save(savedRegion);
		RegionDetailsDto regionDetailsDto = regionMapper.toRegionDetails(saveRegion);


		res.setData(regionDetailsDto);
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

	/*@Override
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
	}*/
	public Response<PaginationResponse<List<RegionDetailsDto>>> getRegionDetails(String fieldName,String searchByField,Integer PageNumber, Integer PageSize) {
		Result<PaginationResponse<List<RegionDetailsDto>>> res = new Result<>();
		Pageable paging = PageRequest.of(PageNumber, PageSize);

		Response<PaginationResponse<List<RegionDetailsDto>>> getListofRegion = new Response<PaginationResponse<List<RegionDetailsDto>>>();
		Page<Region> list = this.regionRepository.getAllAvailaibleRegion(paging);
		List<RegionDetailsDto> regionDtos;
		PaginationResponse<List<RegionDetailsDto>> paginationResponse = null;
		Page<Region> regionData = this.regionRepository.findAll(paging);
		
		if(!fieldName.equals("*") && !searchByField.equals("*")) {
				if(fieldName.equalsIgnoreCase("code")) {
					regionData = regionRepository.findByCode(searchByField,paging);
				}
				if(fieldName.equalsIgnoreCase("name")) {
					regionData = regionRepository.findByName(searchByField,paging);
				}
				
				if(fieldName.equalsIgnoreCase("id")) {
					regionData = regionRepository.findAllById((Integer.parseInt(searchByField)),paging);
				}
				regionDtos = (regionData.stream().map(region -> regionMapper.toRegionDetails(region)).collect(Collectors.toList()));
				paginationResponse=new PaginationResponse<List<RegionDetailsDto>>(regionDtos,regionData.getTotalPages(),regionData.getTotalElements());
		} else {
			regionDtos = (list.toList().stream().map(region -> regionMapper.toRegionDetails(region)).collect(Collectors.toList()));
			paginationResponse=new PaginationResponse<List<RegionDetailsDto>>(regionDtos,list.getTotalPages(),list.getTotalElements());
		}
		if (list.isEmpty()) {
			throw new CustomException(HttpStatusCode.NO_ENTRY_FOUND.getCode(), HttpStatusCode.NO_ENTRY_FOUND,
					HttpStatusCode.NO_ENTRY_FOUND.getMessage(), res);
		}
		res.setData(paginationResponse);
		getListofRegion.setStatusCode(200);
		getListofRegion.setResult(res);
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
		if (region.get().getIsDeleted() == true) {
			throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_DELETED.getCode(), HttpStatusCode.RESOURCE_ALREADY_DELETED,
					"Region with given Id is deleted", regionResult);
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

		if (region.getIsDeleted() == true) {
			throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_DELETED.getCode(), HttpStatusCode.RESOURCE_ALREADY_DELETED,
					"Region with given Id is already deleted", res);
		}
		Region region1 = new Region();
		region1 = region;

		if(region.getIsDeleted() == true){
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
		}

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
		region.setAdminId(null);
		region.setIsDeleted(true);
		regionRepository.save(region);

		Response<RegionDto> response = new Response<>();
		res.setData(regionMapper.entityToDto(region1));
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
	public Response<RegionDetailsDto> updateRegionDetails(RegionCreationDto regionCreationDto,Long regionId) {
		Result<RegionDetailsDto> res = new Result<>();

		res.setData(null);
		Optional<Region> existingRegionContainer = regionRepository.findById(Integer.parseInt(regionId.toString()));
		if (!existingRegionContainer.isPresent()) {
			throw new CustomException(HttpStatusCode.REGION_NOT_FOUND.getCode(), HttpStatusCode.REGION_NOT_FOUND,
					HttpStatusCode.REGION_NOT_FOUND.getMessage(), res);
		}
		Region region = existingRegionContainer.get();

		if(!region.getCode().equals(regionCreationDto.getCode())){
			System.out.println(region.getCode() + " --- " + regionCreationDto.getCode());
			Region regionWithSameCode = regionRepository.getRegionByCode(regionCreationDto.getCode());
			if(regionWithSameCode != null) {
				throw new CustomException(HttpStatusCode.REGION_CODE_DUPLICATE.getCode(), HttpStatusCode.REGION_CODE_DUPLICATE,
						HttpStatusCode.REGION_CODE_DUPLICATE.getMessage(), res);
			}
		}

		if(!region.getName().equals(regionCreationDto.getName())){
			Region regionWithSameName = regionRepository.getRegionByName(regionCreationDto.getName());
			if(regionWithSameName != null) {
				throw new CustomException(HttpStatusCode.REGION_NAME_DUPLICATE.getCode(), HttpStatusCode.REGION_NAME_DUPLICATE,
						HttpStatusCode.REGION_NAME_DUPLICATE.getMessage(), res);
			}
		}

		if(regionCreationDto.getAdminId() != null && regionCreationDto.getAdminId() != region.getAdminId()){
			Region region1 = regionRepository.findByAdminId(regionCreationDto.getAdminId());
			if(region1 != null){
				throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
						HttpStatusCode.BAD_REQUEST_EXCEPTION,
						"Given region admin id is already mapped with another Region",
						res);
			}

			String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
			ResponseEntity<Response<UserDto>> regionAdminResponse = userFeignService.getRegionAdminById(currJwtToken,regionCreationDto.getAdminId().toString());
		}

		region.setAdminId(regionCreationDto.getAdminId());
		region.setCode(regionCreationDto.getCode());
		region.setName(regionCreationDto.getName());

		Set<EducationalInstitution> educationalInstitutionSet = region.getEducationalInstitiute();
		for(EducationalInstitution educationalInstitution:educationalInstitutionSet){
			educationalInstitution.getRegion().remove(region);
			region.getEducationalInstitiute().remove(educationalInstitution);
			educationalInstitutionRepository.save(educationalInstitution);
		}
		Region updateRegion = regionRepository.save(region);

		for(Integer educationId:regionCreationDto.getEduInstId()){
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

	@Override
	public Response<RegionDto> getRegionByName(String name) {
		Response<RegionDto> getRegion = new Response<RegionDto>();
		Region region = regionRepository.getRegionByName(name);
		Result<RegionDto> regionResult = new Result<>();
		if (region == null) {
			throw new CustomException(HttpStatusCode.REGION_NOT_FOUND.getCode(), HttpStatusCode.REGION_NOT_FOUND,
					HttpStatusCode.REGION_NOT_FOUND.getMessage(), regionResult);
		}
		if (region.getIsDeleted()) {
			throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_DELETED.getCode(), HttpStatusCode.RESOURCE_ALREADY_DELETED,
					"Region with given name is deleted", regionResult);
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

	@Override
	public Response<RegionDetailsDto> getRegionByAdminId(Long adminId) {
		Region region = regionRepository.findByAdminId(adminId);
		Response<RegionDetailsDto> response = new Response<>();
		if(region == null){
			response.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
			response.setMessage("No Region Found With Given Admin Id");
			response.setResult(new Result<>(null));
			return response;
		}
		RegionDetailsDto regionDetailsDto = regionMapper.toRegionDetails(region);

		response.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
		response.setMessage("Region Retrived Successfully");
		response.setResult(new Result<>(regionDetailsDto));
		return response;
	}

	@Override
	public Response<Set<SchoolRegionDto>> getSchoolsByRegionId(Long regionId) {
		Set<School> schools = schoolRepository.findSchoolByRegionId(regionId);
		Response<Set<SchoolRegionDto>> response = new Response<>();
		if(schools.isEmpty()){
			response.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
			response.setMessage("No Schools Found With Given Region Id");
			response.setResult(new Result<>(null));
			return response;
		}
		Set<SchoolRegionDto> schoolDetails = schools.stream()
				.filter(Objects::nonNull).map(school -> schoolMapper.toSchoolClassDto(school)).collect(Collectors.toSet());

		response.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
		response.setMessage("Schools Retrived Successfully");
		response.setResult(new Result<>(schoolDetails));
		return response;
	}

	@Override
	public Response<Set<SchoolRegionDto>> getCollegeByRegionId(Long regionId) {
		Set<School> schools = schoolRepository.findCollegeByRegionId(regionId);
		Response<Set<SchoolRegionDto>> response = new Response<>();
		if(schools.isEmpty()){
			response.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
			response.setMessage("No College Found With Given Region Id");
			response.setResult(new Result<>(null));
			return response;
		}
		Set<SchoolRegionDto> schoolDetails = schools.stream()
				.filter(Objects::nonNull).map(school -> schoolMapper.toSchoolClassDto(school)).collect(Collectors.toSet());

		response.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
		response.setMessage("Colleges Retrived Successfully");
		response.setResult(new Result<>(schoolDetails));
		return response;
	}

	public Response<PaginationResponse<List<StudentDetailsDto>>> getStudentsByRegionId(Integer regionId, String fieldName, String searchByField, Integer PageNumber, Integer PageSize) throws ParseException {

		Optional<Region> existingRegionContainer = regionRepository.findById(regionId);
		if (!existingRegionContainer.isPresent()) {
			throw new CustomException(HttpStatusCode.REGION_NOT_FOUND.getCode(), HttpStatusCode.REGION_NOT_FOUND,
					HttpStatusCode.REGION_NOT_FOUND.getMessage(), new Result<>(null));
		}

		Pageable paging = PageRequest.of(PageNumber, PageSize);
		Page<Student> students = null;

		String strDateRegEx ="^((?:19|20)[0-9][0-9])-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$";
		if(!fieldName.equals("*") && !searchByField.equals("*"))
		{
			if(searchByField.matches(strDateRegEx)) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
				Date localDate = formatter.parse(searchByField);
				if(fieldName.equalsIgnoreCase("dateOfBirth")) students = studentRepository.findStudentsByDOBAndRegionId(localDate,regionId,paging);
				else students = studentRepository.findStudentsByDOJAndRegionId(localDate,regionId,paging);
			} else {
				if(fieldName.equalsIgnoreCase("studentFirstName")) {
					students = studentRepository.findStudentsByFirstNameAndRegionId(searchByField,regionId, paging);
				}
				if(fieldName.equalsIgnoreCase("studentLastName")) {
					students = studentRepository.findStudentsByLastNameAndRegionId(searchByField,regionId, paging);
				}
				if(fieldName.equalsIgnoreCase("fullName")) {
					students = studentRepository.findStudentsByFullNameAndRegionId(searchByField,regionId, paging);
				}
				if(fieldName.equalsIgnoreCase("category")) {
					students = studentRepository.findStudentsByCategoryAndRegionId(searchByField,regionId, paging);
				}
				if(fieldName.equalsIgnoreCase("minority")) {
					students = studentRepository.findStudentsByMinorityAndRegionId(searchByField,regionId, paging);
				}
				if(fieldName.equalsIgnoreCase("fatherName")) {
					students = studentRepository.findStudentsByFatherNameAndRegionId(searchByField,regionId, paging);
				}
				if(fieldName.equalsIgnoreCase("motherName")) {
					students = studentRepository.findStudentsByMotherNameAndRegionId(searchByField,regionId, paging);
				}
				if(fieldName.equalsIgnoreCase("gender")) {
					students = studentRepository.findStudentsByGenderAndRegionId(searchByField,regionId, paging);
				}
				if(fieldName.equalsIgnoreCase("verifiedByTeacher")) {
					students = studentRepository.findStudentsByVerifiedByTeacherAndRegionId(Boolean.parseBoolean(searchByField),regionId,paging);
				}
				if(fieldName.equalsIgnoreCase("currentStatus")) {
					students = studentRepository.findStudentsByCurrentStatusAndRegionId(searchByField,regionId,paging);
				}
				if(fieldName.equalsIgnoreCase("verifiedByPrincipal")) {
					students = studentRepository.findStudentsByVerifiedByPrincipalAndRegionId(Boolean.parseBoolean(searchByField),regionId,paging);
				}
				if(fieldName.equalsIgnoreCase("isActivate")) {
					students = studentRepository.findStudentsByIsActivateAndRegionId(Boolean.parseBoolean(searchByField),regionId,paging);
				}
			}
		} else students = studentRepository.findStudentsByRegionId(regionId,paging);

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

}
