package cz.ivosahlik.api.controller;

import cz.ivosahlik.api.dto.ReviewDto;
import cz.ivosahlik.api.exception.AlreadyExistsException;
import cz.ivosahlik.api.exception.ResourceNotFoundException;
import cz.ivosahlik.api.model.Review;
import cz.ivosahlik.api.request.ReviewUpdateRequest;
import cz.ivosahlik.api.response.ApiResponse;
import cz.ivosahlik.api.service.review.IReviewService;
import cz.ivosahlik.api.utils.FeedBackMessage;
import cz.ivosahlik.api.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RequestMapping(UrlMapping.REVIEWS)
@RestController
public class ReviewController {
    private final IReviewService reviewService;
    private final ModelMapper modelMapper;

    @PostMapping(UrlMapping.SUBMIT_REVIEW)
    public ResponseEntity<ApiResponse> saveReview(@RequestParam Long reviewerId,
                                                  @RequestParam Long vetId,
                                                  @RequestBody Review review) {
        try {
            Review savedReview = reviewService.saveReview(review, reviewerId, vetId);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.REVIEW_SUBMIT_SUCCESS, savedReview.getId()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(), null));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.UPDATE_REVIEW)
    public ResponseEntity<ApiResponse> updateReview(@RequestBody ReviewUpdateRequest updateRequest,
                                                    @PathVariable Long reviewId) {
        try {
            Review updatedReview = reviewService.updateReview(reviewId, updateRequest);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.REVIEW_UPDATE_SUCCESS, updatedReview.getId()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping(UrlMapping.DELETE_REVIEW)
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable Long reviewId) {
        try {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.REVIEW_DELETE_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.GET_USER_REVIEWS)
    public ResponseEntity<ApiResponse> getReviewsByUserID(@PathVariable Long userId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size) {
        Page<Review> reviewPage = reviewService.findAllReviewsByUserId(userId, page, size);
        Page<ReviewDto> reviewDtos = reviewPage.map((element) -> modelMapper.map(element, ReviewDto.class));
        return ResponseEntity.status(FOUND).body(new ApiResponse(FeedBackMessage.REVIEW_FOUND, reviewDtos));
    }

    @GetMapping(UrlMapping.GET_AVERAGE_RATING)
    public ResponseEntity<ApiResponse> getAverageRatingForVet(@PathVariable Long vetId) {
        double averageRating = reviewService.getAverageRatingForVet(vetId);
        return ResponseEntity.ok(new ApiResponse(FeedBackMessage.REVIEW_FOUND, averageRating));
    }

}


