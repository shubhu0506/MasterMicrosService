package com.ubi.MasterService.service;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ubi.MasterService.dto.classDto.SchholClassMappingDto;
import com.ubi.MasterService.dto.classDto.SchoolClassDto;
import com.ubi.MasterService.dto.response.Response;
import com.ubi.MasterService.dto.schoolDto.SchoolDto;
import com.ubi.MasterService.entity.ClassDetail;
import com.ubi.MasterService.entity.Region;
import com.ubi.MasterService.entity.School;
import com.ubi.MasterService.error.CustomException;
import com.ubi.MasterService.error.HttpStatusCode;
import com.ubi.MasterService.error.Result;
import com.ubi.MasterService.mapper.ClassMapper;
import com.ubi.MasterService.mapper.SchoolMapper;
import com.ubi.MasterService.repository.ClassRepository;
import com.ubi.MasterService.repository.RegionRepository;
import com.ubi.MasterService.repository.SchoolRepository;


@Service
public class SchoolServiceImpl implements SchoolService{

	@Autowired
	private SchoolMapper schoolMapper;
	
	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private SchoolRepository schoolRepository;
	
	@Autowired
	private ClassRepository classRepository;
	
	@Autowired
	private ClassMapper classMapper;
	
	Logger logger = LoggerFactory.getLogger(SchoolServiceImpl.class);
	
	//@Autowired
	//Result res;
	
	
	@Override
	public Response<SchoolDto> addSchool(SchoolDto schoolDto) {
	
		Result<SchoolDto> res = new Result<>();

		Response<SchoolDto> response = new Response<>();
		Optional<School> tempSchools = schoolRepository.findById(schoolDto.getSchoolId());
		
        School schoolName=schoolRepository.getSchoolByName(schoolDto.getName());
		
		School schoolCode=schoolRepository.getSchoolByCode(schoolDto.getCode());
		

		if (tempSchools.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_SCHOOL_FOUND.getCode(),
					HttpStatusCode.NO_SCHOOL_FOUND,
					HttpStatusCode.NO_SCHOOL_FOUND.getMessage(), res);
		}
		
		if(schoolName !=null)
		{
			throw new CustomException(HttpStatusCode.SCHOOL_NAME_ALREADY_EXISTS.getCode(),
					HttpStatusCode.SCHOOL_NAME_ALREADY_EXISTS,
					HttpStatusCode.SCHOOL_NAME_ALREADY_EXISTS.getMessage(), res);
		}
		
		if(schoolCode!=null)
		{
			throw new CustomException(HttpStatusCode.SCHOOL_CODE_ALREADY_EXISTS.getCode(),
					HttpStatusCode.SCHOOL_CODE_ALREADY_EXISTS,
					HttpStatusCode.SCHOOL_CODE_ALREADY_EXISTS.getMessage(), res);
		}
		
