package com.git.gdsbuilder.parser.file.dxf;

import java.util.Iterator;

import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFVertex;
import org.kabeja.dxf.helpers.Point;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.util.GeometricShapeFactory;

/**
 * {@link DXFEntity} 타입에 따라 호환 가능한 {@link Geometry}로 변환하는 클래스.
 * 
 * @author DY.Oh
 */
public class DXFFileGeomParser {

	/**
	 * 중심점, 반지름, 각도, 각도의 크기 값을 이용하여 {@link LineString} 타입의 Arc를 {@link Geometry}로
	 * 반환.
	 * 
	 * @param epsg      좌표계 (ex EPSG:4326)
	 * @param point     중심점
	 * @param radius    반지름
	 * @param startAng  각도
	 * @param angExtent 각도의 크기
	 * @return {@link Geometry}
	 * @throws Exception {@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public static Geometry parseDTArc(String epsg, Point point, double radius, double startAng, double angExtent)
			throws Exception {

		// CenterPt
		Coordinate coor = new Coordinate(point.getX(), point.getY(), point.getZ());
		GeometricShapeFactory f = new GeometricShapeFactory();
		f.setCentre(coor);
		f.setSize(radius * 2);
		f.setNumPoints(100);
		f.setRotation(0);

		Geometry arc = f.createArc(Math.toRadians(startAng), Math.toRadians(angExtent));
		return arc;
	}

	/**
	 * 중심점, 반지름 값을 이용하여 {@link LineString} 타입의 Circle을 {@link Geometry}로 반환.
	 * 
	 * @param epsg   좌표계 (ex EPSG:4326)
	 * @param pt     중심점
	 * @param radius 반지름
	 * @return {@link Geometry}
	 * @throws Exception {@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public static Geometry parseDTCircle(String epsg, Point pt, double radius) throws Exception {

		Coordinate coor = new Coordinate(pt.getX(), pt.getY(), pt.getZ());
		GeometricShapeFactory f = new GeometricShapeFactory();
		f.setCentre(coor);
		f.setSize(radius * 2);
		f.setNumPoints(100);
		f.setRotation(0);

		Geometry circle = f.createCircle().getBoundary();
		return circle;
	}

	/**
	 * {@link Point} 타입의 {@link Geometry}로 반환.
	 * 
	 * @param epsg 좌표계 (ex EPSG:4326)
	 * @param pt   중심점
	 * @return {@link Geometry}
	 * @throws Exception {@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public static Geometry parseDTPoint(String epsg, Point pt) throws Exception {

		Coordinate coor = new Coordinate(pt.getX(), pt.getY(), pt.getZ());
		GeometryFactory gf = new GeometryFactory();

		Geometry geom = gf.createPoint(coor);
		return geom;
	}

	/**
	 * 2개의 Point(첫점, 끝점)를 {@link LineString} 타입의 {@link Geometry}로 반환.
	 * 
	 * @param epsg    좌표계 (ex EPSG:4326)
	 * @param startPt {@link LineString}의 첫점
	 * @param endPt   {@link LineString}의 끝점
	 * @return {@link Geometry}
	 * @throws Exception {@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public static Geometry parseDTLine(String epsg, Point startPt, Point endPt) throws Exception {

		Coordinate startCoor = new Coordinate(startPt.getX(), startPt.getY(), startPt.getZ());
		Coordinate endCoor = new Coordinate(endPt.getX(), endPt.getY(), endPt.getZ());
		Coordinate[] lineCoor = new Coordinate[2];
		lineCoor[0] = startCoor;
		lineCoor[1] = endCoor;

		GeometryFactory gf = new GeometryFactory();
		Geometry line = gf.createLineString(lineCoor);
		return line;
	}

	/**
	 * n개의 Point(vertexIterator)를 {@link LineString} 타입의 {@link Geometry}로 반환.
	 * 
	 * @param epsg           좌표계 (ex EPSG:4326)
	 * @param isClosed       LineString 폐합 여부 ({@code true} : 폐합, {@code false} :
	 *                       미폐합)
	 *                       <p>
	 *                       {@code true}일 경우 {@link LineString}의 첫 점을 끝 점으로 한번 더
	 *                       추가하여 폐합 시킴.
	 * @param vertexIterator Point Collection
	 * @param vertexCount    Point 갯수
	 * @return {@link Geometry}
	 * @throws Exception {@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public static Geometry parseDTLineString(String epsg, boolean isClosed, Iterator vertexIterator, int vertexCount)
			throws Exception {

		GeometryFactory gf = new GeometryFactory();
		Coordinate[] coors;
		if (isClosed) {
			coors = new Coordinate[vertexCount + 1];
			int i = 0;
			while (vertexIterator.hasNext()) {
				if (i < vertexCount) {
					DXFVertex dxfVertex = (DXFVertex) vertexIterator.next();
					coors[i] = new Coordinate(dxfVertex.getX(), dxfVertex.getY(), dxfVertex.getZ());
					i++;
				} else {
					break;
				}
			}
			coors[vertexCount] = coors[0];
		} else {
			coors = new Coordinate[vertexCount];
			int i = 0;
			while (vertexIterator.hasNext()) {
				if (i < vertexCount) {
					DXFVertex dxfVertex = (DXFVertex) vertexIterator.next();
					coors[i] = new Coordinate(dxfVertex.getX(), dxfVertex.getY(), dxfVertex.getZ());
					i++;
				} else {
					break;
				}
			}
		}
		Geometry line = gf.createLineString(coors);
		return line;
	}

	/**
	 * n개의 Point(vertexIterator)를 높이값(z)이 존재하는 {@link LineString} 타입의
	 * {@link Geometry}로 반환.
	 * 
	 * @param epsg           좌표계 (ex EPSG:4326)
	 * @param isClosed       LineString 폐합 여부 ({@code true} : 폐합, {@code false} :
	 *                       미폐합)
	 *                       <p>
	 *                       {@code true}일 경우 {@link LineString}의 첫 점을 끝 점으로 한번 더
	 *                       추가하여 폐합 시킴.
	 * @param vertexIterator Point Collection
	 * @param vertexCount    Point 갯수
	 * @param elevation      높이값(z)
	 * @return {@link Geometry}
	 * @throws Exception {@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public static Geometry parseDT3DLineString(String epsg, boolean isClosed, Iterator vertexIterator, int vertexCount,
			double elevation) throws Exception {

		GeometryFactory gf = new GeometryFactory();
		Coordinate[] coors;
		if (isClosed) {
			coors = new Coordinate[vertexCount + 1];
			int i = 0;
			while (vertexIterator.hasNext()) {
				if (i < vertexCount) {
					DXFVertex dxfVertex = (DXFVertex) vertexIterator.next();
					coors[i] = new Coordinate(dxfVertex.getX(), dxfVertex.getY(), elevation);
					i++;
				} else {
					break;
				}
			}
			coors[vertexCount] = coors[0];
		} else {
			coors = new Coordinate[vertexCount];
			int i = 0;
			while (vertexIterator.hasNext()) {
				if (i < vertexCount) {
					DXFVertex dxfVertex = (DXFVertex) vertexIterator.next();
					coors[i] = new Coordinate(dxfVertex.getX(), dxfVertex.getY(), elevation);
					i++;
				} else {
					break;
				}
			}
		}
		Geometry line = gf.createLineString(coors);
		return line;
	}

	/**
	 * 4개의 Point를 {@link Polygon} 타입의 {@link Geometry}로 반환.
	 * 
	 * @param epsg   좌표계 (ex EPSG:4326)
	 * @param point1 {@link Polygon}의 첫째 점
	 * @param point2 {@link Polygon}의 둘째 점
	 * @param point3 {@link Polygon}의 셋째 점
	 * @param point4 {@link Polygon}의 넷째 점
	 * @return {@link Geometry}
	 * @throws Exception {@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public static Geometry parseDTPolygon(String epsg, Point point1, Point point2, Point point3, Point point4)
			throws Exception {

		GeometryFactory gf = new GeometryFactory();
		Coordinate coor1 = new Coordinate(point1.getX(), point1.getY(), point1.getZ());
		Coordinate coor2 = new Coordinate(point2.getX(), point2.getY(), point2.getZ());
		Coordinate coor3 = new Coordinate(point3.getX(), point3.getY(), point3.getZ());
		Coordinate coor4 = new Coordinate(point4.getX(), point4.getY(), point4.getZ());

		LinearRing lr;
		Geometry polygon;
		if (coor3.equals3D(coor4)) {
			coor4 = coor1;
			lr = gf.createLinearRing(new Coordinate[] { coor1, coor2, coor3, coor4 });
			polygon = gf.createPolygon(lr, null);
		} else {
			lr = gf.createLinearRing(new Coordinate[] { coor1, coor2, coor3, coor4 });
			polygon = gf.createPolygon(lr, null);
		}
		return polygon;
	}

	/**
	 * 최대 x,y Point 및 최소 x,y Point를 {@link Polygon} 타입의 {@link Geometry}로 반환.
	 * 
	 * @param epsg     좌표계 (ex EPSG:4326)
	 * @param maximumX 최대 x 좌표
	 * @param maximumY 최대 y 좌표
	 * @param minimumX 최소 x 좌표
	 * @param minimumY 최소 y 좌표
	 * @return {@link Geometry}
	 * @throws Exception {@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public static Geometry parseDTPolygon(String epsg, double maximumX, double maximumY, double minimumX,
			double minimumY) throws Exception {

		GeometryFactory gf = new GeometryFactory();

		Coordinate coor1 = new Coordinate(maximumX, maximumY);
		Coordinate coor2 = new Coordinate(maximumX, minimumY);
		Coordinate coor3 = new Coordinate(minimumX, minimumY);
		Coordinate coor4 = new Coordinate(minimumX, maximumY);

		Coordinate[] coors = new Coordinate[] { coor1, coor2, coor3, coor4, coor1 };
		LinearRing lr = gf.createLinearRing(coors);

		Geometry polygon = gf.createPolygon(lr, null);
		return polygon;
	}
}
