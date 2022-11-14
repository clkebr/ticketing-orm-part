package com.cydeo.service;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectService{

    ProjectDTO getByProjectCode(String code);

    List<ProjectDTO> listAllProjects();
    void save(ProjectDTO projectDTO);
    void update(ProjectDTO projectDTO);
    void delete(String code);

    void complete(ProjectDTO byProjectCode);

    List<ProjectDTO> listAllProjectDetails();
}
