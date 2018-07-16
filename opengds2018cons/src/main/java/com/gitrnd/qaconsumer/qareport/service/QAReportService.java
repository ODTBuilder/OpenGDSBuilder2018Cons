package com.gitrnd.qaconsumer.qareport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gitrnd.qaconsumer.qareport.domain.QAReport;
import com.gitrnd.qaconsumer.qareport.repository.QAReportRepository;

@Service
public class QAReportService {

	@Autowired
	private QAReportRepository reportService;

	public Integer insertQAReport(QAReport report) {
		reportService.insertQAReport(report);
		return report.getR_idx();
	}
}
