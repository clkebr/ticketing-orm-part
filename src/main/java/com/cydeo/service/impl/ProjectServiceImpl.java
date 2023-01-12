package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final TaskService taskService;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper, @Lazy UserService userService, UserMapper userMapper, TaskService taskService) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.userService = userService;
        this.userMapper = userMapper;
        this.taskService = taskService;
    }


    @Override
    public ProjectDTO getByProjectCode(String code) {
       return projectMapper.convertToDTO(projectRepository.findByProjectCode(code));

    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        List<Project> projectList= projectRepository.findAll();
        return projectList.stream().map(projectMapper::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public void save(ProjectDTO dto) {
        dto.setProjectStatus(Status.OPEN);
        projectRepository.save(projectMapper.convertToEntity(dto));

    }

    @Override
    public void update(ProjectDTO dto) {

        Project project = projectRepository.findByProjectCode(dto.getProjectCode());
        Project toEntity = projectMapper.convertToEntity(dto);
        toEntity.setId(project.getId());
        toEntity.setProjectStatus(project.getProjectStatus());
        projectRepository.save(toEntity);

    }

    @Override
    public void delete(String code) {
        Project project = projectRepository.findByProjectCode(code);
        project.setIsDeleted(true);
        project.setProjectCode(project.getProjectCode()+"-"+project.getId());
        projectRepository.save(project);
        taskService.deleteByProject(project);
    }

    @Override
    public void complete(ProjectDTO dto) {
        Project project = projectRepository.findByProjectCode(dto.getProjectCode());
        project.setProjectStatus(Status.COMPLETE);
        taskService.completeByProject(dto);
        projectRepository.save(project);

    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() {
//        harold@manager.com

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserDTO currentUserDTO = userService.findByUserName(username);
        User user = userMapper.convertToEntity(currentUserDTO);

        List<Project> list = projectRepository.findAllByAssignedManager(user);

        return list.stream().map(project -> {

            ProjectDTO obj = projectMapper.convertToDTO(project);

            obj.setUnfinishedTaskCounts(taskService.totalNonCompletedTask(project.getProjectCode()));
            obj.setCompleteTaskCounts(taskService.totalCompletedTask(project.getProjectCode()));


            return obj;



        }).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> readAllByAssignedManager(User user) {
        List<Project> list = projectRepository.findAllByAssignedManager(user);
        return list.stream().map(projectMapper::convertToDTO).collect(Collectors.toList());
    }


}
