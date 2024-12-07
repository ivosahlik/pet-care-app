package cz.ivosahlik.api.service.review;

import cz.ivosahlik.api.exception.AlreadyExistsException;
import cz.ivosahlik.api.exception.ResourceNotFoundException;
import cz.ivosahlik.api.model.Review;
import cz.ivosahlik.api.model.User;
import cz.ivosahlik.api.repository.AppointmentRepository;
import cz.ivosahlik.api.repository.ReviewRepository;
import cz.ivosahlik.api.repository.UserRepository;
import cz.ivosahlik.api.request.ReviewUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static cz.ivosahlik.api.enums.AppointmentStatus.*;
import static cz.ivosahlik.api.utils.FeedBackMessage.*;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    public static final double DOUBLE_ZERO_ZERO = 0.0;
    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Override
    public Review saveReview(Review review,
                             Long reviewerId,
                             Long veterinarianId) {
        if (veterinarianId.equals(reviewerId)) {
            throw new IllegalArgumentException(CANNOT_REVIEW);
        }

        Optional<Review> existingReview = reviewRepository.findByVeterinarianIdAndPatientId(veterinarianId, reviewerId);
        if (existingReview.isPresent()) {
            throw new AlreadyExistsException(ALREADY_REVIEWED);
        }
        boolean hadCompletedAppointments = appointmentRepository.existsByVeterinarianIdAndPatientIdAndStatus(veterinarianId, reviewerId, COMPLETED);
        if (!hadCompletedAppointments) {
            throw new IllegalStateException(NOT_ALLOWED);
        }

        User veterinarian = userRepository.findById(veterinarianId)
                .orElseThrow(() -> new ResourceNotFoundException(VET_OR_PATIENT_NOT_FOUND));

        User patient = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException(VET_OR_PATIENT_NOT_FOUND));

        review.setVeterinarian(veterinarian);
        review.setPatient(patient);
        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    @Override
    public double getAverageRatingForVet(Long veterinarianId) {
        List<Review> reviews = reviewRepository.findByVeterinarianId(veterinarianId);
        return reviews.isEmpty() ? DOUBLE_ZERO_ZERO : reviews.stream()
                .mapToInt(Review::getStars)
                .average()
                .orElse(DOUBLE_ZERO_ZERO);
    }

    @Override
    public Review updateReview(Long reviewerId, ReviewUpdateRequest review) {
        return reviewRepository.findById(reviewerId)
                .map(existingReview -> getReview(review, existingReview))
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND));
    }

    private Review getReview(ReviewUpdateRequest review, Review existingReview) {
        existingReview.setStars(review.getStars());
        existingReview.setFeedback(review.getFeedback());
        return reviewRepository.save(existingReview);
    }

    @Override
    public Page<Review> findAllReviewsByUserId(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return reviewRepository.findAllByUserId(userId, pageRequest);
    }

    @Override
    public void deleteReview(Long reviewerId) {
        reviewRepository.findById(reviewerId)
                .ifPresentOrElse(Review::removeRelationShip, () -> {
                    throw new ResourceNotFoundException(RESOURCE_NOT_FOUND);
                });
        reviewRepository.deleteById(reviewerId);
    }

}
