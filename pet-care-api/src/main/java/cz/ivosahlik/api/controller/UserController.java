package cz.ivosahlik.api.controller;

import cz.ivosahlik.api.dto.EntityConverter;
import cz.ivosahlik.api.dto.UserDto;
import cz.ivosahlik.api.event.RegistrationCompleteEvent;
import cz.ivosahlik.api.exception.AlreadyExistsException;
import cz.ivosahlik.api.exception.ResourceNotFoundException;
import cz.ivosahlik.api.model.User;
import cz.ivosahlik.api.request.ChangePasswordRequest;
import cz.ivosahlik.api.request.RegistrationRequest;
import cz.ivosahlik.api.request.UserUpdateRequest;
import cz.ivosahlik.api.response.ApiResponse;
import cz.ivosahlik.api.service.password.ChangePasswordService;
import cz.ivosahlik.api.service.user.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static cz.ivosahlik.api.utils.FeedBackMessage.CREATE_USER_SUCCESS;
import static cz.ivosahlik.api.utils.FeedBackMessage.DELETE_USER_SUCCESS;
import static cz.ivosahlik.api.utils.FeedBackMessage.LOCKED_ACCOUNT_SUCCESS;
import static cz.ivosahlik.api.utils.FeedBackMessage.PASSWORD_CHANGE_SUCCESS;
import static cz.ivosahlik.api.utils.FeedBackMessage.RESOURCE_FOUND;
import static cz.ivosahlik.api.utils.FeedBackMessage.UNLOCKED_ACCOUNT_SUCCESS;
import static cz.ivosahlik.api.utils.FeedBackMessage.USER_FOUND;
import static cz.ivosahlik.api.utils.FeedBackMessage.USER_UPDATE_SUCCESS;
import static cz.ivosahlik.api.utils.UrlMapping.AGGREGATE_BY_STATUS;
import static cz.ivosahlik.api.utils.UrlMapping.AGGREGATE_USERS;
import static cz.ivosahlik.api.utils.UrlMapping.CHANGE_PASSWORD;
import static cz.ivosahlik.api.utils.UrlMapping.COUNT_ALL_PATIENTS;
import static cz.ivosahlik.api.utils.UrlMapping.COUNT_ALL_USERS;
import static cz.ivosahlik.api.utils.UrlMapping.COUNT_ALL_VETS;
import static cz.ivosahlik.api.utils.UrlMapping.DELETE_USER_BY_ID;
import static cz.ivosahlik.api.utils.UrlMapping.GET_ALL_USERS;
import static cz.ivosahlik.api.utils.UrlMapping.GET_USER_BY_ID;
import static cz.ivosahlik.api.utils.UrlMapping.LOCK_USER_ACCOUNT;
import static cz.ivosahlik.api.utils.UrlMapping.REGISTER_USER;
import static cz.ivosahlik.api.utils.UrlMapping.UNLOCK_USER_ACCOUNT;
import static cz.ivosahlik.api.utils.UrlMapping.UPDATE_USER;
import static cz.ivosahlik.api.utils.UrlMapping.USERS;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RequiredArgsConstructor
@RequestMapping(USERS)
@RestController
public class UserController {
    private final UserServiceImpl userServiceImpl;
    private final EntityConverter<User, UserDto> entityConverter;
    private final ChangePasswordService changePasswordService;
    private final ApplicationEventPublisher publisher;

    @PostMapping(REGISTER_USER)
    public ResponseEntity<ApiResponse> register(@RequestBody RegistrationRequest request) {
        try {
            User theUser = userServiceImpl.register(request);
            publisher.publishEvent(new RegistrationCompleteEvent(theUser));
            UserDto registeredUser = entityConverter.mapEntityToDto(theUser, UserDto.class);
            return ok(new ApiResponse(CREATE_USER_SUCCESS, registeredUser));
        } catch (AlreadyExistsException e) {
            return status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UPDATE_USER)
    public ResponseEntity<ApiResponse> update(@PathVariable Long userId,
                                              @RequestBody UserUpdateRequest request) {
        try {
            User theUser = userServiceImpl.update(userId, request);
            UserDto updatedUser = entityConverter.mapEntityToDto(theUser, UserDto.class);
            return ok(new ApiResponse(USER_UPDATE_SUCCESS, updatedUser));
        } catch (ResourceNotFoundException e) {
            return status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(GET_USER_BY_ID)
    public ResponseEntity<ApiResponse> findById(@PathVariable Long userId) {
        try {
            UserDto userDto = userServiceImpl.getUserWithDetails(userId);
            return ok(new ApiResponse(USER_FOUND, userDto));
        } catch (ResourceNotFoundException e) {
            return status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping(DELETE_USER_BY_ID)
    public ResponseEntity<ApiResponse> deleteById(@PathVariable Long userId) {
        try {
            userServiceImpl.delete(userId);
            return ok(new ApiResponse(DELETE_USER_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(GET_ALL_USERS)
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UserDto> theUsers = userServiceImpl.getAllUsers();
        return status(FOUND).body(new ApiResponse(USER_FOUND, theUsers));
    }

    @PutMapping(CHANGE_PASSWORD)
    public ResponseEntity<ApiResponse> changePassword(@PathVariable Long userId,
                                                      @RequestBody ChangePasswordRequest request) {
        try {
            changePasswordService.changePassword(userId, request);
            return ok(new ApiResponse(PASSWORD_CHANGE_SUCCESS, null));
        } catch (IllegalArgumentException e) {
            return badRequest().body(new ApiResponse(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(COUNT_ALL_VETS)
    public long countVeterinarians() {
        return userServiceImpl.countVeterinarians();
    }

    @GetMapping(COUNT_ALL_PATIENTS)
    public long countPatients() {
        return userServiceImpl.countPatients();
    }

    @GetMapping(COUNT_ALL_USERS)
    public long countUsers() {
        return userServiceImpl.countAllUsers();
    }

    @GetMapping(AGGREGATE_USERS)
    public ResponseEntity<ApiResponse> aggregateUsersByMonthAndType() {
        try {
            Map<String, Map<String, Long>> aggregatedUsers = userServiceImpl.aggregateUsersByMonthAndType();
            return ok(new ApiResponse(RESOURCE_FOUND, aggregatedUsers));
        } catch (Exception e) {
            return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(AGGREGATE_BY_STATUS)
    public ResponseEntity<ApiResponse> getAggregatedUsersByEnabledStatus() {
        try {
            Map<String, Map<String, Long>> aggregatedData = userServiceImpl.aggregateUsersByEnabledStatusAndType();
            return ok(new ApiResponse(RESOURCE_FOUND, aggregatedData));
        } catch (Exception e) {
            return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(LOCK_USER_ACCOUNT)
    public ResponseEntity<ApiResponse> lockUserAccount(@PathVariable Long userId) {
        try {
            userServiceImpl.lockUserAccount(userId);
            return ok(new ApiResponse(LOCKED_ACCOUNT_SUCCESS, null));
        } catch (Exception e) {
            return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UNLOCK_USER_ACCOUNT)
    public ResponseEntity<ApiResponse> unLockUserAccount(@PathVariable Long userId) {
        try {
            userServiceImpl.unLockUserAccount(userId);
            return ok(new ApiResponse(UNLOCKED_ACCOUNT_SUCCESS, null));
        } catch (Exception e) {
            return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

}