		School saveSchool = schoolRepository.save(schoolMapper.dtoToEntity(schoolDto));
		response.setStatusCode(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getMessage());
		response.setResult(new Result<SchoolDto>(schoolMapper.entityToDto(saveSchool)));
		return response;
	}


	@Override
	public Response<List<SchoolDto>> getAllSchools(Integer PageNumber, Integer PageSize) {
		
		Result<List<SchoolDto>> allSchoolResult = new Result<>();
		Pageable paging = PageRequest.of(PageNumber, PageSize);
		Response<List<SchoolDto>> getListofSchools = new Response<>();

		Page<School> list = this.schoolRepository.findAll(paging);
		List<SchoolDto> schoolDtos = schoolMapper.entitiesToDtos(list.toList());

		if (list.getSize() == 0) {
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
	public Response<SchoolDto> getSchoolById(int schoolId) {
		
		Response<SchoolDto> getSchool = new Response<>();
		Optional<School> sch =this.schoolRepository.findById(schoolId);
		Result<SchoolDto> schoolResult = new Result<>();
		if (!sch.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_SCHOOL_MATCH_WITH_ID.getCode(),
					HttpStatusCode.NO_SCHOOL_MATCH_WITH_ID,
					HttpStatusCode.NO_SCHOOL_MATCH_WITH_ID.getMessage(), schoolResult);
		}
		schoolResult.setData(schoolMapper.entityToDto(sch.get()));
		getSchool.setStatusCode(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getCode());
		getSchool.setMessage(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getMessage());
		getSchool.setResult(schoolResult);
		return getSchool;
		
	}
	
	@Override
	public Response<SchoolDto> getSchoolByName(String name) {
	
		Result<SchoolDto> res = new Result<>();
		res.setData(null);
		Response<SchoolDto> getSchoolName = new Response<>();
		Optional<School> sch =this.schoolRepository.findByname(name);
		Result<SchoolDto> schoolResult = new Result<>();
		if (!sch.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_SCHOOL_NAME_FOUND.getCode(),
					HttpStatusCode.NO_SCHOOL_NAME_FOUND,
					HttpStatusCode.NO_SCHOOL_NAME_FOUND.getMessage(), res);
		}
		schoolResult.setData(schoolMapper.entityToDto(sch.get()));
		getSchoolName.setStatusCode(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getCode());
		getSchoolName.setMessage(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getMessage());
		getSchoolName.setResult(schoolResult);
		return getSchoolName;
	}		

	@Override
	public Response<SchoolDto> deleteSchoolById(int schoolId) {
		Result<SchoolDto> res=new Result<>();
		res.setData(null);
		Optional<School> school = schoolRepository.findById(schoolId);
		if (!school.isPresent()) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
		}
		
	
		Region region=school.get().getRegion();
		if(region!=null)
		{
			region.getSchool().remove(school.get());
		}
		schoolRepository.deleteById(schoolId);
		Response<SchoolDto> response = new Response<>();
		response.setMessage(HttpStatusCode.SCHOOL_DELETED.getMessage());
		response.setStatusCode(HttpStatusCode.SCHOOL_DELETED.getCode());
		response.setResult(new Result<SchoolDto>(schoolMapper.entityToDto(school.get())));
		return response;
	}

	@Override
	public Response<SchoolDto> updateSchool(SchoolDto schoolDto) throws ParseException{
		
		Result<SchoolDto> res = new Result<>();

		res.setData(null);
		Optional<School> existingSchool = schoolRepository.findById(schoolDto.getSchoolId());
		if (!existingSchool.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_SCHOOL_FOUND.getCode(),
					HttpStatusCode.NO_SCHOOL_FOUND,
					HttpStatusCode.NO_SCHOOL_FOUND.getMessage(), res);
		}
		SchoolDto existingSchools = schoolMapper.entityToDto(existingSchool.get());
		existingSchools.setCode(schoolDto.getCode());
		existingSchools.setContact(schoolDto.getContact());
		existingSchools.setAddress(schoolDto.getAddress());
		existingSchools.setExemptionFlag(schoolDto.isExemptionFlag());
		existingSchools.setName(schoolDto.getName());
		existingSchools.setStrength(schoolDto.getStrength());
		existingSchools.setVvnAccount(schoolDto.getVvnAccount());
		existingSchools.setStrength(schoolDto.getStrength());
		existingSchools.setShift(schoolDto.getShift());
		existingSchools.setEmail(schoolDto.getEmail());
		existingSchools.setSchoolId(schoolDto.getSchoolId());
		
		School updateSchool = schoolRepository
				.save(schoolMapper.dtoToEntity(existingSchools));
		Response<SchoolDto> response = new Response<>();
		response.setMessage(HttpStatusCode.SCHOOL_UPDATED.getMessage());
		response.setStatusCode(HttpStatusCode.SCHOOL_UPDATED.getCode());
		response.setResult(new Result<>(schoolMapper.entityToDto(updateSchool)));
		return response;
		
	}


	@Override
	public Response<SchoolClassDto> addClass(SchholClassMappingDto schoolClassMappingDto) {
		int schoolId = schoolClassMappingDto.getSchoolId();
		long classId = schoolClassMappingDto.getClassId();
		Response<SchoolClassDto> response = new Response<>();
		Result<SchoolClassDto> res = new Result<>();
		School school = schoolRepository.getReferenceById(schoolId);
		ClassDetail classDetail = classRepository.getReferenceById(classId);
		List<ClassDetail> setOfClasses = school.getClassDetail();
		for (ClassDetail currentClass : setOfClasses) {
			if (currentClass.getClassId() == classDetail.getClassId()) {
				throw new CustomException(HttpStatusCode.MAPPING_ALREADY_EXIST.getCode(),
						HttpStatusCode.MAPPING_ALREADY_EXIST, HttpStatusCode.MAPPING_ALREADY_EXIST.getMessage(), res);
			}
		}
		school.getClassDetail().add(classDetail);
		classDetail.setSchool(school);
		//classDetail.getSchool().add(school);
		//classDetail.getSchool().add(school);
		classRepository.save(classDetail);
		schoolRepository.save(school);
		SchoolClassDto schoolClassDto = schoolMapper.toSchoolClassDto(school);
		response.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
		response.setMessage(HttpStatusCode.SUCCESSFUL.getMessage());
		response.setResult(new Result<>(schoolClassDto));
		return response;
	}

	@Override
	public Response<SchoolClassDto> getSchoolwithClass(int id) {
	
		Response<SchoolClassDto> response = new Response<>();
		Result<SchoolClassDto> res = new Result<>();

		Optional<School> school = this.schoolRepository.findById(id);
		System.out.println("scholessssssssssssssssss"+school);

		if (!school.isPresent()) {
			
			throw new CustomException(HttpStatusCode.NO_SCHOOL_MATCH_WITH_ID.getCode(),
					HttpStatusCode.NO_SCHOOL_MATCH_WITH_ID,
					HttpStatusCode.NO_SCHOOL_MATCH_WITH_ID.getMessage(), res);
			
		}
		SchoolClassDto schoolClassDto = new SchoolClassDto();
		schoolClassDto.setSchoolDto(schoolMapper.entityToDto(school.get()));
		schoolClassDto.setClassDto(classMapper.entitiesToDto(school.get().getClassDetail()));

		res.setData(schoolClassDto);
		System.out.println("scholessssssssssssssssss"+schoolClassDto);	
		response.setStatusCode(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.SCHOOL_RETRIVED_SUCCESSFULLY.getMessage());
		response.setResult(new Result<>(schoolClassDto));
		
		System.out.println("ssssssssssssssssssssssss"+response);
		return response;		
	}


	@Override
	public Response<List<SchoolDto>> getSchoolwithSort(String field) {
		
		Result<List<SchoolDto>> allSchoolResult = new Result<>();
		//	Pageable paging = PageRequest.of(pageNumber, pageSize);
			Response<List<SchoolDto>> getListofSchools = new Response<>();

			List<School> list = this.schoolRepository.findAll(Sort.by(Sort.Direction.ASC,field));
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
	


