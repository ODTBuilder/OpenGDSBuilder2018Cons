package com.gitrnd.qaconsumer.filestatus.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitrnd.qaconsumer.filestatus.domain.FileStatus;
import com.gitrnd.qaconsumer.filestatus.mapper.FileStatusMapper;

/**
 * tb_file Repository 클래스.
 * 
 * @author DY.Oh
 *
 */
@Repository
public class FileStatusRepository {

	@Autowired
	private FileStatusMapper fileStatusMapper;

	/**
	 * fid에 해당하는 {@link FileStatus}를 DB에서 조회.
	 * 
	 * @param fid tb_file index
	 * @return {@link FileStatus}
	 * 
	 * @author IJ.S
	 */
	public FileStatus retrieveFileStatusById(int fid) {
		return fileStatusMapper.retrieveFileStatusById(fid);
	}

	/**
	 * DB에 {@link FileStatus}를 삽입.
	 * 
	 * @param fileStatus {@link FileStatus}
	 * 
	 * @author IJ.S
	 */
	public void createFileStatus(FileStatus fileStatus) {
		fileStatusMapper.createFileStatus(fileStatus);
	}

	/**
	 * DB에 저장되어있는 tb_file 테이블 수정.
	 * 
	 * @param fileStatus {@link FileStatus}
	 * 
	 * @author IJ.S
	 */
	public void updateFileStatusComment(FileStatus fileStatus) {
		fileStatusMapper.updateFileStatusComment(fileStatus);
	}
}
