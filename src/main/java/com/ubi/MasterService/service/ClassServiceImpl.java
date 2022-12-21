package com.ubi.MasterService.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ubi.MasterService.dto.classDto.ClassDto;
import com.ubi.MasterService.dto.response.Response;
import com.ubi.MasterService.dto.studentDto.StudentDto;
import com.ubi.MasterService.entity.ClassDetail;
import com.ubi.MasterService.entity.Student;
import com.ubi.MasterService.error.CustomException;
import com.ubi.MasterService.error.HttpStatusCode;
import com.ubi.MasterService.error.Result;
import com.ubi.MasterService.mapper.ClassMapper;
import com.ubi.MasterService.mapper.StudentMapper;
import com.ubi.MasterService.repository.ClassRepository;
import com.ubi.MasterService.repository.SchoolRepository;

@Service
public class ClassServiceImpl implements ClassService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClassServiceImpl.class);

	@Autowired
	private ClassRepository classRepository;

	@Autowired
	private SchoolRepository schoolRepository;
	
	@Autowired
	private ClassMapper classMapper;

	
	@Autowired
	private StudentMapper studentMapper;
	
	public Response<ClassDto> addClassDetails(ClassDto classDto) {

		Result<ClassDto> res = new Result<>();
		Response<ClassDto> response = new Response<>();
//		Optional<ClassDetail> tempClass = classRepository.findById(classDto.getClassId());
		ClassDetail className=classRepository.getClassByclassName(classDto.getClassName());
		ClassDetail classCode=classRepository.getClassByclassCode(classDto.getClassCode());
//		if (tempClass.isPresent()){
//			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
//					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
//		}
		if(className!=null)
		{
			throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_EXISTS.getCode(),
					HttpStatusCode.RESOURCE_ALREADY_EXISTS,HttpStatusCode.RESOURCE_ALREADY_EXISTS.getMessage(),
					res);
		}
		if(classCode!=null)
		{
			throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_EXISTS.getCode(),
					HttpStatusCode.RESOURCE_ALREADY_EXISTS,HttpStatusCode.RESOURCE_ALREADY_EXISTS.getMessage(),
					res);
		}
		ClassDetail saveClass = classRepository.save(classMapper.dtoToEntity(classDto));
		response.setStatusCode(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getMessage());
		response.setResult(new Result<ClassDto>(classMapper.entityToDto(saveClass)));
		return response;
	}

	public Response<List<ClassDto>> getClassDetails(Integer PageNumber, Integer PageSize) {
		
		Result<List<ClassDto>> allClasses = new Result<>();
		Pageable pageing = PageRequest.of(PageNumber, PageSize);
		Response<List<ClassDto>> getListofClasses = new Response<List<ClassDto>>();
		
		Page<ClassDetail> classList = this.classRepository.findAll(pageing);
		List<ClassDto> classDto = classMapper.entitiesToDtos(classList.toList());
		if (classList.getSize() == 0) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), allClasses);
		}
		allClasses.setData(classDto);
		getListofClasses.setStatusCode(HttpStatusCode.CLASS_RETREIVED_SUCCESSFULLY.getCode());
		getListofClasses.setMessage(HttpStatusCode.CLASS_RETREIVED_SUCCESSFULLY.getMessage());
		getListofClasses.setResult(allClasses);
		return getListofClasses;
	}
	
	public Response<ClassDto> getClassById(Long classidL) {
		
		Response<ClassDto> getClass = new Response<>();
		Optional<ClassDetail> classDetail =this.classRepository.findById(classidL);
		Result<ClassDto> classResult = new Result<>();
		if (!classDetail.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_CLASS_MATCH_WITH_ID.getCode(),
					HttpStatusCode.NO_CLASS_MATCH_WITH_ID,
					HttpStatusCode.NO_CLASS_MATCH_WITH_ID.getMessage(), classResult);
		}
		classResult.setData(classMapper.entityToDto(classDetail.get()));
		getClass.setStatusCode(HttpStatusCode.CLASS_RETREIVED_SUCCESSFULLY.getCode());
		getClass.setMessage(HttpStatusCode.CLASS_RETREIVED_SUCCESSFULLY.getMessage());
		getClass.setResult(classResult);
		return getClass;
	}

	public Response<ClassDto> deleteClassById(Long id) {
		Result<ClassDto> res = new Result<>();
		res.setData(null);
		Optional<ClassDetail> classes = classRepository.findById(id);
		if (!classes.isPresent()) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
		}
		
		/*for(School newSchools:classes.get().getSchool()) {
			newSchools.getClassDetail().remove(classes.get());
			schoolRepository.save(newSchools);
		}*/
		
		//classes.get().setSchool(new HashSet<>());
		classRepository.save(classes.get());
		classRepository.deleteById(id);	
		Response<ClassDto> response = new Response<>();
		response.setMessage(HttpStatusCode.CLASS_DELETED_SUCCESSFULLY.getMessage());
		response.setStatusCode(HttpStatusCode.CLASS_DELETED_SUCCESSFULLY.getCode());
		response.setResult(new Result<ClassDto>(classMapper.entityToDto(classes.get())));
		return response;
	}

	public Response<ClassDto> updateClassDetails(ClassDto classDetail) {
		Result<ClassDto> res = new Result<>();
		
		res.setData(null);
		Optional<ClassDetail> existingClassContainer = classRepository.findById(classDetail.getClassId());
		if (!existingClassContainer.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_CLASS_FOUND.getCode(), HttpStatusCode.NO_CLASS_FOUND,
					HttpStatusCode.NO_CLASS_FOUND.getMessage(), res);
		}
		ClassDto existingClass = classMapper.entityToDto(existingClassContainer.get());
		existingClass.setClassCode(classDetail.getClassCode());
		existingClass.setClassName(classDetail.getClassName());

		ClassDetail updateClass = classRepository.save(classMapper.dtoToEntity(existingClass));
		Response<ClassDto> response = new Response<>();
		response.setMessage(HttpStatusCode.CLASS_UPDATED.getMessage());
		response.setStatusCode(HttpStatusCode.CLASS_UPDATED.getCode());
		response.setResult(new Result<>(classMapper.entityToDto(updateClass)));
		return response;
	}

	@Override
	public Response<ClassDto> getClassByName(String className) {
		
		Response<ClassDto> getClass = new Response<ClassDto>();
		ClassDetail classDetail = classRepository.getClassByclassName(className);
		Result<ClassDto> classResult = new Result<>();
		if (classDetail==null) {
			throw new CustomException(HttpStatusCode.CLASS_NOT_FOUND.getCode(),
					HttpStatusCode.CLASS_NOT_FOUND, HttpStatusCode.CLASS_NOT_FOUND.getMessage(),
					classResult);
		}
		
		classResult.setData(classMapper.entityToDto(classDetail));
		getClass.setStatusCode(HttpStatusCode.CLASS_RETREIVED_SUCCESSFULLY.getCode());
		getClass.setMessage(HttpStatusCode.CLASS_RETREIVED_SUCCESSFULLY.getMessage());
		getClass.setResult(classResult);
		return getClass;
}

	@Override
	public Response<List<StudentDto>> getClasswithStudent(Long id) {
		Response<List<StudentDto>> response = new Response<>();
		Result<List<StudentDto>> res = new Result<>();
		Optional<ClassDetail> classDetail = this.classRepository.findById(id);

		if (!classDetail.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_MATCH_WITH_ID.getCode(),
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_MATCH_WITH_ID,
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_MATCH_WITH_ID.getMessage(), res);
		}

		ClassDetail classDetailss = classDetail.get();

		List<Student> student = classDetailss.getStudents();

		response.setResult(res);

		res.setData(studentMapper.entitiesToDtos(student));

		response.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getMessage());
		return response;
	}
	}


