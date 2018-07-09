package com.gitrnd.qaconsumer.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitrnd.qaconsumer.domain.User;
import com.gitrnd.qaconsumer.mapper.UserMapper;

@Repository
public class UserRepository {

	@Autowired
	private UserMapper userMapper;

	public User retrieveUserById(String uid) {
		return userMapper.retrieveUserById(uid);
	}

	public User retrieveUserByIdx(int idx) {
		return userMapper.retrieveUserByIdx(idx);
	}

	public void createUser(User user) {
		userMapper.createUser(user);
	}

	public User checkUserById(User user) {
		return userMapper.checkUserById(user);
	}

	public User checkUserByEmail(User user) {
		return userMapper.checkUserByEmail(user);
	}
}
