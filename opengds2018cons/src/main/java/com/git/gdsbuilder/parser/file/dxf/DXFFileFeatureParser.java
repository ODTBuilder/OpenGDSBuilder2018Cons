package com.git.gdsbuilder.parser.file.dxf;

import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.kabeja.dxf.Bounds;
import org.kabeja.dxf.DXFArc;
import org.kabeja.dxf.DXFCircle;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFInsert;
import org.kabeja.dxf.DXFLWPolyline;
import org.kabeja.dxf.DXFLine;
import org.kabeja.dxf.DXFPolyline;
import org.kabeja.dxf.DXFSolid;
import org.kabeja.dxf.DXFText;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;

/**
 * {@link DXFEntity}를 {@link SimpleFeature}로 변환하는 클래스.
 * <p>
 * {@link DXFEntity} Entity 타입에 따라 {@link SimpleFeature}의 Schema인
 * {@link SimpleFeatureType} 생성함.
 * <p>
 * {@link SimpleFeatureType}의 featureID, the_geom, originType은 {@link DXFEntity}
 * Entity 타입에 상관없이 Default로 생성되며 이외의 속성은 각 Entity 타입에 따라 다른 값을 가질 수 있음.
 * 
 * @author DY.Oh
 *
 */
public class DXFFileFeatureParser {

	protected static String featureID = "featureID";
	protected static String the_geom = "the_geom";
	protected static String textValue = "textValue";
	protected static String elevation = "elevation";
	protected static String rotate = "rotate";
	protected static String width = "width";
	protected static String height = "height";
	protected static String flag = "flag";
	protected static String originType = "originType";
	protected static String boundary = "boundary";

