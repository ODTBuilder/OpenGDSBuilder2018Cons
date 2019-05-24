package com.gitrnd.qaconsumer.qaprogress.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gitrnd.qaconsumer.qaprogress.domain.QAProgress;

/**
 * QAProgressMapper.xml 접근 클래스.
 * 
 * @author DY.Oh
 *
 */
@Mapper
public interface QAProgressMapper {

	/**
	 * tb_progress DB 테이블에 pIdx에 {@link QAProgress} 삽입.
	 * 
	 * @param progress {@link QAProgress}
	 * @return pIdx
	 * 
	 * @author DY.Oh
	 */
	public Integer insertQARequest(QAProgress progress);

	/**
	 * tb_progress DB 테이블 수정.
	 * 
	 * @param progress {@link QAProgress}
	 * 
	 * @author DY.Oh
	 */
	public void updateQAState(QAProgress progress);

	/**
	 * tb_progress DB 테이블 수정.
	 * 
	 * @param progress {@link QAProgress}
	 * 
	 * @author DY.Oh
	 */
	public void updateQAResponse(QAProgress progress);

	/**
	 * tb_progress DB 테이블 조회.
	 * 
	 * @return List<HashMap<String, Object>>
	 * 
	 * @author DY.Oh
	 */
	public List<HashMap<String, Object>> selectQAProgressList();

	/**
	 * tb_progress DB 테이블 조회.
	 * 
	 * @param progress
	 * @return {@link QAProgress}
	 * 
	 * @author DY.Oh
	 */
	public QAProgress selectQAStartTime();

	/**
	 * tb_progress DB 테이에서 pIdx에 해당하는 {@link QAProgress} 조회.
	 * 
	 * @param pIdx tb_progress index
	 * @return pIdx에 해당하는 {@link QAProgress}
	 * 
	 * @author DY.Oh
	 */
	public QAProgress retrieveQAProgressById(int pIdx);

}
