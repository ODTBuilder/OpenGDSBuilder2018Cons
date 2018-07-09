package com.gitrnd.qaconsumer.user.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.gitrnd.qaconsumer.user.domain.User;

@Mapper
public interface UserMapper {
	User retrieveUserById(String uid);

	User retrieveUserByIdx(int idx);

	void createUser(User user);

	User checkUserById(User user);

	User checkUserByEmail(User user);
}
