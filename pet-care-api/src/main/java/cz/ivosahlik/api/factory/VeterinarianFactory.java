package cz.ivosahlik.api.factory;

import cz.ivosahlik.api.model.Veterinarian;
import cz.ivosahlik.api.repository.VeterinarianRepository;
import cz.ivosahlik.api.request.RegistrationRequest;
import cz.ivosahlik.api.service.role.IRoleService;
import cz.ivosahlik.api.service.user.UserAttributesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VeterinarianFactory {
    private final VeterinarianRepository veterinarianRepository;
    private final UserAttributesMapper userAttributesMapper;
    private final IRoleService roleService;

    public Veterinarian createVeterinarian(RegistrationRequest request) {
        Veterinarian veterinarian = new Veterinarian();
        veterinarian.setRoles(roleService.setUserRole("VET"));
        userAttributesMapper.setCommonAttributes(request, veterinarian);
        veterinarian.setSpecialization(request.getSpecialization());
        return veterinarianRepository.save(veterinarian);
    }
}
