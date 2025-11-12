package com.auth.service.auth_service.repository;

import com.auth.service.auth_service.model.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserAppRespository extends JpaRepository<UserApp,Long> {

    public Optional<UserApp> findEntityUserAppByUsername(String username);
}
