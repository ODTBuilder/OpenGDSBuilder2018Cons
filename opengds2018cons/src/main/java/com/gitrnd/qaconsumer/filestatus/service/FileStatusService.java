package com.gitrnd.qaconsumer.filestatus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitrnd.qaconsumer.filestatus.domain.FileStatus;
import com.gitrnd.qaconsumer.filestatus.repository.FileStatusRepository;

/**
 * tb_file Service 클래스.
 * 
 * @author IJ.S
 *
 */
@Service
public class FileStatusService {

	@Autowired
	private FileStatusRepository fileStatusRepository;

	/**
	 * fid에 해당하는 {@link FileStatus}를 DB에서 조회.
	 * 
	 * @param fid tb_file index
	 * @return {@link FileStatus}
	 * 
	 * @author IJ.S
	 */
	@Transactional(readOnly = true)
	public FileStatus retrieveFileStatusById(int fid) {
		return fileStatusRepository.retrieveFileStatusById(fid);
	}

	/**
	 * DB에 {@link FileStatus}를 삽입.
	 * 
	 * @param fileStatus {@link FileStatus}
	 * 
	 * @author IJ.S
	 */
	@Transactional
	public void createFileStatus(FileStatus fileStatus) {
		fileStatusRepository.createFileStatus(fileStatus);
	}

	/**
	 * DB에 저장되어있는 tb_file 테이블 수정.
	 * 
	 * @param fileStatus {@link FileStatus}
	 * 
	 * @author IJ.S
	 */
	public void updateFileStatusComment(FileStatus fileStatus) {
		fileStatusRepository.updateFileStatusComment(fileStatus);
	}
}
