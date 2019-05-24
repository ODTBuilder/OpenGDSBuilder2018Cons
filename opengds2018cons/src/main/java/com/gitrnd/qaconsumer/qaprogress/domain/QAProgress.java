/**
 * 
 */
package com.gitrnd.qaconsumer.qaprogress.domain;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QAProgress 객체.
 * 
 * @author DY.Oh
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QAProgress {

	/**
	 * tb_progress index(PK)
	 */
	Integer pIdx;
	/**
	 * tb_user index
	 */
	Integer uIdx;
	/**
	 * tb_file index
	 */
	Integer fIdx;
	/**
	 * qa 진행 상태
	 */
	Integer qaState;
	/**
	 * qa 시작시간
	 */
	Timestamp start_time;
	/**
	 * qa 종료시간
	 */
	Timestamp endTime;
	/**
	 * qa file명
	 */
	String originName;
	/**
	 * tb_qa_category index
	 */
	Integer qaCategory;
	/**
	 * qa type
	 */
	String qaType;
	/**
	 * file type
	 */
	String fileType;
	/**
	 * err file 저장 경로
	 */
	String errdirectory;
	/**
	 * err file name
	 */
	String errFileName;
	/**
	 * tb_preset index
	 */
	Integer prid;
	/**
	 * qa comment
	 */
	String comment;

}
