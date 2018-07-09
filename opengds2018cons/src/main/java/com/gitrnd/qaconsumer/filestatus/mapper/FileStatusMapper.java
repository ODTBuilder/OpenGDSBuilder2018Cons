package com.gitrnd.qaconsumer.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.gitrnd.qaconsumer.domain.FileStatus;

@Mapper
public interface FileStatusMapper {
	FileStatus retrieveFileStatusById(int fid);

	void createFileStatus(FileStatus fileStatus);

	void updateFileStatusComment(FileStatus fileStatus);
}
