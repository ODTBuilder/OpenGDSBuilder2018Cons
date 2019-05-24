package com.gitrnd.qaconsumer.preset.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Preset 객체.
 * 
 * @author DY.Oh
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Preset {

	/**
	 * tb_preset Index
	 */
	private int pid;
	/**
	 * preset name
	 */
	private String name;
	/**
	 * preset category
	 */
	private int cat;
	/**
	 * preset title
	 */
	private String title;
	/**
	 * 레이어 정의
	 */
	private String layerDef;
	/**
	 * 검수 옵션 정의
	 */
	private String optionDef;
	/**
	 * tb_user Index
	 */
	private int uidx;
	/**
	 * Default Preset 여부
	 */
	private boolean bflag;
	/**
	 * 지원 파일 유형
	 */
	private String support;

}
