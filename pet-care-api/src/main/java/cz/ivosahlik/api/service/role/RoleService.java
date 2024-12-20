package cz.ivosahlik.api.service.role;

import cz.ivosahlik.api.model.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {
    List<Role> getAllRoles();
    Role getRoleById(Long id);
    Role getRoleByName(String roleName);
    void saveRole(Role role);
    Set<Role> setUserRole(String userType);

}
