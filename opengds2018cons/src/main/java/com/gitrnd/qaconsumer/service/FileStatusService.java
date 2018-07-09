package com.gitrnd.qaconsumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitrnd.qaconsumer.domain.FileStatus;
import com.gitrnd.qaconsumer.repository.FileStatusRepository;

@Service
public class FileStatusService {

	@Autowired
	private FileStatusRepository fileStatusRepository;

	@Transactional(readOnly = true)
	public FileStatus retrieveFileStatusById(int fid) {
		return fileStatusRepository.retrieveFileStatusById(fid);
	}

	@Transactional
	public void createFileStatus(FileStatus fileStatus) {
		fileStatusRepository.createFileStatus(fileStatus);
	}

	public void updateFileStatusComment(FileStatus fileStatus) {
		fileStatusRepository.updateFileStatusComment(fileStatus);
	}
}
