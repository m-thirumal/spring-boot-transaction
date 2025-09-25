# Handling Multiple Threads in a Transaction in Spring Boot

## Why is Multi-Threading in Transactions Tricky?

Transactions are managed using the `@Transactional` annotation, which binds a transaction to the current thread via a `PlatformTransactionManager`. This thread-local binding means that a transaction started in one thread is not automatically accessible to other threads, leading to challenges such as:

* `Transaction Propagation:` Child threads may not inherit the parent thread’s transaction, causing unexpected behavior.
* `Data Consistency:` Concurrent modifications by multiple threads can lead to race conditions or inconsistent data.
* `Connection Management:` Database connections are typically tied to a transaction, and sharing them across threads can cause errors.
* `Performance Overhead:` Improper handling of multi-threaded transactions can degrade performance or cause deadlocks.

## Transaction Behavior in Multi-Threaded Environments

```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Service
public class TransactionService {
    @Transactional
    public void processWithThreads() {
        // Save data in parent thread
        saveData("Parent Data");
        // Spawn a new thread
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            // This runs in a new thread, outside the transaction
            saveData("Child Data"); // May throw an exception or use a new connection
        });
        executor.shutdown();
    }
    private void saveData(String data) {
        // Simulate database save
        System.out.println("Saving: " + data);
    }
}
```

### Problem:

The `saveData()` call in the child thread doesn’t participate in the parent’s transaction, potentially leading to:

* A new database connection being opened (outside the transaction).
* Data inconsistencies if the parent transaction rolls back.
* Exceptions if the child thread expects an active transaction.

## Solutions:

### Solution 1: Propagate the Transaction Context to Child Threads 

You can propagate the parent thread’s transaction context to child threads using Spring’s `TransactionSynchronization` or a custom `TransactionTemplate`. However, since database connections are typically not thread-safe, you must ensure proper synchronization or use a `transaction-aware` executor.

Solution: `Use TransactionTemplate`

The `TransactionTemplate` allows programmatic transaction management, enabling you to execute code in child threads within the same transaction.

```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Service
public class TransactionService {
    private final TransactionTemplate transactionTemplate;
    public TransactionService(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
    @Transactional
    public void processWithThreads() {
        // Save data in parent thread
        saveData("Parent Data");
        // Spawn a new thread with transaction context
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            transactionTemplate.execute(status -> {
                saveData("Child Data"); // Runs within the parent transaction
                return null;
            });
        });
        executor.shutdown();
    }
    private void saveData(String data) {
        // Simulate database save
        System.out.println("Saving: " + data);
    }
}
```

* TransactionTemplate ensures the child thread’s operations are executed within the parent’s transaction.
* If the parent transaction rolls back, all changes (including those in the child thread) are undone.

### Solution 2: Transaction-Aware Task Executor 

Spring provides `AsyncTaskExecutor` implementations that can propagate transaction contexts to asynchronous tasks. By wrapping tasks in a transaction-aware executor, you can ensure child threads inherit the parent’s transaction.

Config:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Configuration
@EnableAsync
@EnableTransactionManagement
public class AsyncConfig {
    @Bean
    public SimpleAsyncTaskExecutor taskExecutor(TransactionInterceptor transactionInterceptor) {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setTaskDecorator(runnable -> {
            // Propagate transaction context
            return transactionInterceptor.invoke(null, runnable);
        });
        return executor;
    }
}
```

`@Async` with Transaction Propagation

```java
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    @Transactional
    public void processWithThreads() {
        // Save data in parent thread
        saveData("Parent Data");
        // Call async method
        processAsync();
    }
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW) // Optional: New transaction
    public void processAsync() {
        saveData("Child Data"); // Runs in a separate thread
    }
    private void saveData(String data) {
        // Simulate database save
        System.out.println("Saving: " + data);
    }
}
```

* The custom SimpleAsyncTaskExecutor propagates the transaction context to async methods.
* Use @Transactional(propagation = Propagation.REQUIRES_NEW) if the child thread needs a separate transaction.
* Ensures thread safety and proper transaction boundaries. ✅

Limitations:

* Requires careful configuration of the executor.
* May increase complexity for large-scale async operations.

### Solution 3: Separate Transactions for Each Thread

If child threads perform independent operations, you can assign each thread its own transaction using @Transactional with appropriate propagation settings (e.g., Propagation.REQUIRES_NEW).

```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TransactionService {
    @Transactional
    public void processWithThreads() {
        // Save data in parent thread
        saveData("Parent Data");
        // Spawn a new thread with its own transaction
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(this::processInNewTransaction);
        executor.shutdown();
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processInNewTransaction() {
        saveData("Child Data"); // Runs in a new transaction
    }
    private void saveData(String data) {
        // Simulate database save
        System.out.println("Saving: " + data);
    }
}
```

How It Works:

* The child thread starts a new transaction with Propagation.REQUIRES_NEW, independent of the parent’s transaction.
* Changes in the child thread are committed or rolled back separately. ✅

Limitations:

* Increases database connection usage, as each transaction requires its own connection.
* May lead to data inconsistencies if not coordinated properly.