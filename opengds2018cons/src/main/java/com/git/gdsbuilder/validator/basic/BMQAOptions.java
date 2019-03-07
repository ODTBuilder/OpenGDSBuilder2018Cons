/**
 * 
 */
package com.git.gdsbuilder.validator.basic;

/**
 * @className NFMQAOptions.java
 * @description
 * @author DY.Oh
 * @date 2018. 3. 15. 오전 11:25:05
 */
public class BMQAOptions {

	public enum EnLangType {
		DEFALUT, EN, KO
	}

	public enum Type {

		// 개념일관성 - 스키마 규칙 준수
		ERR_ENTITYTYPE("논리일관성", "개념일관성", "스키마규칙준수", "객체타입불일치"),
		ERR_LAYERTYPE("논리일관성", "개념일관성", "스키마규칙준수", "미정의레이어"),
		ERR_CLUNMTYPE("논리일관성", "개념일관성", "스키마규칙준수", "컬럼타입불일치"),
		ERR_CLUNM("논리일관성", "개념일관성", "스키마규칙준수", "테이블컬럼규칙비준수"),
		ERR_ATTRVALUE("논리일관성", "개념일관성", "스키마규칙준수", "필수컬럼속성누락"),
		
		//도메인일관성 - 코드리스트 규칙 준수
		ERR_CODE("논리일관성", "도메인일관성", "코드리스트규칙준수", "코드리스트비준수"),
		// 구현해야함
		
		//도메인일관성 - 숫자오류
		ERR_NUMTYPE("논리일관성", "도메인일관성", "숫자오류", "숫자타입입력오류"),
		
		//도메인일관성 - 범위불일치
		ERR_RANGE_BFLR_CO("논리일관성", "도메인일관성", "범위불일치", "건물층수오류"),
		ERR_RANGE_HG("논리일관성", "도메인일관성", "범위불일치", "높이값초과"),
		ERR_RANGE_SE10_ROAD_BT("논리일관성", "도메인일관성", "범위불일치", "면리간도로폭오류"), 
		ERR_RANGE_SE14_ROAD_BT("논리일관성", "도메인일관성", "범위불일치", "소로폭오류"), 
		ERR_RANGE_SE00_ROAD_BT("논리일관성", "도메인일관성", "범위불일치", "실폭차도폭오류"), 
		ERR_RANGE_BULD_NM("논리일관성", "도메인일관성", "범위불일치", "일반주택의주기"),
		ERR_RANGE_CARTRK_CO("논리일관성", "도메인일관성", "범위불일치", "차로수오류"),
		
		// 위상일관성
		ERR_GRAPHIC_TWISTED("논리일관성", "위상일관성", "도형형상", "꼬임선형객체"),
		ERR_GRAPHIC_AREA("논리일관성", "위상일관성", "도형형상", "미세면적객체"),
		ERR_GRAPHIC_Length("논리일관성", "위상일관성", "도형형상", "짧은선형객체"),
		ERR_GRAPHIC_PT_DUPLICATED("논리일관성", "위상일관성", "도형형상", "정점중복선형객체"),
		ERR_GRAPHIC_ET_DUPLICATED("논리일관성", "위상일관성", "객체중복", "중복객체"),
		ERR_GRAPHIC_CON_INTERSECTED("논리일관성", "위상일관성", "등고선오류", "등고선교차"),
		ERR_GRAPHIC_CON_OVERDEGREE("논리일관성", "위상일관성", "등고선오류", "등고선꺾임"),
		ERR_GRAPHIC_CON_BREAK("논리일관성", "위상일관성", "등고선오류", "등고선단락"),
		ERR_GRAPHIC_CON_UNCONNECTED("논리일관성", "위상일관성", "등고선오류", "등고선미연결"),
		ERR_GRAPHIC_CENTERLINE_MISS("논리일관성", "위상일관성", "중심선누락", "누락"),
		ERR_GRAPHIC_SELFENTITY("논리일관성", "위상일관성", "경계침범", "침범");
		
		
		String validation;
		String type;
		String item;
		String name;

		private Type(String validation, String type, String item, String name) {
			this.validation = validation;
			this.type = type;
			this.item = item;
			this.name = name;
		}

		public String getValidation() {
			return validation;
		}

		public void setValidation(String validation) {
			this.validation = validation;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getItem() {
			return item;
		}

		public void setItem(String item) {
			this.item = item;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

}
