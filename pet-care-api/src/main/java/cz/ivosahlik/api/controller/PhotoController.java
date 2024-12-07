package cz.ivosahlik.api.controller;

import cz.ivosahlik.api.exception.ResourceNotFoundException;
import cz.ivosahlik.api.model.Photo;
import cz.ivosahlik.api.response.ApiResponse;
import cz.ivosahlik.api.service.photo.PhotoService;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;

import static cz.ivosahlik.api.utils.FeedBackMessage.PHOTO_REMOVE_SUCCESS;
import static cz.ivosahlik.api.utils.FeedBackMessage.PHOTO_UPDATE_SUCCESS;
import static cz.ivosahlik.api.utils.FeedBackMessage.RESOURCE_FOUND;
import static cz.ivosahlik.api.utils.UrlMapping.DELETE_PHOTO;
import static cz.ivosahlik.api.utils.UrlMapping.GET_PHOTO_BY_ID;
import static cz.ivosahlik.api.utils.UrlMapping.PHOTOS;
import static cz.ivosahlik.api.utils.UrlMapping.UPDATE_PHOTO;
import static cz.ivosahlik.api.utils.UrlMapping.UPLOAD_PHOTO;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping(PHOTOS)
@RequiredArgsConstructor
public class PhotoController {
    private final PhotoService photoService;

    @PostMapping(UPLOAD_PHOTO)
    public ResponseEntity<ApiResponse> savePhoto(
            @RequestParam MultipartFile file,
            @RequestParam Long userId) {
        try {
            Photo photo = photoService.savePhoto(file, userId);
            return ok(new ApiResponse(PHOTO_UPDATE_SUCCESS, photo.getId()));
        } catch (IOException | SQLException e) {
            return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(value = GET_PHOTO_BY_ID)
    public ResponseEntity<ApiResponse> getPhotoById(@PathVariable Long photoId) {
        try {
            Photo photo = photoService.getPhotoById(photoId);
            if (photo != null) {
                byte[] photoBytes = photoService.getImageData(photo.getId());
                return ok(new ApiResponse(RESOURCE_FOUND, photoBytes));
            }
        } catch (ResourceNotFoundException | SQLException e) {
            return status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
        return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(null, NOT_FOUND));
    }

    @DeleteMapping(DELETE_PHOTO)
    public ResponseEntity<ApiResponse> deletePhoto(@PathVariable Long photoId, @PathVariable Long userId) {
        try {
            Photo photo = photoService.getPhotoById(photoId);
            if (photo != null) {
                photoService.deletePhoto(photo.getId(), userId);
                return ok(new ApiResponse(PHOTO_REMOVE_SUCCESS, photo.getId()));
            }
        } catch (ResourceNotFoundException | SQLException e) {
            return status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
        return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(null, INTERNAL_SERVER_ERROR));
    }

    @PutMapping(UPDATE_PHOTO)
    public ResponseEntity<ApiResponse> updatePhoto(@PathVariable Long photoId, @RequestBody MultipartFile file) throws SQLException {
        try {
            Photo photo = photoService.getPhotoById(photoId);
            if (photo != null) {
                Photo updatedPhoto = photoService.updatePhoto(photo.getId(), file);
                return ok(new ApiResponse(PHOTO_UPDATE_SUCCESS, updatedPhoto.getId()));
            }
        } catch (ResourceNotFoundException | IOException e) {
            return status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
        return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(null, INTERNAL_SERVER_ERROR));
    }

}
