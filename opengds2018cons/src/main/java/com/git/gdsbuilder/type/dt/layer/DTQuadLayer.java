package com.git.gdsbuilder.type.dt.layer;

import com.git.gdsbuilder.quadtree.Quadtree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className DTLayer.java
 * @description DTLayer 정보를 저장하는 클래스
 * @author DY.Oh
 * @date 2018. 1. 30. 오후 2:03:42
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTQuadLayer extends DTLayer {

	Quadtree quadtree;

}
