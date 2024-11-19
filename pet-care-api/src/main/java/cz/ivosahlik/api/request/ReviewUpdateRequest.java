package cz.ivosahlik.api.request;

import lombok.Data;

@Data
public class ReviewUpdateRequest {
    private int stars;
    private String feedback;
}
