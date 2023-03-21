package com.ubi.masterservice.service;

import java.io.IOException;
import java.util.*;
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
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ubi.masterservice.dto.classDto.ClassDto;
import com.ubi.masterservice.dto.classDto.ClassStudentDto;
import com.ubi.masterservice.dto.classDto.TeacherDto;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.studentDto.StudentDto;
import com.ubi.masterservice.dto.user.UserDto;
import com.ubi.masterservice.entity.ClassDetail;
import com.ubi.masterservice.entity.School;
import com.ubi.masterservice.entity.Student;
import com.ubi.masterservice.error.CustomException;
import com.ubi.masterservice.error.HttpStatusCode;
import com.ubi.masterservice.error.Result;
import com.ubi.masterservice.externalServices.UserFeignService;
import com.ubi.masterservice.mapper.ClassMapper;
import com.ubi.masterservice.mapper.SchoolMapper;
import com.ubi.masterservice.mapper.StudentMapper;
import com.ubi.masterservice.repository.ClassRepository;
import com.ubi.masterservice.repository.SchoolRepository;
import com.ubi.masterservice.repository.StudentRepository;
import com.ubi.masterservice.util.PermissionUtil;

@Service
public class ClassServiceImpl implements ClassService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassServiceImpl.class);

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private PermissionUtil permissionUtil;

    @Autowired
    private UserFeignService userFeignService;

    @Autowired
    private SchoolMapper schoolMapper;

    @Autowired
    private StudentMapper studentMapper;

    private String topicName = "master_topic_add";

    private String topicDelete = "master_delete";

    private String topicUpdateName = "master_topic_update";

    private NewTopic topic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public ClassServiceImpl(NewTopic topic, KafkaTemplate<String, String> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }


    public Response<ClassStudentDto> addClassDetails(ClassDto classDto) {

        Result<ClassStudentDto> res = new Result<>();
        Response<ClassStudentDto> response = new Response<>();
        if (classDto.getStream() == null || classDto.getStream().isEmpty()) {
            classDto.setStream("NA");
        }
//		ClassDetail className = classRepository.getClassByclassName(classDto.getClassName());
//		ClassDetail classCode = classRepository.getClassByclassCode(classDto.getClassCode());

        School school = schoolRepository.findByIdIfNotDeleted(classDto.getSchoolId());

        if (school != null && school.getClassDetail() != null) {
            for (ClassDetail classDetail : school.getClassDetail()) {
                if (classDetail.getClassName().equals(classDto.getClassName()) && classDetail.getSection().equals(classDto.getSection()) && classDetail.getStream().equals(classDto.getStream())) {
                    throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_EXISTS.getCode(),
                            HttpStatusCode.RESOURCE_ALREADY_EXISTS, "Class with given class name,section & stream already exists in this school", res);
                }
            }
        } else {
            throw new CustomException(HttpStatusCode.NO_SCHOOL_ADDED.getCode(),
                    HttpStatusCode.NO_SCHOOL_ADDED,
                    "Invalid School is being sent to map with Class", res);
        }


        ClassDetail classDetail = new ClassDetail();
        classDetail.setClassName(classDto.getClassName());
        classDetail.setClassCode(classDto.getClassCode());
        classDetail.setSection(classDto.getSection());
        classDetail.setStream(classDto.getStream());
        classDetail.setSchool(school);
        classDetail.setTeacherId(classDto.getTeacherId());
        classDetail.setStudents(new HashSet<>());

        TeacherDto teacherDto = null;


        if (classDetail.getTeacherId() != null) {
            ClassDetail classDetail1 = classRepository.findByTeacherId(classDetail.getTeacherId());
            if (classDetail1 != null) {
                throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
                        HttpStatusCode.BAD_REQUEST_EXCEPTION,
                        "Given teacher is already mapped with another class",
                        res);
            }

            String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
            ResponseEntity<Response<UserDto>> teacherResponse = userFeignService.getTeacherById(currJwtToken,
                    classDetail.getTeacherId().toString());
            UserDto userDto = teacherResponse.getBody().getResult().getData();
            if (userDto != null) {
                teacherDto = new TeacherDto(userDto.getId(), userDto.getContactInfoDto().getFirstName(),
                        userDto.getContactInfoDto().getLastName(), classDetail.getClassId(), school.getSchoolId());
            }
        }

        ClassDetail savedClass = classRepository.save(classDetail);
        ClassStudentDto classStudentDto = classMapper.toStudentDto(savedClass);
        classStudentDto.setTeacherDto(teacherDto);
        res.setData(classStudentDto);
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
        kafkaTemplate.send(topicName, 0, "Key1", jsonStr);
        LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));

        return response;
    }

    public Response<PaginationResponse<List<ClassStudentDto>>> getClassDetails(String fieldName, String searchByField, Integer PageNumber, Integer PageSize) {

        Result<PaginationResponse<List<ClassStudentDto>>> allClasses = new Result<>();
        Pageable pageing = PageRequest.of(PageNumber, PageSize);
        Response<PaginationResponse<List<ClassStudentDto>>> getListofClasses = new Response<PaginationResponse<List<ClassStudentDto>>>();
        ClassStudentDto classStudentDto = null;
        Set<StudentDto> studentDto = null;
        TeacherDto teacherDto = null;
        Page<ClassDetail> classList = this.classRepository.getAllAvailaibleClassDetails(pageing);

        List<ClassStudentDto> classDto = new ArrayList();
        PaginationResponse paginationResponse;

        if (!fieldName.equals("*") && !searchByField.equals("*")) {
            if (fieldName.equalsIgnoreCase("classCode")) {
                classList = classRepository.findByClassCode(searchByField, pageing);
            }
            if (fieldName.equalsIgnoreCase("className")) {
                classList = classRepository.findByClassNameIgnoreCase(searchByField, pageing);
            }
            if (fieldName.equalsIgnoreCase("section")) {
                classList = classRepository.findBySection(searchByField, pageing);
            }
            if (fieldName.equalsIgnoreCase("stream")) {
                classList = classRepository.findByStream(searchByField, pageing);
            }
            if (fieldName.equalsIgnoreCase("classId")) {
                classList = classRepository.findByClassId((Long.parseLong(searchByField)), pageing);
            }

            if (classList.getNumberOfElements() == 0) {
                getListofClasses.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
                getListofClasses.setMessage("No class Found with given field");
                getListofClasses.setResult(new Result(null));
                return getListofClasses;
            }
        }
        for (ClassDetail classDetail : classList) {
            classStudentDto = new ClassStudentDto();
            classStudentDto.setClassDto(classMapper.entityToDto(classDetail));
            classStudentDto.setSchoolDto(schoolMapper.entityToDto(classDetail.getSchool()));

            String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();

            if (classDetail.getTeacherId() != null) {
                ResponseEntity<Response<UserDto>> teacherResponse = userFeignService.getTeacherById(currJwtToken,
                        classDetail.getTeacherId().toString());
                UserDto userDto = teacherResponse.getBody().getResult().getData();
                if (userDto != null) {
                    teacherDto = new TeacherDto(userDto.getId(), userDto.getContactInfoDto().getFirstName(),
                            userDto.getContactInfoDto().getLastName(), classDetail.getClassId(), classDetail.getSchool().getSchoolId());
                }
            }

            studentDto = classDetail.getStudents().stream()
                    .map(students -> studentMapper.entityToDto(students)).collect(Collectors.toSet());
            classStudentDto.setStudentDto(studentDto);
            classStudentDto.setTeacherDto(teacherDto);
            classDto.add(classStudentDto);
        }

        if (classList.isEmpty()) {
            getListofClasses.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
            getListofClasses.setMessage("No class Found");
            getListofClasses.setResult(new Result(null));
            return getListofClasses;
        }

        paginationResponse = new PaginationResponse<List<ClassStudentDto>>(classDto, classList.getTotalPages(), classList.getTotalElements());


        allClasses.setData(paginationResponse);
        getListofClasses.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
        getListofClasses.setMessage("Class retrived");
        getListofClasses.setResult(allClasses);
        return getListofClasses;
    }


    public Response<ClassStudentDto> getClassById(Long classid) {

        Response<ClassStudentDto> getClass = new Response<>();
        Optional<ClassDetail> classDetail = this.classRepository.findById(classid);
        Result<ClassStudentDto> classResult = new Result<>();
        if (!classDetail.isPresent()) {
            throw new CustomException(HttpStatusCode.NO_CLASS_MATCH_WITH_ID.getCode(),
                    HttpStatusCode.NO_CLASS_MATCH_WITH_ID, HttpStatusCode.NO_CLASS_MATCH_WITH_ID.getMessage(),
                    classResult);
        }

        String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();

        TeacherDto teacherDto = null;

        if (classDetail.get().getTeacherId() != null) {
            ResponseEntity<Response<UserDto>> teacherResponse = userFeignService.getTeacherById(currJwtToken,
                    classDetail.get().getTeacherId().toString());
            UserDto userDto = teacherResponse.getBody().getResult().getData();
            if (userDto != null) {
                teacherDto = new TeacherDto(userDto.getId(), userDto.getContactInfoDto().getFirstName(),
                        userDto.getContactInfoDto().getLastName(), classDetail.get().getClassId(), classDetail.get().getSchool().getSchoolId());
            }
        }


        ClassStudentDto classStudentDto = new ClassStudentDto();
        classStudentDto.setClassDto(classMapper.entityToDto(classDetail.get()));
        classStudentDto.setSchoolDto(schoolMapper.entityToDto(classDetail.get().getSchool()));
        Set<StudentDto> studentDto = classDetail.get().getStudents().stream()
                .map(students -> studentMapper.entityToDto(students)).collect(Collectors.toSet());
        classStudentDto.setTeacherDto(teacherDto);
        classStudentDto.setStudentDto(studentDto);

        classResult.setData(classStudentDto);
        getClass.setStatusCode(HttpStatusCode.CLASS_RETREIVED_SUCCESSFULLY.getCode());
        getClass.setMessage(HttpStatusCode.CLASS_RETREIVED_SUCCESSFULLY.getMessage());
        getClass.setResult(classResult);
        return getClass;
    }

    public Response<ClassDto> deleteClassById(Long id) {
        System.out.println("----- deleting class ---" + id);
        Result<ClassDto> res = new Result<>();
        Response<ClassDto> response = new Response<>();
        Optional<ClassDetail> classes = classRepository.findById(id);
        if (!classes.isPresent()) {
            throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
                    HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
        }

        ClassDetail classs = classes.get();

        if (classs.getIsDeleted() == true) {
            throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_DELETED.getCode(), HttpStatusCode.RESOURCE_ALREADY_DELETED,
                    "Class with given Id is already deleted", res);
        }
        ClassDetail class1 = new ClassDetail();
        class1 = classs;


        if (classs.getIsDeleted() == true) {
            throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
                    HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
        }


        School school = classs.getSchool();
        school.getClassDetail().remove(classs);
        schoolRepository.save(school);

        for (Student studentId : classs.getStudents()) {
            studentId.setClassDetail(null);
            studentRepository.save(studentId);
        }
        classs.setIsDeleted(true);
        classRepository.save(classs);
        res.setData(classMapper.entityToDto(classs));
        response.setMessage(HttpStatusCode.CLASS_DELETED_SUCCESSFULLY.getMessage());
        response.setStatusCode(HttpStatusCode.CLASS_DELETED_SUCCESSFULLY.getCode());
        response.setResult(res);

        ObjectMapper obj = new ObjectMapper();

        String jsonStr = null;
        try {
            jsonStr = obj.writeValueAsString(res.getData());
            LOGGER.info(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        kafkaTemplate.send(topicDelete, 0, "Key3", jsonStr);
        LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
        return response;
    }

    public Response<ClassStudentDto> updateClassDetails(ClassDto classDetailDto) {
        Result<ClassStudentDto> res = new Result<>();
        Response<ClassStudentDto> response = new Response<>();
        Optional<ClassDetail> existingClassContainer = classRepository.findById(classDetailDto.getClassId());
        if (classDetailDto.getStream() == null || classDetailDto.getStream().isEmpty()) {
            classDetailDto.setStream("NA");
        }

        if (!existingClassContainer.isPresent()) {
            throw new CustomException(HttpStatusCode.NO_CLASS_FOUND.getCode(), HttpStatusCode.NO_CLASS_FOUND,
                    HttpStatusCode.NO_CLASS_FOUND.getMessage(), res);
        }

        School school = schoolRepository.findByIdIfNotDeleted(classDetailDto.getSchoolId());
        ClassDetail classDetails = existingClassContainer.get();
        StringBuilder existingClassName = new StringBuilder();
        existingClassName.append(classDetails.getClassName()).append(classDetails.getSection()).append(classDetails.getStream());

        StringBuilder newClassName = new StringBuilder();
        existingClassName.append(classDetailDto.getClassName()).append(classDetailDto.getSection()).append(classDetailDto.getStream());


        if (school != null && school.getClassDetail() != null) {
            if(!newClassName.equals(existingClassName)){
                for (ClassDetail classDetail : school.getClassDetail()) {
                    if (classDetail.getClassName().equals(classDetailDto.getClassName()) && classDetail.getSection().equals(classDetailDto.getSection()) && classDetail.getStream().equals(classDetailDto.getStream())) {
                        throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_EXISTS.getCode(),
                                HttpStatusCode.RESOURCE_ALREADY_EXISTS, "Class with given class name,section & stream already exists in this school", res);
                    }
                }
            }
        } else {
            throw new CustomException(HttpStatusCode.NO_SCHOOL_ADDED.getCode(),
                    HttpStatusCode.NO_SCHOOL_ADDED,
                    "Invalid School is being sent to map with Class", res);
        }


        ClassDto existingClassDetail = classMapper.entityToDto(existingClassContainer.get());
        existingClassDetail.setClassName(classDetailDto.getClassName());
        existingClassDetail.setClassCode(classDetailDto.getClassCode());
        existingClassDetail.setSection(classDetailDto.getSection());
        existingClassDetail.setStream(classDetailDto.getStream());
        existingClassDetail.setSchoolId(classDetailDto.getSchoolId());
        existingClassDetail.setTeacherId(classDetailDto.getTeacherId());

        String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();

        TeacherDto teacherDto = null;

        if (classDetailDto.getTeacherId() != null && existingClassContainer.get().getTeacherId() != classDetailDto.getTeacherId()) {

            ClassDetail classDetail1 = classRepository.findByTeacherId(existingClassDetail.getTeacherId());
            if (classDetail1 != null) {
                throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
                        HttpStatusCode.BAD_REQUEST_EXCEPTION,
                        "Given teacher is already mapped with another class",
                        res);
            }

            ResponseEntity<Response<UserDto>> teacherResponse = userFeignService.getTeacherById(currJwtToken,
                    classDetailDto.getTeacherId().toString());
            UserDto userDto = teacherResponse.getBody().getResult().getData();
            if (userDto != null) {
                teacherDto = new TeacherDto(userDto.getId(), userDto.getContactInfoDto().getFirstName(),
                        userDto.getContactInfoDto().getLastName(), existingClassDetail.getClassId(), existingClassDetail.getSchoolId());
            }
        }
        existingClassDetail.setTeacherId(classDetailDto.getTeacherId());

        ClassDetail classDetail1 = classMapper.dtoToEntity(existingClassDetail);
        ClassDetail updatedClassDetail = classRepository.save(classDetail1);
        ClassStudentDto classStudentDto = classMapper.toStudentDto(updatedClassDetail);
        classStudentDto.setTeacherDto(teacherDto);
        res.setData(classStudentDto);
        response.setMessage(HttpStatusCode.CLASS_UPDATED.getMessage());
        response.setStatusCode(HttpStatusCode.CLASS_UPDATED.getCode());
        response.setResult(res);

        ObjectMapper obj = new ObjectMapper();
        String jsonStr = null;
        try {
            jsonStr = obj.writeValueAsString(res.getData());
            LOGGER.info(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        kafkaTemplate.send(topicUpdateName, 0, "Key2", jsonStr);
        LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
        return response;
    }

    public Response<ClassStudentDto> removeClassTeacher(String classId) {
        Result<ClassStudentDto> res = new Result<>();
        Response<ClassStudentDto> response = new Response<>();
        Optional<ClassDetail> existingClassContainer = classRepository.findById(Long.parseLong(classId));
        if (!existingClassContainer.isPresent()) {
            throw new CustomException(HttpStatusCode.NO_CLASS_FOUND.getCode(), HttpStatusCode.NO_CLASS_FOUND,
                    HttpStatusCode.NO_CLASS_FOUND.getMessage(), res);
        }
        ClassDetail classDetail = existingClassContainer.get();
        classDetail.setTeacherId(null);
        ClassDetail updatedClassDetail = classRepository.save(classDetail);
        ClassStudentDto classStudentDto = classMapper.toStudentDto(updatedClassDetail);
        classStudentDto.setTeacherDto(null);

        res.setData(classStudentDto);

        response.setMessage(HttpStatusCode.CLASS_UPDATED.getMessage());
        response.setStatusCode(HttpStatusCode.CLASS_UPDATED.getCode());
        response.setResult(res);

        ObjectMapper obj = new ObjectMapper();
        String jsonStr = null;
        try {
            jsonStr = obj.writeValueAsString(res.getData());
            LOGGER.info(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        kafkaTemplate.send(topicUpdateName, 0, "Key2", jsonStr);
        LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
        return response;
    }

    @Override
    public Response<ClassStudentDto> getClassByName(String className) {

        Response<ClassStudentDto> getClass = new Response<ClassStudentDto>();
        ClassDetail classDetail = this.classRepository.getClassByclassName(className);
        Result<ClassStudentDto> classResult = new Result<>();
        if (classDetail == null) {
            throw new CustomException(HttpStatusCode.CLASS_NOT_FOUND.getCode(), HttpStatusCode.CLASS_NOT_FOUND,
                    HttpStatusCode.CLASS_NOT_FOUND.getMessage(), classResult);
        }

        String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();

        TeacherDto teacherDto = null;

        if (classDetail.getTeacherId() != null) {
            ResponseEntity<Response<UserDto>> teacherResponse = userFeignService.getTeacherById(currJwtToken,
                    classDetail.getTeacherId().toString());
            UserDto userDto = teacherResponse.getBody().getResult().getData();
            if (userDto != null) {
                teacherDto = new TeacherDto(userDto.getId(), userDto.getContactInfoDto().getFirstName(),
                        userDto.getContactInfoDto().getLastName(), classDetail.getClassId(), classDetail.getSchool().getSchoolId());
            }
        }


        ClassStudentDto classStudentDto = new ClassStudentDto();
        classStudentDto.setClassDto(classMapper.entityToDto(classDetail));
        classStudentDto.setSchoolDto(schoolMapper.entityToDto(classDetail.getSchool()));
        Set<StudentDto> studentDto = classDetail.getStudents().stream()
                .map(students -> studentMapper.entityToDto(students)).collect(Collectors.toSet());
        classStudentDto.setStudentDto(studentDto);
        classStudentDto.setTeacherDto(teacherDto);
        classResult.setData(classStudentDto);
        getClass.setStatusCode(HttpStatusCode.CLASS_RETREIVED_SUCCESSFULLY.getCode());
        getClass.setMessage(HttpStatusCode.CLASS_RETREIVED_SUCCESSFULLY.getMessage());
        getClass.setResult(classResult);
        return getClass;
    }

    @Override
    public Response<List<ClassDto>> getClasswithSort(String field) {

        Result<List<ClassDto>> allClassResult = new Result<>();

        Response<List<ClassDto>> getListofClasses = new Response<>();

        List<ClassDetail> list = this.classRepository.findAll(Sort.by(Sort.Direction.ASC, field));
        List<ClassDto> classDtos = classMapper.entitiesToDtos(list);

        if (list.size() == 0) {
            throw new CustomException(HttpStatusCode.NO_CLASS_FOUND.getCode(), HttpStatusCode.NO_CLASS_FOUND,
                    HttpStatusCode.NO_CLASS_FOUND.getMessage(), allClassResult);
        }
        allClassResult.setData(classDtos);
        getListofClasses.setStatusCode(HttpStatusCode.CLASS_RETRIVED_SUCCESSFULLY.getCode());
        getListofClasses.setMessage(HttpStatusCode.CLASS_RETRIVED_SUCCESSFULLY.getMessage());
        getListofClasses.setResult(allClassResult);
        return getListofClasses;
    }

    @Override
    public Response<ClassDto> getClassByTeacherId(Long teacherId) {
        ClassDetail classDetail = classRepository.findByTeacherId(teacherId);
        Response<ClassDto> response = new Response<>();
        if (classDetail == null) {
            response.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
            response.setMessage("No Class Found With Given Teacher Id");
            response.setResult(new Result<>(null));
            return response;
        }
        ClassDto classDto = classMapper.entityToDto(classDetail);
        response.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
        response.setMessage("Class Retrieved Successfully");
        response.setResult(new Result<>(classDto));
        return response;
    }


}
