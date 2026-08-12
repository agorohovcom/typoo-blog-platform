package com.agorohov.shared.utils.logging;

import lombok.CustomLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.function.Supplier;

/**
 * Lazy-логирование для Lombok с поддержкой traceId и отложенного вычисления аргументов.
 * <p>
 * Заменяет стандартный SLF4J-логгер, добавляя возможность использовать {@link Supplier}
 * <p>
 * для отложенного вычисления сообщений и аргументов логирования. Это позволяет избежать
 * <p>
 * затратных операций (таких, как создание строк или выполнение вычислений), когда
 * <p>
 * соответствующий уровень логирования отключен.
 * <p>
 * Особенности:
 * <p>
 * Автоматическое добавление traceId к логам
 * Lazy-вычисление сообщений через {@link Supplier}<String>
 * Lazy-вычисление аргументов через {@link Supplier}<?>
 * Смешанное использование обычных и lazy-аргументов
 * Полная совместимость со стандартными методами SLF4J (без traceId и lazy-вычислений)
 * Примеры использования:
 * <p>
 * // Вместо @slf4j используйте:
 * <p>
 * {@literal @}CustomLog
 * <p>
 * public class MyClass {
 * <p>
 * public void method() {
 * // Lazy-сообщение
 * log.debug(() -> "Expensive: " + expensiveOperation());
 * // Lazy-аргументы
 * log.info("User: {}, Data: {}", () -> getUserName(), () -> loadData());
 * // Смешанные аргументы
 * log.warn(() -> "Mix log for {}: {}", user, (Supplier<?>)() -> getMessage());
 * // Обычный лог (без lazy)
 * log.debug("Simple message");
 * }
 * }
 * <p>
 * Настройка Lombok:
 * <p>
 * Добавьте в файл {@code lombok.config}:
 * <p>
 * lombok.log.custom.declaration = com.agorohov.shared.utils.logging.CustomLogger com.agorohov.shared.utils.logging.CustomLogger.getLogger(TYPE)
 *
 * @see CustomLog
 * @see Supplier
 */
public class CustomLogger {

    private final Logger delegate;

    public CustomLogger(Class<?> clazz) {
        this.delegate = LoggerFactory.getLogger(clazz);
    }

    public CustomLogger(Logger logger) {
        this.delegate = logger;
    }

    public static CustomLogger getLogger(Class<?> clazz) {
        return new CustomLogger(clazz);
    }

    // TRACE LEVEL
    public void trace(String message) {
        ensureTraceId();
        delegate.trace(message);
    }

    public void trace(String format, Object arg) {
        ensureTraceId();
        delegate.trace(format, arg);
    }

    public void trace(String format, Object arg1, Object arg2) {
        ensureTraceId();
        delegate.trace(format, arg1, arg2);
    }

    public void trace(String msg, Throwable t) {
        ensureTraceId();
        delegate.trace(msg, t);
    }

    public void trace(Supplier<String> messageSupplier) {
        if (delegate.isTraceEnabled()) {
            ensureTraceId();
            delegate.trace(messageSupplier.get());
        }
    }

    public void trace(Supplier<String> messageSupplier, Throwable t) {
        if (delegate.isTraceEnabled()) {
            ensureTraceId();
            delegate.trace(messageSupplier.get(), t);
        }
    }

    public void trace(String format, Supplier<?>... lazyArgs) {
        if (delegate.isTraceEnabled()) {
            ensureTraceId();
            delegate.trace(format, extractSuppliers(lazyArgs));
        }
    }

    public void trace(Supplier<String> messageSupplier, Supplier<?>... lazyArgs) {
        if (delegate.isTraceEnabled()) {
            ensureTraceId();
            delegate.trace(messageSupplier.get(), extractSuppliers(lazyArgs));
        }
    }

    public void trace(String format, Object... mixedArgs) {
        if (!delegate.isTraceEnabled()) return;

        ensureTraceId();

        if (hasSuppliers(mixedArgs)) {
            delegate.trace(format, resolveSuppliers(mixedArgs));
        } else {
            delegate.trace(format, mixedArgs);
        }
    }

    public void trace(Supplier<String> messageSupplier, Object... mixedArgs) {
        if (!delegate.isTraceEnabled()) return;

        ensureTraceId();

        if (hasSuppliers(mixedArgs)) {
            delegate.trace(messageSupplier.get(), resolveSuppliers(mixedArgs));
        } else {
            delegate.trace(messageSupplier.get(), mixedArgs);
        }
    }

    // DEBUG LEVEL
    public void debug(String message) {
        ensureTraceId();
        delegate.debug(message);
    }

