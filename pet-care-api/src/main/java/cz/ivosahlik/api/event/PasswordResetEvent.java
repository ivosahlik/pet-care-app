package cz.ivosahlik.api.event;

import cz.ivosahlik.api.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class PasswordResetEvent extends ApplicationEvent {
    private final User user;
    public PasswordResetEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
