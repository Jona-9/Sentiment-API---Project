package com.project.sentimentapi.event;

import com.project.sentimentapi.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {
    private final User usuario;

    public UserRegisteredEvent(Object source, User usuario) {
        super(source);
        this.usuario = usuario;
    }
}