    public void debug(String format, Object arg) {
        ensureTraceId();
        delegate.debug(format, arg);
    }

    public void debug(String format, Object arg1, Object arg2) {
        ensureTraceId();
        delegate.debug(format, arg1, arg2);
    }

    public void debug(String msg, Throwable t) {
        ensureTraceId();
        delegate.debug(msg, t);
    }

    public void debug(Supplier<String> messageSupplier) {
        if (delegate.isDebugEnabled()) {
            ensureTraceId();
            delegate.debug(messageSupplier.get());
        }
    }

    public void debug(Supplier<String> messageSupplier, Throwable t) {
        if (delegate.isDebugEnabled()) {
            ensureTraceId();
            delegate.debug(messageSupplier.get(), t);
        }
    }

    public void debug(String format, Supplier<?>... lazyArgs) {
        if (delegate.isDebugEnabled()) {
            ensureTraceId();
            delegate.debug(format, extractSuppliers(lazyArgs));
        }
    }

    public void debug(Supplier<String> messageSupplier, Supplier<?>... lazyArgs) {
        if (delegate.isDebugEnabled()) {
            ensureTraceId();
            delegate.debug(messageSupplier.get(), extractSuppliers(lazyArgs));
        }
    }

    public void debug(String format, Object... mixedArgs) {
        if (!delegate.isDebugEnabled()) return;

        ensureTraceId();

        if (hasSuppliers(mixedArgs)) {
            delegate.debug(format, resolveSuppliers(mixedArgs));
        } else {
            delegate.debug(format, mixedArgs);
        }
    }

    public void debug(Supplier<String> messageSupplier, Object... mixedArgs) {
        if (!delegate.isDebugEnabled()) return;

        ensureTraceId();

        if (hasSuppliers(mixedArgs)) {
            delegate.debug(messageSupplier.get(), resolveSuppliers(mixedArgs));
        } else {
            delegate.debug(messageSupplier.get(), mixedArgs);
        }
    }

    // INFO LEVEL
    public void info(String message) {
        ensureTraceId();
        delegate.info(message);
    }

    public void info(String format, Object arg) {
        ensureTraceId();
        delegate.info(format, arg);
    }

    public void info(String format, Object arg1, Object arg2) {
        ensureTraceId();
        delegate.info(format, arg1, arg2);
    }

    public void info(String msg, Throwable t) {
        ensureTraceId();
        delegate.info(msg, t);
    }

    public void info(Supplier<String> messageSupplier) {
        if (delegate.isInfoEnabled()) {
            ensureTraceId();
            delegate.info(messageSupplier.get());
        }
    }

    public void info(Supplier<String> messageSupplier, Throwable t) {
        if (delegate.isInfoEnabled()) {
            ensureTraceId();
            delegate.info(messageSupplier.get(), t);
        }
    }

    public void info(String format, Supplier<?>... lazyArgs) {
        if (delegate.isInfoEnabled()) {
            ensureTraceId();
            delegate.info(format, extractSuppliers(lazyArgs));
        }
    }

    public void info(Supplier<String> messageSupplier, Supplier<?>... lazyArgs) {
        if (delegate.isInfoEnabled()) {
            ensureTraceId();
            delegate.info(messageSupplier.get(), extractSuppliers(lazyArgs));
        }
    }

    public void info(String format, Object... mixedArgs) {
        if (!delegate.isInfoEnabled()) return;

        ensureTraceId();

        if (hasSuppliers(mixedArgs)) {
            delegate.info(format, resolveSuppliers(mixedArgs));
        } else {
            delegate.info(format, mixedArgs);
        }
    }

    public void info(Supplier<String> messageSupplier, Object... mixedArgs) {
        if (!delegate.isInfoEnabled()) return;

        ensureTraceId();

        if (hasSuppliers(mixedArgs)) {
            delegate.info(messageSupplier.get(), resolveSuppliers(mixedArgs));
        } else {
            delegate.info(messageSupplier.get(), mixedArgs);
        }
    }

    // WARN LEVEL
    public void warn(String message) {
        ensureTraceId();
        delegate.warn(message);
    }

    public void warn(String format, Object arg) {
        ensureTraceId();
        delegate.warn(format, arg);
    }

    public void warn(String format, Object arg1, Object arg2) {
        ensureTraceId();
        delegate.warn(format, arg1, arg2);
    }

    public void warn(String msg, Throwable t) {
        ensureTraceId();
        delegate.warn(msg, t);
    }

