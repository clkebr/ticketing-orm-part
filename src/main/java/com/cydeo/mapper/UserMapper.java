package com.cydeo.mapper;

import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User convertToEntity(UserDTO userDTO){
        return modelMapper.map(userDTO,User.class);
    }

    public UserDTO convertToDTO(User entity){
        return modelMapper.map(entity,UserDTO.class);

    }
}
