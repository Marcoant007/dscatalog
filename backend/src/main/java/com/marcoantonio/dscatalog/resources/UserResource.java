package com.marcoantonio.dscatalog.resources;

import java.net.URI;

import javax.validation.Valid;

import com.marcoantonio.dscatalog.dtos.UserDTO;
import com.marcoantonio.dscatalog.dtos.UserInsertDTO;
import com.marcoantonio.dscatalog.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "/users")
public class UserResource {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "firstName") String orderBy) {
         PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
         Page<UserDTO> list = userService.findAllPaged(pageRequest);
         return ResponseEntity.ok().body(list);   
    }

    @GetMapping(value ="/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id){
        UserDTO userDTO = userService.findById(id);
        return ResponseEntity.ok().body(userDTO);
    }

    @PostMapping
    public ResponseEntity<UserDTO> created(@Valid @RequestBody UserInsertDTO userDTO){
        UserDTO newUserDTO = userService.createdUser(userDTO);
        URI uri =  ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newUserDTO.getId())
        .toUri();
        return ResponseEntity.created(uri).body(newUserDTO);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> updated(@Valid @PathVariable Long id, @RequestBody UserDTO userDTO){
        userDTO = userService.updatedUser(id, userDTO);
        return ResponseEntity.ok().body(userDTO);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleted(@PathVariable Long id){
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}
