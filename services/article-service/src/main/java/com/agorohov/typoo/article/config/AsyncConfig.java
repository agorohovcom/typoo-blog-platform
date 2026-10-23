package com.agorohov.typoo.article.config;

import lombok.CustomLog;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@CustomLog
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("article-async-");

        // Этот декоратор нужен пока я не настроил Micrometer Tracing,
        // так как сейчас CustomLog.ensureTraceId() генерит новый трейс айди если его нет.
        // Потом заменить на ContextPropagatingTaskDecorator.
        executor.setTaskDecorator(runnable -> {
            Map<String, String> ctx = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    if (ctx != null) {
                        MDC.setContextMap(ctx);
                    }
                    runnable.run();
                } catch (Exception e) {
                    log.error("Async task failed", e);  // логируем пока MDC активен — traceId есть
                    throw e;
                } finally {
                    MDC.clear();
                }
            };
        });

        // Мягкая деградация: при насыщении пула — выполнить в вызывающем потоке
        executor.setRejectedExecutionHandler((r, e) -> {
            log.warn("Async pool saturated: pool={}, active={}, queue={}, running in caller thread",
                    e.getPoolSize(), e.getActiveCount(), e.getQueue().size());
            new ThreadPoolExecutor.CallerRunsPolicy().rejectedExecution(r, e);
        });

        executor.initialize();

        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        // Пока просто логируем ошибки асинхронного лиснера, но даже без трейс айди
        // В дальнейшем сюда можно будет добавить ретраи, метрики и алерты
        return (ex, method, params) ->
                log.error("Async method {} failed with params {}",
                        method.getName(), params,
                        ex);
    }
}
