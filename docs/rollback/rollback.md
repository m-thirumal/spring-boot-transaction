# Rollback

* By default:
  * `Unchecked` (RuntimeException, Error) → `Rollback`
  * `Checked` Exception → `No rollback` (unless configured)
* Custom rollback → `@Transactional(rollbackFor = Exception.class)`
* No rollback → `@Transactional(noRollbackFor = SomeException.class)`


