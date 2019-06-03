package com.gitrnd.qaconsumer.user.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.gitrnd.qaconsumer.user.domain.User;

/**
 * User 테이블 Mapper 인터페이스
 * @author SG.LEE
 *
 */
@Mapper
public interface UserMapper {
	/**
	 * 사용자 조회
	 * @author SG.LEE
	 * @param uid 사용자 id
	 * @return User 클래스
	 */
	User retrieveUserById(String uid);

	/**
	 * 사용자 조회
	 * @author SG.LEE
	 * @param idx 사용자 테이블 pk
	 * @return User 클래스
	 */
	User retrieveUserByIdx(int idx);

	/**
	 * 사용자 생성
	 * @author SG.LEE
	 * @param user User 클래스
	 */
	void createUser(User user);

	/**
	 * 아이디 중복여부 체크
	 * @author SG.LEE
	 * @param user User 클래스 
	 * @return User 클래스
	 */
	User checkUserById(User user);

	/**
	 * 이메일 체크
	 * @author SG.LEE
	 * @param user User 클래스 
	 * @return User 클래스
	 */
	User checkUserByEmail(User user);
}
