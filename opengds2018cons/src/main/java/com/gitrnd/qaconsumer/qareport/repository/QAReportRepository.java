package com.gitrnd.qaconsumer.qareport.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitrnd.qaconsumer.qareport.domain.QAReport;
import com.gitrnd.qaconsumer.qareport.mapper.QAReportMapper;

@Repository
public class QAReportRepository {

	@Autowired
	private QAReportMapper mapper;

	public Integer insertQAReport(QAReport report) {
		return mapper.insertQAReport(report);
	}

	/**
	 * @param pIdx
	 * @return
	 */
	public QAReport retrieveQAReportByPId(int pIdx) {
		return null;
	}
}
