package cz.ivosahlik.api.request;

import cz.ivosahlik.api.model.User;
import lombok.Data;

import java.util.Date;

@Data
public class VerificationTokenRequest {
    private String token;
    private Date expirationTime;
    private User user;
}
