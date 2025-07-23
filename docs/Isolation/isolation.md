# Isolation

Isolation defines how and when changes made by one transaction become visible to other concurrent transactions.

Spring doesn’t enforce isolation levels — it delegates that responsibility to the underlying database via JDBC.

The database must support the requested isolation level; otherwise, the closest supported level is applied (or an error may occur, depending on the driver).

The database (not Spring) enforces the rules — managing row locks, creating MVCC snapshots, or blocking other writes.

| Isolation Level  | Dirty Read  | Non-Repeatable Read   | Phantom Read  |
| ---------------- | ----------- | --------------------- | ------------- |
| Read Uncommitted | ✅ Yes      | ✅ Yes               | ✅ Yes        |
| Read Committed   | ❌ No       | ✅ Yes               | ✅ Yes        |
| Repeatable Read  | ❌ No       | ❌ No                | ✅ Yes        |
| Serializable     | ❌ No       | ❌ No                | ❌ No         |


## Dirty Read — Reading Uncommitted Data

You read something that the other transaction hasn’t committed yet. If that transaction rolls back, your read was dirty (never real)

## Non-Repeatable Read — Data Changes Mid-Transaction

You read the same row twice inside your transaction and get different results because another transaction committed a change in between.

## Phantom Read — New Rows Appear

New rows appear when you run the same query twice

| Level                 | Allows                                         | Prevents                                 | Real-World Behavior                                                      | Use Case                                     | Spring Default (by DB)                    |
| --------------------- | ---------------------------------------------- | ---------------------------------------- | ------------------------------------------------------------------------ | -------------------------------------------- | ----------------------------------------- |
| **READ\_UNCOMMITTED** | ✅ Dirty Reads<br>✅ Non-repeatable<br>✅ Phantom | ❌ Nothing                                | T2 can see uncommitted/rolled-back data<br>⚠️ Highly unsafe              | Almost never; for diagnostics only           | ⚠️ Rare / not recommended                 |
| **READ\_COMMITTED**   | ✅ Non-repeatable Reads<br>✅ Phantom            | ✅ Dirty Reads                            | T2 sees only committed data<br>May get different results on re-read      | Most OLTP systems<br>Default in Oracle, PG   | ✅ Spring → DB default:<br>Postgres/Oracle |
| **REPEATABLE\_READ**  | ✅ Phantom Reads                                | ✅ Dirty Reads<br>✅ Non-repeatable        | T2 sees same row values consistently<br>But new matching rows may appear | Reporting or financial reads                 | ✅ Spring → DB default:<br>MySQL           |
| **SERIALIZABLE**      | ❌ None (Fully isolated)                        | ✅ Dirty<br>✅ Non-repeatable<br>✅ Phantom | T2 may get blocked or rolled back<br>Feels like single-threaded          | Strict consistency (e.g., banking transfers) | ⚠️ Requires retry logic                   |

##  Behind the Scenes (by DB):

| DB Engine        | Default Isolation | Serializable Type                     |
| ---------------- | ----------------- | ------------------------------------- |
| **PostgreSQL**   | Read Committed    | Serializable Snapshot Isolation (SSI) |
| **MySQL/InnoDB** | Repeatable Read   | Range locks                           |
| **Oracle**       | Read Committed    | Serializable Read Consistency         |
| **SQL Server**   | Read Committed    | Range locks                           |

## Best Practice Recommendations

| Scenario                                           | Recommended Isolation |
| -------------------------------------------------- | --------------------- |
| General transactional systems (OLTP)               | `READ_COMMITTED`      |
| Reporting or read consistency                      | `REPEATABLE_READ`     |
| Critical operations (e.g., money transfer + audit) | `SERIALIZABLE`        |
