package com.gitrnd.qaconsumer.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.gitrnd.qaconsumer.domain.User;

@Mapper
public interface UserMapper {
	User retrieveUserById(String uid);

	User retrieveUserByIdx(int idx);

	void createUser(User user);

	User checkUserById(User user);

	User checkUserByEmail(User user);
}
