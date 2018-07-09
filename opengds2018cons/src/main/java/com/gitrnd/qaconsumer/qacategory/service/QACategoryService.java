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
 * @className QACategoryService.java
 * @description
 * @author DY.Oh
 * @date 2018. 3. 9. 오전 10:48:11
 */

@Service
@Transactional
public class QACategoryService {

	@Autowired
	private QACategoryRepository qaCategoryRepository;

	@Transactional(readOnly = true)
	public QACategory retrieveQACategoryByIdx(int idx) {
		return qaCategoryRepository.retrieveQACategoryByIdx(idx);
	}

}
