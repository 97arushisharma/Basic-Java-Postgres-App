package com.postgretry.postdb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
@Repository
public interface UserRepository extends JpaRepository<UserInfo, Long> {
	
	List<UserInfo> findByName(String Name);
	List<UserInfo> findByUid(Long uid);
	
	
	
		
	
	
}