	/**
	 * LINE 타입의 {@link DXFEntity}를 {@link SimpleFeature}로 변환.
	 * 
	 * @param epsg      좌표계 (ex EPSG:4326)
	 * @param dxfEntity DXF Entity
	 * @return {@link DXFEntity}를 변환한 {@link SimpleFeature}
	 * @throws Exception {@link Exception}{@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public static SimpleFeature parseDTLineFeaeture(String epsg, DXFEntity dxfEntity) throws Exception {

		String entityType = dxfEntity.getType();
		if (entityType.equals("LINE")) {
			DXFLine dxfLine = (DXFLine) dxfEntity;
			Geometry geom = DXFFileGeomParser.parseDTLine(epsg, dxfLine.getStartPoint(), dxfLine.getEndPoint());
			String entityID = dxfLine.getID();
			String geomType = geom.getGeometryType();

			// create sf
			SimpleFeatureType sfType = DataUtilities.createType(entityID,
					featureID + ":String," + the_geom + ":" + geomType + "," + originType + ":String");
			SimpleFeature newSimpleFeature = SimpleFeatureBuilder.build(sfType,
					new Object[] { entityID, geom, entityType }, entityID);
			return newSimpleFeature;
		} else {
			return null;
		}
	}

	/**
	 * POLYLINE 타입의 {@link DXFEntity}를 {@link SimpleFeature}로 변환.
	 * <p>
	 * POLYLINE Entity는 높이값(z)과 폐합여부(flags)를 속성으로 가지므로 해당 값을 {@link SimpleFeature}의
	 * {@link SimpleFeatureType}으로 변환함.
	 * 
	 * @param epsg      좌표계 (ex EPSG:4326)
	 * @param dxfEntity DXF Entity
	 * @return {@link DXFEntity}를 변환한 {@link SimpleFeature}
	 * @throws Exception {@link Exception}{@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public static SimpleFeature parseDTPolylineFeature(String epsg, DXFEntity dxfEntity) throws Exception {

		String entityType = dxfEntity.getType();
		if (entityType.equals("POLYLINE")) {
			DXFPolyline dxfPolyline = (DXFPolyline) dxfEntity;
			Geometry geom = DXFFileGeomParser.parseDTLineString(epsg, dxfPolyline.isClosed(),
					dxfPolyline.getVertexIterator(), dxfPolyline.getVertexCount());
			String entityID = dxfPolyline.getID();
			double elevationValue = geom.getCoordinate().z;
			int flagValue = dxfEntity.getFlags();

			// create sf
			String geomType = geom.getGeometryType();
			SimpleFeatureType sfType = DataUtilities.createType(entityID, featureID + ":String," + the_geom + ":"
					+ geomType + "," + elevation + ":Double," + flag + ":Integer" + "," + originType + ":String");
			SimpleFeature newSimpleFeature = SimpleFeatureBuilder.build(sfType,
					new Object[] { entityID, geom, elevationValue, flagValue, entityType }, entityID);
			return newSimpleFeature;
		} else {
			return null;
		}
	}

	/**
	 * LWPOLYLINE 타입의 {@link DXFEntity}를 {@link SimpleFeature}로 변환.
	 * <p>
	 * LWPOLYLINE Entity는 높이값(z)과 폐합여부(flags)를 속성으로 가지므로 해당 값을
	 * {@link SimpleFeature}의 {@link SimpleFeatureType}으로 변환함.
	 * 
	 * @param epsg      좌표계 (ex EPSG:4326)
	 * @param dxfEntity DXF Entity
	 * @return {@link DXFEntity}를 변환한 {@link SimpleFeature}
	 * @throws Exception {@link Exception}{@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public static SimpleFeature parseDTLWPolylineFeature(String epsg, DXFEntity dxfEntity) throws Exception {

		String entityType = dxfEntity.getType();
		if (entityType.equals("LWPOLYLINE")) {
			DXFLWPolyline dxfLwPolyline = (DXFLWPolyline) dxfEntity;
			Geometry geom = DXFFileGeomParser.parseDTLineString(epsg, dxfLwPolyline.isClosed(),
					dxfLwPolyline.getVertexIterator(), dxfLwPolyline.getVertexCount());

			String entityID = dxfLwPolyline.getID();
			double elevationValue = dxfLwPolyline.getElevation();
			int flagValue = dxfEntity.getFlags();

			// create sf
			String geomType = geom.getGeometryType();
			SimpleFeatureType sfType = DataUtilities.createType(entityID, featureID + ":String," + the_geom + ":"
					+ geomType + "," + elevation + ":Double," + flag + ":Integer" + "," + originType + ":String");
			SimpleFeature newSimpleFeature = SimpleFeatureBuilder.build(sfType,
					new Object[] { entityID, geom, elevationValue, flagValue, entityType }, entityID);
			return newSimpleFeature;
		} else {
			return null;
		}
	}

	/**
	 * INSERT 타입의 {@link DXFEntity}를 {@link SimpleFeature}로 변환.
	 * <p>
	 * INSERT Entity는 각도(rotate)를 속성으로 가지므로 해당 값을 {@link SimpleFeature}의
	 * {@link SimpleFeatureType}으로 변환함.
	 * 
	 * @param epsg      좌표계 (ex EPSG:4326)
	 * @param dxfEntity DXF Entity
	 * @return {@link DXFEntity}를 변환한 {@link SimpleFeature}
	 * @throws Exception {@link Exception}{@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public static SimpleFeature parseDTInsertFeature(String epsg, DXFEntity dxfEntity) throws Exception {

		String entityType = dxfEntity.getType();
		if (entityType.equals("INSERT")) {
			DXFInsert dxfInsert = (DXFInsert) dxfEntity;
			Geometry geom = DXFFileGeomParser.parseDTPoint(epsg, dxfInsert.getPoint());
			double rotateValue = dxfInsert.getRotate();
			String entityID = dxfInsert.getID();

			// create sf
			String geomType = geom.getGeometryType();
			SimpleFeatureType sfType = DataUtilities.createType(entityID, featureID + ":String," + the_geom + ":"
					+ geomType + "," + rotate + ":Double" + "," + originType + ":String");
			SimpleFeature newSimpleFeature = SimpleFeatureBuilder.build(sfType,
					new Object[] { entityID, geom, rotateValue, entityType }, entityID);
			return newSimpleFeature;
		} else {
			return null;
		}
	}

	/**
	 * CIRCLE 타입의 {@link DXFEntity}를 {@link SimpleFeature}로 변환.
	 * 
	 * @param epsg      좌표계 (ex EPSG:4326)
	 * @param dxfEntity DXF Entity
	 * @return {@link DXFEntity}를 변환한 {@link SimpleFeature}
	 * @throws Exception {@link Exception}{@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public static SimpleFeature parseDTCircleFeature(String epsg, DXFEntity dxfEntity) throws Exception {

		String entityType = dxfEntity.getType();
		if (entityType.equals("CIRCLE")) {
			DXFCircle dxfCircle = (DXFCircle) dxfEntity;
			Geometry geom = DXFFileGeomParser.parseDTCircle(epsg, dxfCircle.getCenterPoint(), dxfCircle.getRadius());
			String entityID = dxfCircle.getID();

			// create sf
			String geomType = geom.getGeometryType();
			SimpleFeatureType sfType = DataUtilities.createType(entityID,
					featureID + ":String," + the_geom + ":" + geomType + "," + originType + ":String");
			SimpleFeature newSimpleFeature = SimpleFeatureBuilder.build(sfType,
					new Object[] { entityID, geom, entityType }, entityID);
			return newSimpleFeature;
		} else {
			return null;
		}
	}

	/**
	 * SOLID 타입의 {@link DXFEntity}를 {@link SimpleFeature}로 변환.
	 * 
	 * @param epsg      좌표계 (ex EPSG:4326)
	 * @param dxfEntity DXF Entity
	 * @return {@link DXFEntity}를 변환한 {@link SimpleFeature}
	 * @throws Exception {@link Exception}{@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public static SimpleFeature parseDTSolidFeature(String epsg, DXFEntity dxfEntity) throws Exception {

		String entityType = dxfEntity.getType();
		if (entityType.equals("SOLID")) {
			DXFSolid dxfSolid = (DXFSolid) dxfEntity;
			Geometry geom = DXFFileGeomParser.parseDTPolygon(epsg, dxfSolid.getPoint1(), dxfSolid.getPoint2(),
					dxfSolid.getPoint3(), dxfSolid.getPoint4());
			String entityID = dxfSolid.getID();

			// create sf
			String geomType = geom.getGeometryType();
			SimpleFeatureType sfType = DataUtilities.createType(entityID,
					featureID + ":String," + the_geom + ":" + geomType + "," + originType + ":String");
			SimpleFeature newSimpleFeature = SimpleFeatureBuilder.build(sfType,
					new Object[] { entityID, geom, entityType }, entityID);
			return newSimpleFeature;
		} else {
			return null;
		}

	}

	/**
	 * TEXT 타입의 {@link DXFEntity}를 {@link SimpleFeature}로 변환.
	 * <p>
	 * TEXT Entity는 text, 높이값(height)과 각도(Rotation)를 속성으로 가지므로 해당 값을
	 * {@link SimpleFeature}의 {@link SimpleFeatureType}으로 변환함.
	 * 
	 * @param epsg      좌표계 (ex EPSG:4326)
	 * @param dxfEntity DXF Entity
	 * @return {@link DXFEntity}를 변환한 {@link SimpleFeature}
	 * @throws Exception {@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public static SimpleFeature parseDTTextFeature(String epsg, DXFEntity dxfEntity) throws Exception {

		String entityType = dxfEntity.getType();
		if (entityType.equals("TEXT")) {
			DXFText dxfText = (DXFText) dxfEntity;
			Geometry geom = DXFFileGeomParser.parseDTPoint(epsg, dxfText.getInsertPoint());
			String entityID = dxfText.getID();
			Bounds bounds = dxfText.getBounds();
			Geometry boundsGeom = DXFFileGeomParser.parseDTPolygon(epsg, bounds.getMaximumX(), bounds.getMaximumY(),
					bounds.getMinimumX(), bounds.getMinimumY());

			// create sf
			String geomType = geom.getGeometryType();
			String boundGeomType = boundsGeom.getGeometryType();
			SimpleFeatureType sfType = DataUtilities.createType(entityID,
					featureID + ":String," + the_geom + ":" + geomType + "," + textValue + ":String," + rotate
							+ ":Double," + height + ":Double," + originType + ":String," + boundary + ":"
							+ boundGeomType);
			SimpleFeature newSimpleFeature = SimpleFeatureBuilder.build(sfType, new Object[] { entityID, geom,
					dxfText.getText(), dxfText.getRotation(), dxfText.getHeight(), entityType, boundsGeom }, entityID);
			return newSimpleFeature;
		} else {
			return null;
		}
	}

	/**
	 * ARC 타입의 {@link DXFEntity}를 {@link SimpleFeature}로 변환.
	 * 
	 * @param epsg      좌표계 (ex EPSG:4326)
	 * @param dxfEntity DXF Entity
	 * @return {@link DXFEntity}를 변환한 {@link SimpleFeature}
	 * @throws Exception {@link Exception}
	 * 
	 * @author DY.Oh
	 */
	public static SimpleFeature parseDTArcFeature(String epsg, DXFEntity dxfEntity) throws Exception {

		String entityType = dxfEntity.getType();
		if (entityType.equals("ARC")) {
			DXFArc dxfArc = (DXFArc) dxfEntity;
			Geometry geom = DXFFileGeomParser.parseDTArc(epsg, dxfArc.getCenterPoint(), dxfArc.getRadius(),
					dxfArc.getStartAngle(), dxfArc.getTotalAngle());
			String entityID = dxfArc.getID();
			String geomType = geom.getGeometryType();

			// create sf
			SimpleFeatureType sfType = DataUtilities.createType(entityID,
					featureID + ":String," + the_geom + ":" + geomType + "," + originType + ":String");
			SimpleFeature newSimpleFeature = SimpleFeatureBuilder.build(sfType,
					new Object[] { entityID, geom, entityType }, entityID);
			return newSimpleFeature;
		} else {
			return null;
		}
	}
}
