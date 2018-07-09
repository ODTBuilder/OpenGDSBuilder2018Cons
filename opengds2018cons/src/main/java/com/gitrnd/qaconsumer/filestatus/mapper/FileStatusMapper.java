package com.gitrnd.qaconsumer.filestatus.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.gitrnd.qaconsumer.filestatus.domain.FileStatus;

@Mapper
public interface FileStatusMapper {
	FileStatus retrieveFileStatusById(int fid);

	void createFileStatus(FileStatus fileStatus);

	void updateFileStatusComment(FileStatus fileStatus);
}
