# Spring boot Transaction

A `transaction` is a sequence of multiple statements that must either `all succeed or all fail`. If one step fails, everything gets `rolled back`.

The Transaction is used to keep our data `consistent` in the database.

## ACID Properties

* `A — Atomicity:` All or nothing.
    If any part fails, the whole transaction is rolled back.
* `C — Consistency:` Rules Respected
    Your database should always stay valid — it should move from one valid state to another.
* `I — Isolation:` Do it like you are alone
    Multiple transactions shouldn’t interfere with each other.
* `D — Durability:` Written in Stone
    Once the transaction is committed, the changes are permanent, even if the system crashes.

Spring’s `@Transactional` is responsible for transaction boundaries

    * Starting a transaction before your method runs
    * Committing it if the method completes successfully
    * Rolling back if a runtime exception is thrown

## How does Spring work internally?

Spring uses `AOP (Aspect-Oriented Programming)` to wrap your service method in extra behavior — in this case, transaction management.

When your service is marked with `@Transactional`, Spring does the following:

    Creates a proxy around your class or method
    When a method is called:
    — It begins a transaction
    — Runs your business logic
    — Commits or rolls back based on success or failure

    Spring uses:
    — `JDK Dynamic Proxies` if your class implements an interface `CGLIB proxies` if it doesn’t

This allows Spring to manage transactions without modifying your core logic.

Now,

    💡 Clear and clean business logic
    🔄 Reusable, declarative transaction control
    🛡 Safe rollback on exceptions
    📦 Less boilerplate, more focus

## Where Can You Add `@Transactional`?

You can use it in two ways:

### Method Level

Add `@Transactional` on top of a `public` method. Only public methods can be proxied by Spring. If you add it to `private or protected` methods, it `won’t work`, because Spring can’t create proxies for them.

### Class Level

Add `@Transactional` At the class level, Spring will apply it to all public methods within that class.

By default, Spring rolls back only for unchecked exceptions (subclasses of RuntimeException or Error).

If your method throws a checked exception, the transaction will not be rolled back — unless you explicitly configure it:
```java
@Transactional(rollbackFor = Exception.class)
```
## Propogations

* Required (default): My method needs a transaction, either open one for me or use an existing one → getConnection(). setAutocommit(false). commit().

* Supports: I don’t really care if a transaction is open or not, i can work either way → nothing to do with JDBC

* Mandatory: I’m not going to open up a transaction myself, but I’m going to cry if no one else opened one up → nothing to do with JDBC

* Require_new: I want my completely own transaction → getConnection(). setAutocommit(false). commit().

* Not_Supported: I really don’t like transactions, I will even try and suspend a current, running transaction → nothing to do with JDBC

* Never: I’m going to cry if someone else started up a transaction → nothing to do with JDBC

* Nested: It sounds so complicated, but we are just talking savepoints! → connection.setSavepoint()

## Isolation


## Common Pitfall

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

