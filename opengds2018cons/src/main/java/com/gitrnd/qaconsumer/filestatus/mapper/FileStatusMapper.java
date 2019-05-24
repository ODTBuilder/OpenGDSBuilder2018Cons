package com.gitrnd.qaconsumer.filestatus.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.gitrnd.qaconsumer.filestatus.domain.FileStatus;

/**
 * FileStatusMapper.xml 접근 클래스.
 * 
 * @author DY.Oh
 *
 */
@Mapper
public interface FileStatusMapper {

	/**
	 * fid에 해당하는 {@link FileStatus}를 DB에서 조회.
	 * 
	 * @param fid tb_file index
	 * @return {@link FileStatus}
	 * 
	 * @author IJ.S
	 */
	FileStatus retrieveFileStatusById(int fid);

	/**
	 * DB에 {@link FileStatus}를 삽입.
	 * 
	 * @param fileStatus {@link FileStatus}
	 * 
	 * @author IJ.S
	 */
	void createFileStatus(FileStatus fileStatus);

	/**
	 * DB에 저장되어있는 tb_file 테이블 수정.
	 * 
	 * @param fileStatus {@link FileStatus}
	 * 
	 * @author IJ.S
	 */
	void updateFileStatusComment(FileStatus fileStatus);
}
