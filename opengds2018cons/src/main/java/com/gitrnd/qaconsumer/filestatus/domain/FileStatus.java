package com.gitrnd.qaconsumer.filestatus.domain;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FileStatus 객체.
 * 
 * @author IJ.S
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileStatus {
	/**
	 * tb_file index
	 */
	private int fid;
	/**
	 * file 경로
	 */
	private String location;
	/**
	 * file name
	 */
	private String fname;
	/**
	 * file upload 시간
	 */
	private Timestamp ctime;
	/**
	 * upload 상태
	 */
	private int status;
	/**
	 * tb_user index
	 */
	private int uidx;
}
