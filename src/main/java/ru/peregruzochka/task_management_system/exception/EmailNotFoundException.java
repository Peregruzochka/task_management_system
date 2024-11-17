package ru.peregruzochka.task_management_system.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class EmailNotFoundException extends UsernameNotFoundException {
    public EmailNotFoundException(String msg) {
        super(msg);
    }
}

