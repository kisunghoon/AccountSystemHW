package zerobase.accounthw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zerobase.accounthw.domain.AccountUser;

public interface AccountUserRepository extends JpaRepository<AccountUser, Long> {
}
