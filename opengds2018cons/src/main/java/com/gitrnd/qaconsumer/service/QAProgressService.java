package com.gitrnd.qaconsumer.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.gitrnd.qaconsumer.domain.QAProgress;
import com.gitrnd.qaconsumer.repository.QAProgressRepository;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class QAProgressService {

	@Autowired
	private QAProgressRepository progressRep;

	@Transactional(readOnly = true)
	public QAProgress retrieveQAProgressById(int pIdx) {
		return progressRep.retrieveQAProgressById(pIdx);
	}

	public Integer insertQARequest(QAProgress progress) {
		return progressRep.insertQARequest(progress);
	}

	public void updateQAState(QAProgress progress) {
		progressRep.updateQAState(progress);
	}

	public void updateQAResponse(QAProgress progress) {
		progressRep.updateQAResponse(progress);
	}

	public List<HashMap<String, Object>> selectQAProgressList() {
		return progressRep.selectQAProgressList();
	}

	public QAProgress selectQAStartTime(QAProgress progress) {
		return progressRep.selectQAStartTime();
	}
}
