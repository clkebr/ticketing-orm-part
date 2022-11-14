package com.cydeo.service.impl;

import com.cydeo.dto.TaskDTO;
import com.cydeo.entity.Task;
import com.cydeo.enums.Status;
import com.cydeo.mapper.TaskMapper;
import com.cydeo.repository.TaskRepository;
import com.cydeo.service.TaskService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskDTO findById(Long id) {
        return taskMapper.convertToDTO(taskRepository.findById(id).get());
    }

    @Override
    public List<TaskDTO> listAllTasks() {
        List<Task> taskList = taskRepository.findAll();
        return taskList.stream().map(taskMapper::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public void save(TaskDTO dto) {
        dto.setTaskStatus(Status.OPEN);
        dto.setAssignedDate(LocalDate.now());
        Task toEntity = taskMapper.convertToEntity(dto);
        taskRepository.save(toEntity);

    }

    @Override
    public void update(TaskDTO dto) {
        Optional<Task> taskOptional = taskRepository.findById(dto.getId());
        Task toEntity = taskRepository.save(taskMapper.convertToEntity(dto));

        if (taskOptional.isPresent()){
            toEntity.setId(taskOptional.get().getId());
            toEntity.setTaskStatus(taskOptional.get().getTaskStatus());
            toEntity.setAssignedDate(taskOptional.get().getAssignedDate());
            taskRepository.save(toEntity);
        }

    }

    @Override
    public void delete(Long id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            taskOptional.get().setDeleted(true);
            taskRepository.save(taskOptional.get());
        }




    }

    @Override
    public int totalNonCompletedTask(String projectCode) {
        return taskRepository.totalNonCompletedTasks(projectCode);
    }

    @Override
    public int totalCompletedTask(String projectCode) {
        return taskRepository.totalCompletedTasks(projectCode);
    }
}
