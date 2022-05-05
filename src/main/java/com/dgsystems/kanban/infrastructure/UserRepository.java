package com.dgsystems.kanban.infrastructure;

import com.dgsystems.kanban.web.security.UserAccount;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository {
	Optional<UserAccount> findByUsername(String username);

	UserAccount save(UserAccount newUser);
}