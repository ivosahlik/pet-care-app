package cz.ivosahlik.api.controller;

import cz.ivosahlik.api.dto.ReviewDto;
import cz.ivosahlik.api.exception.AlreadyExistsException;
import cz.ivosahlik.api.exception.ResourceNotFoundException;
import cz.ivosahlik.api.model.Review;
import cz.ivosahlik.api.request.ReviewUpdateRequest;
import cz.ivosahlik.api.response.ApiResponse;
import cz.ivosahlik.api.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
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

import static cz.ivosahlik.api.utils.FeedBackMessage.REVIEW_DELETE_SUCCESS;
import static cz.ivosahlik.api.utils.FeedBackMessage.REVIEW_FOUND;
import static cz.ivosahlik.api.utils.FeedBackMessage.REVIEW_SUBMIT_SUCCESS;
import static cz.ivosahlik.api.utils.FeedBackMessage.REVIEW_UPDATE_SUCCESS;
import static cz.ivosahlik.api.utils.UrlMapping.DELETE_REVIEW;
import static cz.ivosahlik.api.utils.UrlMapping.GET_AVERAGE_RATING;
import static cz.ivosahlik.api.utils.UrlMapping.GET_USER_REVIEWS;
import static cz.ivosahlik.api.utils.UrlMapping.REVIEWS;
import static cz.ivosahlik.api.utils.UrlMapping.SUBMIT_REVIEW;
import static cz.ivosahlik.api.utils.UrlMapping.UPDATE_REVIEW;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RequiredArgsConstructor
@RequestMapping(REVIEWS)
@RestController
public class ReviewController {
    private final ReviewService reviewService;
    private final ModelMapper modelMapper;

    @PostMapping(SUBMIT_REVIEW)
    public ResponseEntity<ApiResponse> saveReview(@RequestParam Long reviewerId,
                                                  @RequestParam Long vetId,
                                                  @RequestBody Review review) {
        try {
            Review savedReview = reviewService.saveReview(review, reviewerId, vetId);
            return ok(new ApiResponse(REVIEW_SUBMIT_SUCCESS, savedReview.getId()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(), null));
        } catch (AlreadyExistsException e) {
            return status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UPDATE_REVIEW)
    public ResponseEntity<ApiResponse> updateReview(@RequestBody ReviewUpdateRequest updateRequest,
                                                    @PathVariable Long reviewId) {
        try {
            Review updatedReview = reviewService.updateReview(reviewId, updateRequest);
            return ok(new ApiResponse(REVIEW_UPDATE_SUCCESS, updatedReview.getId()));
        } catch (ResourceNotFoundException e) {
            return status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping(DELETE_REVIEW)
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable Long reviewId) {
        try {
            reviewService.deleteReview(reviewId);
            return ok(new ApiResponse(REVIEW_DELETE_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(GET_USER_REVIEWS)
    public ResponseEntity<ApiResponse> getReviewsByUserID(@PathVariable Long userId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size) {
        Page<Review> reviewPage = reviewService.findAllReviewsByUserId(userId, page, size);
        Page<ReviewDto> reviewDtos = reviewPage.map((element) -> modelMapper.map(element, ReviewDto.class));
        return status(FOUND).body(new ApiResponse(REVIEW_FOUND, reviewDtos));
    }

    @GetMapping(GET_AVERAGE_RATING)
    public ResponseEntity<ApiResponse> getAverageRatingForVet(@PathVariable Long vetId) {
        double averageRating = reviewService.getAverageRatingForVet(vetId);
        return ok(new ApiResponse(REVIEW_FOUND, averageRating));
    }

}


