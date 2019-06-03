package com.gitrnd.qaconsumer.user.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitrnd.qaconsumer.user.domain.User;
import com.gitrnd.qaconsumer.user.mapper.UserMapper;

/**
 * 사용자 Repository 클래스 
 * @author SG.LEE
 */
@Repository
public class UserRepository {

	/**
	 * 사용자 Mapper 인터페이스
	 */
	@Autowired
	private UserMapper userMapper;

	/**
	 * 사용자 조회
	 * @author SG.LEE
	 * @param uid 사용자 id
	 * @return User 클래스
	 */
	public User retrieveUserById(String uid) {
		return userMapper.retrieveUserById(uid);
	}

	/**
	 * 사용자 조회
	 * @author SG.LEE
	 * @param idx 사용자 테이블 pk
	 * @return User 클래스
	 */
	public User retrieveUserByIdx(int idx) {
		return userMapper.retrieveUserByIdx(idx);
	}

	/**
	 * 사용자 생성
	 * @author SG.LEE
	 * @param user User 클래스
	 */
	public void createUser(User user) {
		userMapper.createUser(user);
	}

	/**
	 * 아이디 중복여부 체크
	 * @author SG.LEE
	 * @param user User 클래스 
	 * @return User 클래스
	 */
	public User checkUserById(User user) {
		return userMapper.checkUserById(user);
	}

	/**
	 * 이메일 체크
	 * @author SG.LEE
	 * @param user User 클래스 
	 * @return User 클래스
	 */
	public User checkUserByEmail(User user) {
		return userMapper.checkUserByEmail(user);
	}
}