    public void warn(Supplier<String> messageSupplier) {
        if (delegate.isWarnEnabled()) {
            ensureTraceId();
            delegate.warn(messageSupplier.get());
        }
    }

    public void warn(Supplier<String> messageSupplier, Throwable t) {
        if (delegate.isWarnEnabled()) {
            ensureTraceId();
            delegate.warn(messageSupplier.get(), t);
        }
    }

    public void warn(String format, Supplier<?>... lazyArgs) {
        if (delegate.isWarnEnabled()) {
            ensureTraceId();
            delegate.warn(format, extractSuppliers(lazyArgs));
        }
    }

    public void warn(Supplier<String> messageSupplier, Supplier<?>... lazyArgs) {
        if (delegate.isWarnEnabled()) {
            ensureTraceId();
            delegate.warn(messageSupplier.get(), extractSuppliers(lazyArgs));
        }
    }

    public void warn(String format, Object... mixedArgs) {
        if (!delegate.isWarnEnabled()) return;

        ensureTraceId();

        if (hasSuppliers(mixedArgs)) {
            delegate.warn(format, resolveSuppliers(mixedArgs));
        } else {
            delegate.warn(format, mixedArgs);
        }
    }

    public void warn(Supplier<String> messageSupplier, Object... mixedArgs) {
        if (!delegate.isWarnEnabled()) return;

        ensureTraceId();

        if (hasSuppliers(mixedArgs)) {
            delegate.warn(messageSupplier.get(), resolveSuppliers(mixedArgs));
        } else {
            delegate.warn(messageSupplier.get(), mixedArgs);
        }
    }

    // ERROR LEVEL
    public void error(String message) {
        ensureTraceId();
        delegate.error(message);
    }

    public void error(String format, Object arg) {
        ensureTraceId();
        delegate.error(format, arg);
    }

    public void error(String format, Object arg1, Object arg2) {
        ensureTraceId();
        delegate.error(format, arg1, arg2);
    }

    public void error(String msg, Throwable t) {
        ensureTraceId();
        delegate.error(msg, t);
    }

    public void error(Supplier<String> messageSupplier) {
        if (delegate.isErrorEnabled()) {
            ensureTraceId();
            delegate.error(messageSupplier.get());
        }
    }

    public void error(Supplier<String> messageSupplier, Throwable t) {
        if (delegate.isErrorEnabled()) {
            ensureTraceId();
            delegate.error(messageSupplier.get(), t);
        }
    }

    public void error(String format, Supplier<?>... lazyArgs) {
        if (delegate.isErrorEnabled()) {
            ensureTraceId();
            delegate.error(format, extractSuppliers(lazyArgs));
        }
    }

    public void error(Supplier<String> messageSupplier, Supplier<?>... lazyArgs) {
        if (delegate.isErrorEnabled()) {
            ensureTraceId();
            delegate.error(messageSupplier.get(), extractSuppliers(lazyArgs));
        }
    }

    public void error(String format, Object... mixedArgs) {
        if (!delegate.isErrorEnabled()) return;

        ensureTraceId();

        if (hasSuppliers(mixedArgs)) {
            delegate.error(format, resolveSuppliers(mixedArgs));
        } else {
            delegate.error(format, mixedArgs);
        }
    }

    public void error(Supplier<String> messageSupplier, Object... mixedArgs) {
        if (!delegate.isErrorEnabled()) return;

        ensureTraceId();

        if (hasSuppliers(mixedArgs)) {
            delegate.error(messageSupplier.get(), resolveSuppliers(mixedArgs));
        } else {
            delegate.error(messageSupplier.get(), mixedArgs);
        }
    }

    // TRACE-ID
    private void ensureTraceId() {
        if (MDC.get(MdcUtils.TRACE_ID) == null) {
            MdcUtils.generateAndSetTraceId();
        }
    }

    // SUPPORT
    private Object[] extractSuppliers(Supplier<?>... suppliers) {
        Object[] extractedArgs = new Object[suppliers.length];
        for (int i = 0; i < suppliers.length; i++) {
            extractedArgs[i] = (suppliers[i]).get();
        }
        return extractedArgs;
    }

    private Object[] resolveSuppliers(Object[] mixedArgs) {
        Object[] resolved = new Object[mixedArgs.length];
        for (int i = 0; i < mixedArgs.length; i++) {
            if (mixedArgs[i] instanceof Supplier) {
                resolved[i] = ((Supplier<?>) mixedArgs[i]).get();
            } else {
                resolved[i] = mixedArgs[i];
            }
        }
        return resolved;
    }

    private boolean hasSuppliers(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Supplier) {
                return true;
            }
        }
        return false;
    }
}