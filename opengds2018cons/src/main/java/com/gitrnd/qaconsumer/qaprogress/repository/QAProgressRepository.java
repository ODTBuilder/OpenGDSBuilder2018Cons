package com.gitrnd.qaconsumer.qaprogress.repository;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitrnd.qaconsumer.qaprogress.domain.QAProgress;
import com.gitrnd.qaconsumer.qaprogress.mapper.QAProgressMapper;

@Repository
public class QAProgressRepository {

	@Autowired
	private QAProgressMapper mapper;

	public Integer insertQARequest(QAProgress progress) {
		return mapper.insertQARequest(progress);
	}

	public void updateQAState(QAProgress progress) {
		mapper.updateQAState(progress);
	}

	public void updateQAResponse(QAProgress progress) {
		mapper.updateQAResponse(progress);
	}

	public List<HashMap<String, Object>> selectQAProgressList() {
		return mapper.selectQAProgressList();
	}

	/**
	 * @author DY.Oh
	 * @Date 2018. 3. 9. 오후 2:47:01
	 * @return String
	 * @decription
	 */
	public QAProgress selectQAStartTime() {
		return mapper.selectQAStartTime();
	}

	/**
	 * @author DY.Oh
	 * @Date 2018. 4. 11. 오후 3:32:06
	 * @param fid
	 * @return FileStatus
	 * @decription
	 */
	public QAProgress retrieveQAProgressById(int pIdx) {
		return mapper.retrieveQAProgressById(pIdx);
	}
}
