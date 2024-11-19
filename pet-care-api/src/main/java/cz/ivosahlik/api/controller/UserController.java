package cz.ivosahlik.api.controller;

import cz.ivosahlik.api.dto.EntityConverter;
import cz.ivosahlik.api.dto.UserDto;
import cz.ivosahlik.api.event.RegistrationCompleteEvent;
import cz.ivosahlik.api.exception.ResourceNotFoundException;
import cz.ivosahlik.api.exception.AlreadyExistsException;
import cz.ivosahlik.api.model.User;
import cz.ivosahlik.api.request.ChangePasswordRequest;
import cz.ivosahlik.api.request.RegistrationRequest;
import cz.ivosahlik.api.request.UserUpdateRequest;
import cz.ivosahlik.api.response.ApiResponse;
import cz.ivosahlik.api.service.password.IChangePasswordService;
import cz.ivosahlik.api.service.user.UserService;
import cz.ivosahlik.api.utils.FeedBackMessage;
import cz.ivosahlik.api.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RequestMapping(UrlMapping.USERS)
@RestController
public class UserController {
    private final UserService userService;
    private final EntityConverter<User, UserDto> entityConverter;
    private final IChangePasswordService changePasswordService;
    private final ApplicationEventPublisher publisher;

    @PostMapping(UrlMapping.REGISTER_USER)
    public ResponseEntity<ApiResponse> register(@RequestBody RegistrationRequest request) {
        try {
            User theUser = userService.register(request);
            publisher.publishEvent(new RegistrationCompleteEvent(theUser));
            UserDto registeredUser = entityConverter.mapEntityToDto(theUser, UserDto.class);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.CREATE_USER_SUCCESS, registeredUser));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.UPDATE_USER)
    public ResponseEntity<ApiResponse> update(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        try {
            User theUser = userService.update(userId, request);
            UserDto updatedUser = entityConverter.mapEntityToDto(theUser, UserDto.class);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.USER_UPDATE_SUCCESS, updatedUser));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.GET_USER_BY_ID)
    public ResponseEntity<ApiResponse> findById(@PathVariable Long userId) {
        try {
            UserDto userDto = userService.getUserWithDetails(userId);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.USER_FOUND, userDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping(UrlMapping.DELETE_USER_BY_ID)
    public ResponseEntity<ApiResponse> deleteById(@PathVariable Long userId) {
        try {
            userService.delete(userId);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.DELETE_USER_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.GET_ALL_USERS)
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UserDto> theUsers = userService.getAllUsers();
        return ResponseEntity.status(FOUND).body(new ApiResponse(FeedBackMessage.USER_FOUND, theUsers));
    }

    @PutMapping(UrlMapping.CHANGE_PASSWORD)
    public ResponseEntity<ApiResponse> changePassword(@PathVariable Long userId,
                                                      @RequestBody ChangePasswordRequest request) {
        try {
            changePasswordService.changePassword(userId, request);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.PASSWORD_CHANGE_SUCCESS, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.COUNT_ALL_VETS)
    public long countVeterinarians() {
        return userService.countVeterinarians();
    }

    @GetMapping(UrlMapping.COUNT_ALL_PATIENTS)
    public long countPatients() {
        return userService.countPatients();
    }

    @GetMapping(UrlMapping.COUNT_ALL_USERS)
    public long countUsers() {
        return userService.countAllUsers();
    }

    @GetMapping(UrlMapping.AGGREGATE_USERS)
    public ResponseEntity<ApiResponse> aggregateUsersByMonthAndType() {
        try {
            Map<String, Map<String, Long>> aggregatedUsers = userService.aggregateUsersByMonthAndType();
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.RESOURCE_FOUND, aggregatedUsers));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.AGGREGATE_BY_STATUS)
    public ResponseEntity<ApiResponse> getAggregatedUsersByEnabledStatus() {
        try {
            Map<String, Map<String, Long>> aggregatedData = userService.aggregateUsersByEnabledStatusAndType();
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.RESOURCE_FOUND, aggregatedData));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.LOCK_USER_ACCOUNT)
    public ResponseEntity<ApiResponse> lockUserAccount(@PathVariable Long userId) {
        try {
            userService.lockUserAccount(userId);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.LOCKED_ACCOUNT_SUCCESS, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.UNLOCK_USER_ACCOUNT)
    public ResponseEntity<ApiResponse> unLockUserAccount(@PathVariable Long userId) {
        try {
            userService.unLockUserAccount(userId);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.UNLOCKED_ACCOUNT_SUCCESS, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

}


