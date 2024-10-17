# Spring boot Transaction

## Propogations

* Required (default): My method needs a transaction, either open one for me or use an existing one → getConnection(). setAutocommit(false). commit().

* Supports: I don’t really care if a transaction is open or not, i can work either way → nothing to do with JDBC

* Mandatory: I’m not going to open up a transaction myself, but I’m going to cry if no one else opened one up → nothing to do with JDBC

* Require_new: I want my completely own transaction → getConnection(). setAutocommit(false). commit().

* Not_Supported: I really don’t like transactions, I will even try and suspend a current, running transaction → nothing to do with JDBC

* Never: I’m going to cry if someone else started up a transaction → nothing to do with JDBC

* Nested: It sounds so complicated, but we are just talking savepoints! → connection.setSavepoint()

## Isolation

