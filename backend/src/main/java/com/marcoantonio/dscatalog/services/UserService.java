package com.marcoantonio.dscatalog.services;


import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import com.marcoantonio.dscatalog.dtos.RoleDTO;
import com.marcoantonio.dscatalog.dtos.UserDTO;
import com.marcoantonio.dscatalog.dtos.UserInsertDTO;
import com.marcoantonio.dscatalog.entities.Role;
import com.marcoantonio.dscatalog.entities.User;
import com.marcoantonio.dscatalog.repositories.RoleRepository;
import com.marcoantonio.dscatalog.repositories.UserRepository;
import com.marcoantonio.dscatalog.services.exceptions.DatabaseException;
import com.marcoantonio.dscatalog.services.exceptions.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(PageRequest pageRequest) {
        Page<User> list  =  userRepository.findAll(pageRequest);
        Page<UserDTO> listDTO = list.map(userObject -> new UserDTO(userObject));
        return listDTO;
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        Optional<User> userObj = userRepository.findById(id);
        User entity = userObj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO createdUser(UserInsertDTO userDTO) {
        User entity = new User();
        copyDtoToEntity(userDTO, entity);
        entity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        entity = userRepository.save(entity);
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO updatedUser( Long id, UserDTO userDTO) {
       try {
        User entity = userRepository.getOne(id);
        copyDtoToEntity(userDTO, entity);
        entity = userRepository.save(entity);
        return new UserDTO(entity);
       } catch (EntityNotFoundException e ) {
           throw new ResourceNotFoundException("Id not Found " + id); 
       }
    }

    public void delete(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
        catch (DataIntegrityViolationException e ){
            throw new DatabaseException("Integrity violation");
        }
    }

    private void copyDtoToEntity(UserDTO userDTO, User entity) {
        entity.setFirstName(userDTO.getFirstName());
        entity.setLastName(userDTO.getLastName());
        entity.setEmail(userDTO.getEmail());
        
        entity.getRoles().clear();
        for(RoleDTO roleDTO : userDTO.getRoles()){
            Role role = roleRepository.getOne(roleDTO.getId());
            entity.getRoles().add(role);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if(user == null){
            logger.error("User not found: " + username);
            throw new UsernameNotFoundException("Email not found");
        }
        logger.info("User found: "+ username);
        return user;
    }
    

}
