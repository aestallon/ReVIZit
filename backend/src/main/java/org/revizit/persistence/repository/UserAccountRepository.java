package org.revizit.persistence.repository;

import org.revizit.persistence.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {
    Optional<UserAccount> findByUsername(String username);

    List<UserAccount> findByInactive(boolean inactive);

}
