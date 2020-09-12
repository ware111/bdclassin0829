package com.blackboard.classin.service;

import javax.mail.MessagingException;
import java.io.IOException;

public interface TimerTaskService {
    void scheduleTask() throws MessagingException, InterruptedException, IOException;
}
