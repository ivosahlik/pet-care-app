package cz.ivosahlik.api.service.photo;

import cz.ivosahlik.api.exception.ResourceNotFoundException;
import cz.ivosahlik.api.model.Photo;
import cz.ivosahlik.api.model.User;
import cz.ivosahlik.api.repository.PhotoRepository;
import cz.ivosahlik.api.repository.UserRepository;
import cz.ivosahlik.api.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;

    @Override
    public Photo savePhoto(MultipartFile file, Long userId) throws IOException, SQLException {
        Optional<User> theUser = userRepository.findById(userId);
        if (theUser.isEmpty()) {
            return null;
        }
        Photo photo = new Photo();
        if (file != null && !file.isEmpty()) {
            byte[] photoBytes = file.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            photo.setImage(photoBlob);
            photo.setFileType(file.getContentType());
            photo.setFileName(file.getOriginalFilename());
        }
        Photo savedPhoto = photoRepository.save(photo);
        theUser.ifPresent(user -> user.setPhoto(savedPhoto));
        userRepository.save(theUser.get());
        return savedPhoto;
    }


    @Transactional
    @Override
    public void deletePhoto(Long id, Long userId) {
        userRepository.findById(userId).ifPresentOrElse(User::removeUserPhoto, () -> {
            throw new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND);
        });
        photoRepository.findById(id)
                .ifPresentOrElse(photoRepository::delete, () -> {
                    throw new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND);
                });
    }

    @Override
    public Photo updatePhoto(Long id, MultipartFile file) throws SQLException, IOException {
        Photo photo = getPhotoById(id);
        if (photo == null) {
            throw new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND);
        }
        byte[] photoBytes = file.getBytes();
        Blob photoBlob = new SerialBlob(photoBytes);
        photo.setImage(photoBlob);
        photo.setFileType(file.getContentType());
        photo.setFileName(file.getOriginalFilename());
        return photoRepository.save(photo);
    }

    @Override
    public Photo getPhotoById(Long id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.RESOURCE_FOUND));
    }

    @Override
    public byte[] getImageData(Long id) throws SQLException {
        Photo photo = getPhotoById(id);
        if (photo == null) {
            return new byte[0];
        }
        Blob photoBlob = photo.getImage();
        int blobLength = (int) photoBlob.length();
        return photoBlob.getBytes(1, blobLength);
    }
}
