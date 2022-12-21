package com.ubi.MasterService.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.ubi.MasterService.dto.classDto.ClassDto;
import com.ubi.MasterService.entity.ClassDetail;


@Component
public class ClassMapper {
	ModelMapper modelMapper= new ModelMapper();
	 
	//entity to DTO Mapping
 public ClassDto entityToDto(ClassDetail classDetail) {
		return modelMapper.map(classDetail, ClassDto.class);
	}
 
 public List<ClassDto> entitiesToDtos(List<ClassDetail> classDetail) {
        return classDetail.stream().filter(Objects::nonNull).map(this::entityToDto).collect(Collectors.toList());
    }
 
 public List<ClassDto> entitiesToDto(List<ClassDetail> classDetail) {
		return classDetail.stream().filter(Objects::nonNull).map(this::entityToDto).collect(Collectors.toList());
	}

	// DTO to entity Mapping
	public ClassDetail dtoToEntity(ClassDto classDto) {
		return modelMapper.map(classDto, ClassDetail.class);
	}
	
    public List<ClassDetail> dtosToEntities(List<ClassDto> classDtos) {
        return classDtos.stream().filter(Objects::nonNull).map(this::dtoToEntity).collect(Collectors.toList());
    }
      
}
