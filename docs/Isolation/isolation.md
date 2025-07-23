# Isolation

Isolation defines how and when changes made by one transaction become visible to other concurrent transactions.

Spring doesn’t enforce isolation levels — it delegates that responsibility to the underlying database via JDBC.

The database must support the requested isolation level; otherwise, the closest supported level is applied (or an error may occur, depending on the driver).

The database (not Spring) enforces the rules — managing row locks, creating MVCC snapshots, or blocking other writes.