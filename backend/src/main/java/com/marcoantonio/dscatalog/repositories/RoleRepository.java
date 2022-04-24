package com.marcoantonio.dscatalog.repositories;

import com.marcoantonio.dscatalog.entities.Role;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    
}
