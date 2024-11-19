package cz.ivosahlik.api.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private Long id;
    private int stars;
    private String feedback;
    private Long veterinarianId;
    private String veterinarianName;
    private Long patientId;
    private String patientName;
    private byte[] patientImage;
    private byte[] veterinarianImage;
    
}
