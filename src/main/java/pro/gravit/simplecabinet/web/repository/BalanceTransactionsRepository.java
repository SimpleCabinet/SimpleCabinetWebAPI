package pro.gravit.simplecabinet.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.gravit.simplecabinet.web.model.BalanceTransaction;
import pro.gravit.simplecabinet.web.model.UserBalance;

public interface BalanceTransactionsRepository extends JpaRepository<BalanceTransaction, Long> {
    @Query("select t from BalanceTransaction t where t.from = :balance or t.to = :balance order by id")
    Page<BalanceTransaction> findAllByBalance(@Param("balance") UserBalance balance, Pageable pageable);
}
