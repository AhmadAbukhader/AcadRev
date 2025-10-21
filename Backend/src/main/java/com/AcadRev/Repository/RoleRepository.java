package com.AcadRev.Repository;

import com.AcadRev.Model.Role;
import com.AcadRev.Model.UserType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
    Role findById (int id);
    Role findByRole(UserType role);
}

