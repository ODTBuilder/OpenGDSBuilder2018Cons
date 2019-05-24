package com.gitrnd.qaconsumer.qaprogress.repository;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitrnd.qaconsumer.qaprogress.domain.QAProgress;
import com.gitrnd.qaconsumer.qaprogress.mapper.QAProgressMapper;

/**
 * tb_progress Repository 클래스.
 * 
 * @author DY.Oh
 *
 */
@Repository
public class QAProgressRepository {

	@Autowired
	private QAProgressMapper mapper;

	/**
	 * tb_progress DB 테이블에 pIdx에 {@link QAProgress} 삽입.
	 * 
	 * @param progress {@link QAProgress}
	 * @return pIdx
	 * 
	 * @author DY.Oh
	 */
	public Integer insertQARequest(QAProgress progress) {
		return mapper.insertQARequest(progress);
	}

	/**
	 * tb_progress DB 테이블 수정.
	 * 
	 * @param progress {@link QAProgress}
	 * 
	 * @author DY.Oh
	 */
	public void updateQAState(QAProgress progress) {
		mapper.updateQAState(progress);
	}

	/**
	 * tb_progress DB 테이블 수정.
	 * 
	 * @param progress {@link QAProgress}
	 * 
	 * @author DY.Oh
	 */
	public void updateQAResponse(QAProgress progress) {
		mapper.updateQAResponse(progress);
	}

	/**
	 * tb_progress DB 테이블 조회.
	 * 
	 * @return List<HashMap<String, Object>>
	 * 
	 * @author DY.Oh
	 */
	public List<HashMap<String, Object>> selectQAProgressList() {
		return mapper.selectQAProgressList();
	}

	/**
	 * tb_progress DB 테이블 조회.
	 * 
	 * @return {@link QAProgress}
	 * 
	 * @author DY.Oh
	 */
	public QAProgress selectQAStartTime() {
		return mapper.selectQAStartTime();
	}

	/**
	 * tb_progress DB 테이에서 pIdx에 해당하는 {@link QAProgress} 조회.
	 * 
	 * @param pIdx tb_progress index
	 * @return pIdx에 해당하는 {@link QAProgress}
	 * 
	 * @author DY.Oh
	 */
	public QAProgress retrieveQAProgressById(int pIdx) {
		return mapper.retrieveQAProgressById(pIdx);
	}
}
