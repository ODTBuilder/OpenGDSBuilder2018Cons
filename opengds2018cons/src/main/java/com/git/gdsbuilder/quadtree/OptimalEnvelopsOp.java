package com.git.gdsbuilder.quadtree;

import java.util.ArrayList;
import java.util.List;

import org.geotools.data.shapefile.index.quadtree.QuadTree;
import org.geotools.data.simple.SimpleFeatureCollection;

import com.vividsolutions.jts.geom.Envelope;

/**
 * 1개 영역 내 포함되는 최대 객체 수(maxSize)에 따라 {@link QuadTree} 검수 영역 Level을 계산하는 클래스.
 * 
 * @author DY.Oh
 *
 */
public class OptimalEnvelopsOp {

	/**
	 * {@link SimpleFeatureCollection} Quadtree
	 */
	private Quadtree quadTree;
	/**
	 * 1개 영역 내 포함되는 최대 객체 수(maxSize)
	 */
	private int maxSize;
	/**
	 * Quadtree 최대 레벨
	 */
	private int maxLevel;
	/**
	 * 1개 영역 내 포함되는 최대 객체 수(maxSize)를 만족하는 Quadtree 레벨
	 */
	private int optimalLevel;

	/**
	 * OptimalEnvelopsOp 생성자.
	 * 
	 * @param quadTree quadTree
	 * @param maxLevel Quadtree 최대 레벨
	 * @param maxSize  1개 영역 내 포함되는 최대 객체 수
	 */
	public OptimalEnvelopsOp(Quadtree quadTree, int maxLevel, int maxSize) {
		this.quadTree = quadTree;
		this.maxLevel = maxLevel;
		this.maxSize = maxSize;
	}

	/**
	 * level에 해당하는 Index 영역 목록 반환.
	 * <p>
	 * 해당 level에 도달 할 때 까지 Max 레벨부터 순차적으로 계산.
	 * 
	 * @param level quadtree 레벨
	 * @return Index 영역 목록
	 * 
	 * @author DY.Oh
	 */
	public List<Object> getOptimalEnvelops(int level) {

		boolean isOptimal = true;
		Node[] nodes = quadTree.getRoot().getSubnode();
		List<Object> envelopeList = getNodeEnvelopeList(nodes, level);
		for (Object result : envelopeList) {
			Envelope envelope = (Envelope) result;
			List items = quadTree.query(envelope);
			int size = items.size();
			if (size > maxSize || size == 0) {
				isOptimal = false;
				break;
			}
		}
		if (isOptimal) {
			this.optimalLevel = level;
			return envelopeList;
		} else {
			return getOptimalEnvelops(level - 1);
		}
	}

	private List<Object> getNodeEnvelopeList(Node[] nodes, int level) {

		List<Object> envelopeList = new ArrayList<>();

		int length = nodes.length;
		Node[] levelNodes = new Node[quadTree.size()];
		int n = 0;
		for (int i = 0; i < length; i++) {
			Node node = nodes[i];
			if (node != null) {
				int subLevel = node.getLevel();
				if (subLevel == level) {
					envelopeList.add(node.getEnvelope());
				} else if (subLevel > level) {
					Node[] subLodes = node.getSubnode();
					int subLength = subLodes.length;
					for (int s = 0; s < subLength; s++) {
						Node subNode = subLodes[s];
						if (subNode != null) {
							levelNodes[n] = subLodes[s];
							n++;
						}
					}
				} else {
					break;
				}
			}
		}
		if (n > 0) {
			return getNodeEnvelopeList(levelNodes, level);
		} else {
			return envelopeList;
		}
	}

	public Quadtree getQuadTree() {
		return quadTree;
	}

	public void setQuadTree(Quadtree quadTree) {
		this.quadTree = quadTree;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public int getLevel() {
		return maxLevel;
	}

	public void setLevel(int level) {
		this.maxLevel = level;
	}

}
