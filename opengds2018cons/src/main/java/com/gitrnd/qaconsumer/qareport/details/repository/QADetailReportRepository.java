package com.gitrnd.qaconsumer.qareport.details.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitrnd.qaconsumer.qareport.details.domain.QADetailReport;
import com.gitrnd.qaconsumer.qareport.details.mapper.QADetailReportMapper;

@Repository
public class QADetailReportRepository {

	@Autowired
	private QADetailReportMapper mapper;

	/**
	 * @param detail
	 * @return
	 */
	public Integer insertQADetailReport(QADetailReport detail) {
		mapper.insertQADetailReport(detail);
		return detail.getRd_idx();
	}
}
