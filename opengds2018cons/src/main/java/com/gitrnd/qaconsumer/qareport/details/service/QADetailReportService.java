package com.gitrnd.qaconsumer.qareport.details.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gitrnd.qaconsumer.qareport.details.domain.QADetailReport;
import com.gitrnd.qaconsumer.qareport.details.repository.QADetailReportRepository;

@Service
public class QADetailReportService {

	@Autowired
	private QADetailReportRepository detailRepository;

	public Integer insertQADetailReport(QADetailReport detail) {
		return detailRepository.insertQADetailReport(detail);
	}
}
