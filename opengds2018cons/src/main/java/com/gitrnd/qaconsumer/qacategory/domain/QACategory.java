/**
 * 
 */
package com.gitrnd.qaconsumer.qacategory.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 검수 카테고리.
 * 
 * @author IJ.S
 *
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class QACategory {

	/**
	 * index
	 */
	int cidx;
	/**
	 * 검수 카테고리명
	 */
	String title;
	/**
	 * 지원 파일 포맷
	 */
	String support;
}
