# Common Pitfall

1. ⚠️ Placing `@Transactional` on `Private` or `Static` Methods (or constructors)
Spring manages transactions via AOP proxies, which don’t intercept private or static methods.

```java
@Transactional
private void internalMethod() { // ⚠️ NOT TRANSACTIONAL!
 // Spring won't manage transactions here`
}
```

✔ Fix: Always use @Transactional on public methods.

2. ⚠️ Nested Transactions Can Cause Unexpected Rollbacks
By default, @Transactional rolls back the entire transaction if one operation fails.
If you need partial rollbacks, use Propagation.REQUIRES_NEW.
Example:

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void logTransaction(String message) {
 transactionRepository.save(new TransactionLog(message));
}
```

✔ Ensures log entry is saved even if the main transaction fails.

3. ⚠️ Not Configuring Rollback for Specific Exceptions
By default, @Transactional only rolls back unchecked exceptions (RuntimeException), not checked exceptions.
Example (Won’t Rollback):

```java
@Transactional
public void processOrder() throws Exception { // ⚠️ Checked Exception won't trigger rollback
 throw new Exception("Processing failed!");
}
```

✔ Fix: Explicitly specify rollback behavior.

```java
@Transactional(rollbackFor = Exception.class)
public void processOrder() throws Exception {
 throw new Exception("Processing failed!");
}
```

4. ⚠️ Mixing `@Transactional` with Asynchronous `(@Async)` Methods
If you use `@Async`, Spring executes the method in a separate thread, causing `@Transactional to lose its context.
Example:

```java
@Async
@Transactional
public void processData() {
 // ⚠️ Transaction won’t apply correctly here
}
```

✔ Fix: Use `@Transactional` only in synchronous methods.
