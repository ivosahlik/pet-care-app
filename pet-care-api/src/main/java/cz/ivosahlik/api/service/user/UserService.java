package cz.ivosahlik.api.service.user;

import cz.ivosahlik.api.dto.UserDto;
import cz.ivosahlik.api.model.User;
import cz.ivosahlik.api.request.RegistrationRequest;
import cz.ivosahlik.api.request.UserUpdateRequest;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface UserService {
    User register(RegistrationRequest request);

    User update(Long userId, UserUpdateRequest request);

    User findById(Long userId);

    void delete(Long userId);

    List<UserDto> getAllUsers();

    UserDto getUserWithDetails(Long userId) throws SQLException;

    long countVeterinarians();

    long countPatients();

    long countAllUsers();

    Map<String, Map<String,Long>> aggregateUsersByMonthAndType();

    Map<String, Map<String, Long>> aggregateUsersByEnabledStatusAndType();

}
