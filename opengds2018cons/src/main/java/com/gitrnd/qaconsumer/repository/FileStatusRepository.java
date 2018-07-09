package com.gitrnd.qaconsumer.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitrnd.qaconsumer.domain.FileStatus;
import com.gitrnd.qaconsumer.mapper.FileStatusMapper;

@Repository
public class FileStatusRepository {

	@Autowired
	private FileStatusMapper fileStatusMapper;

	public FileStatus retrieveFileStatusById(int fid) {
		return fileStatusMapper.retrieveFileStatusById(fid);
	}

	public void createFileStatus(FileStatus fileStatus) {
		fileStatusMapper.createFileStatus(fileStatus);
	}

	public void updateFileStatusComment(FileStatus fileStatus) {
		fileStatusMapper.updateFileStatusComment(fileStatus);
	}
}
