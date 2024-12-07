package cz.ivosahlik.api.service.review;

import cz.ivosahlik.api.model.Review;
import cz.ivosahlik.api.request.ReviewUpdateRequest;
import org.springframework.data.domain.Page;

public interface ReviewService {
    Review saveReview(Review review, Long reviewerId, Long veterinarianId);
    double getAverageRatingForVet(Long veterinarianId);
    Review updateReview(Long reviewerId, ReviewUpdateRequest review);
    Page<Review> findAllReviewsByUserId(Long userId, int page, int size);
    void deleteReview(Long reviewerId);
}
