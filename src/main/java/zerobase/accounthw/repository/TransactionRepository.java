package zerobase.accounthw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.accounthw.domain.Transaction;

import java.util.Optional;

@Repository
public interface TransactionRepository  extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionId(String transactionId);
}
