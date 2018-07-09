package com.gitrnd.qaconsumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitrnd.qaconsumer.domain.User;
import com.gitrnd.qaconsumer.repository.UserRepository;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Transactional(readOnly = true)
	public User retrieveUserById(String uid) {
		return userRepository.retrieveUserById(uid);
	}

	@Transactional(readOnly = true)
	public User retrieveUserByIdx(int idx) {
		return userRepository.retrieveUserByIdx(idx);
	}

	@Transactional
	public void createUser(User user) {
		userRepository.createUser(user);
	}

	@Transactional(readOnly = true)
	public User checkUserById(User user) {
		return userRepository.checkUserById(user);
	}

	@Transactional(readOnly = true)
	public User checkUserByEmail(User user) {
		return userRepository.checkUserByEmail(user);
	}

}
