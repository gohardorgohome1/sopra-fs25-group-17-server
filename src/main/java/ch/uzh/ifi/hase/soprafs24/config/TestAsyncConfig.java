package ch.uzh.ifi.hase.soprafs24.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.core.task.SyncTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class TestAsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        return new SyncTaskExecutor(); // Executes @Async methods synchronously during tests
    }
}