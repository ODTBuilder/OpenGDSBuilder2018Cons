/**
 * 
 */
package com.gitrnd.qaconsumer.qacategory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitrnd.qaconsumer.qacategory.domain.QACategory;
import com.gitrnd.qaconsumer.qacategory.repository.QACategoryRepository;

/**
 * QACategoryService.
 * 
 * @author IJ.S
 *
 */
@Service
@Transactional
public class QACategoryService {

	@Autowired
	private QACategoryRepository qaCategoryRepository;

	/**
	 * idx에 해당하는 {@link QACategory}를 DB에서 조회하여 반환.
	 * 
	 * @param idx idx
	 * @return DB에서 조회된 {@link QACategory}
	 * 
	 * @author IJ.S
	 */
	@Transactional(readOnly = true)
	public QACategory retrieveQACategoryByIdx(int idx) {
		return qaCategoryRepository.retrieveQACategoryByIdx(idx);
	}

}
