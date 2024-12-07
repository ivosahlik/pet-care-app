package cz.ivosahlik.api.factory;

import cz.ivosahlik.api.model.Admin;
import cz.ivosahlik.api.repository.AdminRepository;
import cz.ivosahlik.api.request.RegistrationRequest;
import cz.ivosahlik.api.service.role.RoleService;
import cz.ivosahlik.api.service.user.UserAttributesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminFactory {
    private final AdminRepository adminRepository;
    private final UserAttributesMapper userAttributesMapper;
    private final RoleService roleService;

    public Admin createAdmin(RegistrationRequest request) {
        Admin admin = new Admin();
        admin.setRoles(roleService.setUserRole("ADMIN"));
        userAttributesMapper.setCommonAttributes(request, admin);
        return adminRepository.save(admin);
    }
}
