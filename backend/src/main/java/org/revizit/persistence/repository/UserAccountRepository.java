package org.revizit.persistence.repository;

import org.revizit.persistence.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {

    @Query("select u from UserAccount u where u.username = :username and u.inactive = false")
    Optional<UserAccount> findByUsername(String username);

    List<UserAccount> findByInactive(boolean inactive);


    @Query("""
        select u
        from   UserAccount u
        where  u.inactive = false
        and    u.username in :usernames""")
    List<UserAccount> findActiveByUsername(Collection<String> usernames);

}
