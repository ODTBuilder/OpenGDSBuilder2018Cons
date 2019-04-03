package com.git.gdsbuilder.validator.basic;

import java.util.ArrayList;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.git.gdsbuilder.type.dt.feature.DTFeature;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.validate.option.specific.AttributeFilter;
import com.git.gdsbuilder.type.validate.option.specific.OptionFilter;
import com.git.gdsbuilder.type.validate.option.specific.OptionTolerance;
import com.git.gdsbuilder.validator.feature.filter.FeatureFilter;
import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;

import lombok.Data;

@Data
public class BasicFeatureGraphicValidator {

	public BasicErrorFeature validateLayerFixMiss(DTFeature feature, String geomType) {

		// 객체타입 불일치
		SimpleFeature sf = feature.getSimefeature();
		Geometry geom = (Geometry) sf.getDefaultGeometry();
		if (geom == null || geom.isEmpty()) {
			return null;
		}
		if (!geom.getGeometryType().toUpperCase().equals(geomType.toUpperCase())) {
			Geometry errPt = null;
			try {
				errPt = geom.getInteriorPoint();
			} catch (TopologyException e) {
				Coordinate[] coors = geom.getCoordinates();
				errPt = new GeometryFactory().createPoint(coors[0]);
			}
			BasicErrorFeature err = new BasicErrorFeature();
			err.setErrPoint(errPt);
			err.setGeomStr(geom.toString());
			err.setLayerID(feature.getLayerID());
			err.setValidation(BMQAOptions.Type.ERR_ENTITYTYPE.getValidation());
			err.setType(BMQAOptions.Type.ERR_ENTITYTYPE.getType());
			err.setItem(BMQAOptions.Type.ERR_ENTITYTYPE.getItem());
			err.setName(BMQAOptions.Type.ERR_ENTITYTYPE.getName());
			return err;
		} else {
			return null;
		}
	}

	public List<BasicErrorFeature> validateEntityTwisted(DTFeature feature) {

		SimpleFeature sf = feature.getSimefeature();
		Geometry geom = (Geometry) sf.getDefaultGeometry();
		String geomType = geom.getGeometryType();

		Geometry boundary = null;
		if (geomType.equals("LineString") || geomType.equals("MultiLineString")) {
			boundary = geom;
		}
		if (geomType.equals("Polygon") || geomType.equals("MultiPolygon")) {
			boundary = geom.getBoundary();
		}

		List<BasicErrorFeature> errList = new ArrayList<>();
		if (boundary != null) {
			if (!boundary.isSimple()) {
				GeometryFactory factory = new GeometryFactory();
				Coordinate[] coordinates = boundary.getCoordinates();
				for (int i = 0; i < coordinates.length - 1; i++) {
					Coordinate[] coordI = new Coordinate[] { new Coordinate(coordinates[i]),
							new Coordinate(coordinates[i + 1]) };
					LineString lineI = factory.createLineString(coordI);
					for (int j = i + 1; j < coordinates.length - 1; j++) {
						Coordinate[] coordJ = new Coordinate[] { new Coordinate(coordinates[j]),
								new Coordinate(coordinates[j + 1]) };
						LineString lineJ = factory.createLineString(coordJ);
						if (lineI.intersects(lineJ)) {
							Geometry intersectGeom = lineI.intersection(lineJ);
							Coordinate[] intersectCoors = intersectGeom.getCoordinates();
							for (int k = 0; k < intersectCoors.length; k++) {
								Coordinate interCoor = intersectCoors[k];
								Geometry errPt = factory.createPoint(interCoor);
								Boolean flag = false;
								for (int l = 0; l < coordI.length; l++) {
									Coordinate coordPoint = coordI[l];
									if (interCoor.equals2D(coordPoint)) {
										flag = true;
										break;
									}
								}
								if (flag == false) {
									BasicErrorFeature err = new BasicErrorFeature();
									err.setErrPoint(errPt);
									err.setGeomStr(geom.toString());
									err.setLayerID(feature.getLayerID());
									err.setValidation(BMQAOptions.Type.ERR_GRAPHIC_TWISTED.getValidation());
									err.setType(BMQAOptions.Type.ERR_GRAPHIC_TWISTED.getType());
									err.setItem(BMQAOptions.Type.ERR_GRAPHIC_TWISTED.getItem());
									err.setName(BMQAOptions.Type.ERR_GRAPHIC_TWISTED.getName());
									errList.add(err);
								}
							}
						}
					}
				}
			}
		}
		if (errList.size() > 0) {
			return errList;
		} else {
			return null;
		}
	}

	public BasicErrorFeature validateSmallArea(DTFeature feature, OptionTolerance tolerance) {

		SimpleFeature sf = feature.getSimefeature();
		boolean isTrue = false;
		List<AttributeFilter> filters = feature.getFilter();
		if (filters != null) {
			isTrue = FeatureFilter.filter(sf, filters);
		} else {
			isTrue = true;
		}
		boolean isError = false;
		Geometry geom = (Geometry) sf.getDefaultGeometry();
		Double value = tolerance.getValue();
		String conditon = tolerance.getCondition();

		CoordinateReferenceSystem dataCRS = sf.getFeatureType().getCoordinateReferenceSystem();
		CoordinateReferenceSystem worldCRS;
		MathTransform transform;
		try {
			if (isTrue) {
				if (geom.getGeometryType().equals("MultiPolygon") || geom.getGeometryType().equals("Polygon")) {
					if (dataCRS != null) {
						// crs transform
						worldCRS = CRS.decode("EPSG:32652");
						transform = CRS.findMathTransform(dataCRS, worldCRS, true);
						for (int i = 0; i < geom.getNumGeometries(); i++) {
							Geometry g = JTS.transform(geom.getGeometryN(i), transform);
							double geomArea = g.getArea();
							if (conditon.equals("over")) {
								if (geomArea < value) {
									isError = true;
								}
							} else if (conditon.equals("under")) {
								if (geomArea > value) {
									isError = true;
								}
							} else if (conditon.equals("equal")) {
								if (geomArea != value) {
									isError = true;
								}
							}
						}
					} else {
						for (int i = 0; i < geom.getNumGeometries(); i++) {
							double geomArea = geom.getGeometryN(i).getArea();
							if (conditon.equals("over")) {
								if (geomArea < value) {
									isError = true;
								}
							} else if (conditon.equals("under")) {
								if (geomArea > value) {
									isError = true;
								}
							} else if (conditon.equals("equal")) {
								if (geomArea != value) {
									isError = true;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (isError) {
			Geometry errPt = null;
			try {
				errPt = geom.getInteriorPoint();
			} catch (TopologyException e) {
				Coordinate[] coors = geom.getCoordinates();
				errPt = new GeometryFactory().createPoint(coors[0]);
			}
			BasicErrorFeature err = new BasicErrorFeature();
			err.setErrPoint(errPt);
			err.setGeomStr(geom.toString());
			err.setLayerID(feature.getLayerID());
			err.setValidation(BMQAOptions.Type.ERR_GRAPHIC_AREA.getValidation());
			err.setType(BMQAOptions.Type.ERR_GRAPHIC_AREA.getType());
			err.setItem(BMQAOptions.Type.ERR_GRAPHIC_AREA.getItem());
			err.setName(BMQAOptions.Type.ERR_GRAPHIC_AREA.getName());
			return err;
		} else {
			return null;
		}
	}

	public BasicErrorFeature validateSmallLength(DTFeature feature, OptionTolerance tolerance) {

		SimpleFeature sf = feature.getSimefeature();
		List<AttributeFilter> filters = feature.getFilter();
		boolean isTrue = false;

		if (filters != null) {
			isTrue = FeatureFilter.filter(sf, filters);
		} else {
			isTrue = true;
		}
		boolean isError = false;
		Geometry geom = (Geometry) sf.getDefaultGeometry();
		Double value = tolerance.getValue();
		String conditon = tolerance.getCondition();

		CoordinateReferenceSystem dataCRS = sf.getFeatureType().getCoordinateReferenceSystem();
		CoordinateReferenceSystem worldCRS;
		MathTransform transform;
		try {
			if (isTrue) {
				if (geom.getGeometryType().equals("MultiLineString") || geom.getGeometryType().equals("LineString")) {
					if (dataCRS != null) {
						// crs transform
						worldCRS = CRS.decode("EPSG:32652");
						transform = CRS.findMathTransform(dataCRS, worldCRS, true);
						for (int i = 0; i < geom.getNumGeometries(); i++) {
							Geometry g = JTS.transform(geom.getGeometryN(i), transform);
							double geomArea = g.getLength();
							if (conditon.equals("over")) {
								if (geomArea < value) {
									isError = true;
								}
							} else if (conditon.equals("under")) {
								if (geomArea > value) {
									isError = true;
								}
							} else if (conditon.equals("equal")) {
								if (geomArea != value) {
									isError = true;
								}
							}
						}
					} else {
						for (int i = 0; i < geom.getNumGeometries(); i++) {
							double geomArea = geom.getGeometryN(i).getLength();
							if (conditon.equals("over")) {
								if (geomArea < value) {
									isError = true;
								}
							} else if (conditon.equals("under")) {
								if (geomArea > value) {
									isError = true;
								}
							} else if (conditon.equals("equal")) {
								if (geomArea != value) {
									isError = true;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isError) {
			Geometry errPt = null;
			try {
				errPt = geom.getInteriorPoint();
			} catch (TopologyException e) {
				Coordinate[] coors = geom.getCoordinates();
				errPt = new GeometryFactory().createPoint(coors[0]);
			}
			BasicErrorFeature err = new BasicErrorFeature();
			err.setErrPoint(errPt);
			err.setGeomStr(geom.toString());
			err.setLayerID(feature.getLayerID());
			err.setValidation(BMQAOptions.Type.ERR_GRAPHIC_Length.getValidation());
			err.setType(BMQAOptions.Type.ERR_GRAPHIC_Length.getType());
			err.setItem(BMQAOptions.Type.ERR_GRAPHIC_Length.getItem());
			err.setName(BMQAOptions.Type.ERR_GRAPHIC_Length.getName());
			return err;
		} else {
			return null;
		}
	}

	public BasicErrorFeature validateEntityDuplicated(DTFeature feature, DTFeature reFeature) {

		SimpleFeature sf = feature.getSimefeature();
		SimpleFeature reSf = reFeature.getSimefeature();

		boolean isTrue = false;
		List<AttributeFilter> filters = feature.getFilter();
		if (filters != null) {
			isTrue = FeatureFilter.filter(sf, filters);
		} else {
			isTrue = true;
		}
		List<AttributeFilter> reFilters = reFeature.getFilter();
		if (reFilters != null) {
			isTrue = FeatureFilter.filter(reSf, reFilters);
		} else {
			isTrue = true;
		}
		if (isTrue) {
			Geometry geom = (Geometry) sf.getDefaultGeometry();
			Geometry reGeom = (Geometry) reSf.getDefaultGeometry();
			// geom 비교
			if (geom.equals(reGeom)) {
				Geometry errPt = null;
				try {
					errPt = geom.getInteriorPoint();
				} catch (TopologyException e) {
					Coordinate[] coors = geom.getCoordinates();
					errPt = new GeometryFactory().createPoint(coors[0]);
				}
				BasicErrorFeature err = new BasicErrorFeature();
				err.setErrPoint(errPt);
				err.setGeomStr(geom.toString());
				err.setLayerID(feature.getLayerID());
				err.setValidation(BMQAOptions.Type.ERR_GRAPHIC_ET_DUPLICATED.getValidation());
				err.setType(BMQAOptions.Type.ERR_GRAPHIC_ET_DUPLICATED.getType());
				err.setItem(BMQAOptions.Type.ERR_GRAPHIC_ET_DUPLICATED.getItem());
				err.setName(BMQAOptions.Type.ERR_GRAPHIC_ET_DUPLICATED.getName());
				return err;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public List<BasicErrorFeature> validatePointDuplicated(DTFeature feature) {

		SimpleFeature sf = feature.getSimefeature();
		boolean isTrue = false;
		List<AttributeFilter> filters = feature.getFilter();

		if (filters != null) {
			isTrue = FeatureFilter.filter(sf, filters);
		} else {
			isTrue = true;
		}

		List<BasicErrorFeature> errFeatures = new ArrayList<BasicErrorFeature>();
		if (isTrue) {
			Geometry geom = (Geometry) sf.getDefaultGeometry();
			String layerID = feature.getLayerID();
			int numGeom = geom.getNumGeometries();
			for (int i = 0; i < numGeom; i++) {
				Geometry singleGeom = geom.getGeometryN(i);
				if (singleGeom instanceof LineString) {
					LineString lineString = (LineString) singleGeom;
					errFeatures.addAll(pointDuplicated(lineString.getCoordinates(), geom, layerID));
				}
				if (singleGeom instanceof Polygon) {
					Polygon polygon = (Polygon) singleGeom;
					LineString exteriorRing = polygon.getExteriorRing();
					errFeatures.addAll(pointDuplicated(exteriorRing.getCoordinates(), geom, layerID));
					int numInnerRings = polygon.getNumInteriorRing();
					for (int in = 0; in < numInnerRings; in++) {
						LineString innerRing = polygon.getInteriorRingN(in);
						errFeatures.addAll(pointDuplicated(innerRing.getCoordinates(), geom, layerID));
					}
				}
			}
		}
		if (errFeatures.size() > 0) {
			return errFeatures;
		} else {
			return null;
		}
	}

	private List<BasicErrorFeature> pointDuplicated(Coordinate[] coors, Geometry geom, String layerID) {

		List<BasicErrorFeature> errFeatures = new ArrayList<BasicErrorFeature>();
		String geomStr = geom.toString();
		int coorLength = coors.length;
		if (coorLength == 2) {
			Coordinate coor0 = coors[0];
			Coordinate coor1 = coors[1];
			if (coor0.equals3D(coor1)) {
				Geometry errPt = new GeometryFactory().createPoint(coor1);
				BasicErrorFeature err = new BasicErrorFeature();
				err.setErrPoint(errPt);
				err.setGeomStr(geomStr);
				err.setLayerID(layerID);
				err.setValidation(BMQAOptions.Type.ERR_GRAPHIC_PT_DUPLICATED.getValidation());
				err.setType(BMQAOptions.Type.ERR_GRAPHIC_PT_DUPLICATED.getType());
				err.setItem(BMQAOptions.Type.ERR_GRAPHIC_PT_DUPLICATED.getItem());
				err.setName(BMQAOptions.Type.ERR_GRAPHIC_PT_DUPLICATED.getName());
				errFeatures.add(err);
			}
		}
		if (coorLength > 3) {
			for (int c = 0; c < coorLength - 1; c++) {
				Coordinate coor0 = coors[c];
				Coordinate coor1 = coors[c + 1];
				if (coor0.equals3D(coor1)) {
					Geometry errPt = new GeometryFactory().createPoint(coor1);
					BasicErrorFeature err = new BasicErrorFeature();
					err.setErrPoint(errPt);
					err.setGeomStr(geomStr);
					err.setLayerID(layerID);
					err.setValidation(BMQAOptions.Type.ERR_GRAPHIC_PT_DUPLICATED.getValidation());
					err.setType(BMQAOptions.Type.ERR_GRAPHIC_PT_DUPLICATED.getType());
					err.setItem(BMQAOptions.Type.ERR_GRAPHIC_PT_DUPLICATED.getItem());
					err.setName(BMQAOptions.Type.ERR_GRAPHIC_PT_DUPLICATED.getName());
					errFeatures.add(err);
				}
			}
		}
		return errFeatures;
	}

	public List<BasicErrorFeature> validateSelfEntity(DTFeature feature, DTFeature reFeature,
			OptionTolerance tolerance) {

		boolean isTrue = false;
		SimpleFeature sf = feature.getSimefeature();
		List<AttributeFilter> filters = feature.getFilter();
		if (filters != null) {
			isTrue = FeatureFilter.filter(sf, filters);
		} else {
			isTrue = true;
		}
		// relation filter
		SimpleFeature reSf = reFeature.getSimefeature();

		// System.out.println(sf.getID() + " - " + reSf.getID());

		List<AttributeFilter> refilters = reFeature.getFilter();
		if (refilters != null) {
			isTrue = FeatureFilter.filter(reSf, refilters);
		} else {
			isTrue = true;
		}
		Geometry geom = (Geometry) sf.getDefaultGeometry();
		Geometry reGeom = (Geometry) reSf.getDefaultGeometry();

		if (geom == null || reGeom == null) {
			return null;
		}

		List<BasicErrorFeature> errFeatures = new ArrayList<BasicErrorFeature>();
		GeometryFactory geometryFactory = new GeometryFactory();
		if (isTrue) {
			String geomType = geom.getGeometryType();
			Geometry returnGeom = null;
			Double value = null;
			String condition = null;
			if (tolerance == null) {
				value = 0.0;
				condition = "equal";
			} else {
				value = tolerance.getValue();
				condition = tolerance.getCondition();
			}
			try {
				if (geomType.equals("Point") || geomType.equals("MultiPoint")) {
					returnGeom = selfEntityPoint(geom, reGeom);
				}
				if (geomType.equals("LineString") || geomType.equals("MultiLineString")) {
					returnGeom = selfEntityLineString(geom, reGeom, value, condition);
				}
				if (geomType.equals("Polygon") || geomType.equals("MultiPolygon")) {
					returnGeom = selfEntityPolygon(geom, reGeom, value, condition);
				}
			} catch (TopologyException e) {
				Coordinate[] coors = geom.getCoordinates();
				returnGeom = new GeometryFactory().createPoint(coors[0]);
			}
			if (returnGeom != null && !returnGeom.isEmpty()) {

				String geomStr = geom.toString();
				String typeName = feature.getTypeName();
				String retypeName = reFeature.getTypeName();
				String errName = typeName + "/" + retypeName;

				String returnGeomType = returnGeom.getGeometryType().toUpperCase();
				if (returnGeomType.equals("LINESTRING")) {
					if (returnGeom.getLength() == 0.0 || returnGeom.getLength() == 0) {
						Coordinate[] coordinates = returnGeom.getCoordinates();
						Point startPoint = geometryFactory.createPoint(coordinates[0]);
						BasicErrorFeature err = new BasicErrorFeature();
						err.setErrPoint(startPoint);
						err.setGeomStr(geomStr);
						err.setLayerID(typeName);
						err.setValidation(BMQAOptions.Type.ERR_GRAPHIC_SELFENTITY.getValidation());
						err.setType(BMQAOptions.Type.ERR_GRAPHIC_SELFENTITY.getType());
						err.setItem(BMQAOptions.Type.ERR_GRAPHIC_SELFENTITY.getItem());
						err.setName(errName + BMQAOptions.Type.ERR_GRAPHIC_SELFENTITY.getName());
						errFeatures.add(err);
					} else {
						BasicErrorFeature err = new BasicErrorFeature();
						err.setErrPoint(returnGeom.getInteriorPoint());
						err.setGeomStr(geomStr);
						err.setLayerID(typeName);
						err.setValidation(BMQAOptions.Type.ERR_GRAPHIC_SELFENTITY.getValidation());
						err.setType(BMQAOptions.Type.ERR_GRAPHIC_SELFENTITY.getType());
						err.setItem(BMQAOptions.Type.ERR_GRAPHIC_SELFENTITY.getItem());
						err.setName(errName + BMQAOptions.Type.ERR_GRAPHIC_SELFENTITY.getName());
						errFeatures.add(err);
					}
				} else {
					for (int i = 0; i < returnGeom.getNumGeometries(); i++) {
						BasicErrorFeature err = new BasicErrorFeature();
						err.setErrPoint(returnGeom.getGeometryN(i).getInteriorPoint());
						err.setGeomStr(geomStr);
						err.setLayerID(typeName);
						err.setValidation(BMQAOptions.Type.ERR_GRAPHIC_SELFENTITY.getValidation());
						err.setType(BMQAOptions.Type.ERR_GRAPHIC_SELFENTITY.getType());
						err.setItem(BMQAOptions.Type.ERR_GRAPHIC_SELFENTITY.getItem());
						err.setName(errName + BMQAOptions.Type.ERR_GRAPHIC_SELFENTITY.getName());
						errFeatures.add(err);
					}
				}
			}
		}
		if (errFeatures.size() > 0) {
			return errFeatures;
		} else {
			return null;
		}
	}

	private Geometry selfEntityPoint(Geometry geometryI, Geometry geometryJ) {

		String typeJ = geometryJ.getGeometryType();
		Geometry returnGeom = null;
		if (typeJ.equals("Point") || typeJ.equals("MultiPoint")) {
			if (geometryI.intersects(geometryJ)) {
				returnGeom = geometryI.intersection(geometryJ);
			}
		}
		if (typeJ.equals("LineString") || typeJ.equals("MultiLineString")) {
			if (geometryI.intersects(geometryJ) || geometryI.touches(geometryJ)) {
				returnGeom = geometryI.intersection(geometryJ);
			}
		}
		if (typeJ.equals("Polygon") || typeJ.equals("MultiPolygon")) {
			if (geometryI.within(geometryJ)) {
				returnGeom = geometryI.intersection(geometryJ);
			}
		}
		return returnGeom;
	}

	private Geometry selfEntityLineString(Geometry geometryI, Geometry geometryJ, double tolerance, String condition) {
		GeometryFactory geometryFactory = new GeometryFactory();
		String typeJ = geometryJ.getGeometryType();
		Geometry returnGeom = null;
		if (typeJ.equals("Point") || typeJ.equals("MultiPoint")) {
			if (geometryI.equals(geometryJ)) {
				returnGeom = geometryI.intersection(geometryJ);
			}
		}

		if (typeJ.equals("LineString") || typeJ.equals("MultiLineString")) {
			if (geometryI.crosses(geometryJ) && !geometryI.equals(geometryJ)) {
				Geometry lineReturnGeom = null;
				lineReturnGeom = geometryI.intersection(geometryJ);
				String upperType = lineReturnGeom.getGeometryType().toString().toUpperCase();

				Coordinate[] coors = geometryI.getCoordinates();
				Coordinate firstCoor = coors[0];
				Coordinate lastCoor = coors[coors.length - 1];
				Point firstPoint = geometryFactory.createPoint(firstCoor);
				Point lastPoint = geometryFactory.createPoint(lastCoor);
				Coordinate[] coorsJ = geometryJ.getCoordinates();
				Coordinate firstCoorJ = coorsJ[0];
				Coordinate lastCoorJ = coorsJ[coorsJ.length - 1];
				Point firstPointJ = geometryFactory.createPoint(firstCoorJ);
				Point lastPointJ = geometryFactory.createPoint(lastCoorJ);

				if (upperType.equals("POINT")) {
					double firstDistance = firstPoint.distance(lineReturnGeom);
					double lastDistance = lastPoint.distance(lineReturnGeom);
					double firstDistanceJ = firstPointJ.distance(lineReturnGeom);
					double lastDistanceJ = lastPointJ.distance(lineReturnGeom);
					if (firstPoint.equals(lastPoint) && !firstPointJ.equals(lastPointJ)) {
						if (condition.equals("over")) {
							if (firstDistanceJ < tolerance && lastDistanceJ < tolerance) {
								returnGeom = lineReturnGeom;
							}
						}
						if (condition.equals("under")) {
							if (firstDistanceJ > tolerance && lastDistanceJ > tolerance) {
								returnGeom = lineReturnGeom;
							}
						}
						if (condition.equals("equal")) {
							if (firstDistanceJ != tolerance && lastDistanceJ != tolerance) {
								returnGeom = lineReturnGeom;
							}
						}

					} else if (!firstPoint.equals(lastPoint) && firstPointJ.equals(lastPointJ)) {
						if (condition.equals("over")) {
							if (firstDistance < tolerance && lastDistance < tolerance) {
								returnGeom = lineReturnGeom;
							}
						}
						if (condition.equals("under")) {
							if (firstDistance > tolerance && lastDistance > tolerance) {
								returnGeom = lineReturnGeom;
							}
						}
						if (condition.equals("equal")) {
							if (firstDistance != tolerance && lastDistance != tolerance) {
								returnGeom = lineReturnGeom;
							}
						}
					} else if (!firstPoint.equals(lastPoint) && !firstPointJ.equals(lastPointJ)) {
						if (condition.equals("over")) {
							if (firstDistance < tolerance && lastDistance < tolerance && firstDistanceJ < tolerance
									&& lastDistanceJ < tolerance) {
								returnGeom = lineReturnGeom;
							}
						}
						if (condition.equals("under")) {
							if (firstDistance > tolerance && lastDistance > tolerance && firstDistanceJ > tolerance
									&& lastDistanceJ > tolerance) {
								returnGeom = lineReturnGeom;
							}
						}
						if (condition.equals("equal")) {
							if (firstDistance != tolerance && lastDistance != tolerance && firstDistanceJ != tolerance
									&& lastDistanceJ != tolerance) {
								returnGeom = lineReturnGeom;
							}
						}
					}
				} else {
					if (firstPoint.equals(lastPoint) && firstPointJ.equals(lastPointJ)) {
						LinearRing ringI = geometryFactory.createLinearRing(coors);
						LinearRing holesI[] = null;
						Polygon polygonI = geometryFactory.createPolygon(ringI, holesI);
						LinearRing ringJ = geometryFactory.createLinearRing(coorsJ);
						LinearRing holesJ[] = null;
						Polygon polygonJ = geometryFactory.createPolygon(ringJ, holesJ);
						Geometry intersectPolygon = polygonI.intersection(polygonJ);
						if (intersectPolygon.getArea() > tolerance) {
							returnGeom = lineReturnGeom;
						}
					} else if (firstPoint.equals(lastPoint) && !firstPointJ.equals(lastPointJ)) {
						List<Point> points = new ArrayList<Point>();
						Coordinate[] lineReturnCoor = lineReturnGeom.getCoordinates();
						for (int i = 0; i < lineReturnCoor.length; i++) {
							Point returnPoint = geometryFactory.createPoint(lineReturnCoor[i]);
							if (returnPoint.distance(firstPointJ) > tolerance
									&& returnPoint.distance(lastPointJ) > tolerance) {
								points.add(returnPoint);
							}
						}
						if (points.size() != 0) {
							Point[] pointList = new Point[points.size()];
							for (int j = 0; j < points.size(); j++) {
								pointList[j] = points.get(j);
							}
							returnGeom = geometryFactory.createMultiPoint(pointList);
						}

					} else if (!firstPoint.equals(lastPoint) && firstPointJ.equals(lastPointJ)) {
						List<Point> points = new ArrayList<Point>();
						Coordinate[] lineReturnCoor = lineReturnGeom.getCoordinates();
						for (int i = 0; i < lineReturnCoor.length; i++) {
							Point returnPoint = geometryFactory.createPoint(lineReturnCoor[i]);
							if (returnPoint.distance(firstPoint) > tolerance
									&& returnPoint.distance(lastPoint) > tolerance) {
								points.add(returnPoint);
							}
						}
						if (points.size() != 0) {
							Point[] pointList = new Point[points.size()];
							for (int j = 0; j < points.size(); j++) {
								pointList[j] = points.get(j);
							}
							returnGeom = geometryFactory.createMultiPoint(pointList);
						}
					}
				}
			}
		}
		if (typeJ.equals("Polygon") || typeJ.equals("MultiPolygon")) {

			if (geometryI.crosses(geometryJ.getBoundary()) || geometryI.within(geometryJ)) {
				Geometry geometry = geometryI.intersection(geometryJ);
				String upperType = geometry.getGeometryType().toUpperCase();
				if (upperType.equals("LINESTRING") || upperType.equals("MULTILINESTRING")) {
					if (geometryI.within(geometryJ)) {
						returnGeom = geometry;
					} else {
						if (geometry.getLength() > tolerance) {
							returnGeom = geometryI.intersection(geometryJ.getBoundary());
						}
					}
				}
			}
		}
		return returnGeom;
	}

	private Geometry selfEntityPolygon(Geometry geometryI, Geometry geometryJ, double tolerance, String condition) {

		String typeJ = geometryJ.getGeometryType();
		Geometry returnGeom = null;
		if (typeJ.equals("Point") || typeJ.equals("MultiPoint")) {
			if (geometryI.within(geometryJ)) {
				returnGeom = geometryI.intersection(geometryJ);
			}
		}
		if (typeJ.equals("LineString") || typeJ.equals("MultiLineString")) {
			Geometry geom = geometryI.intersection(geometryJ);
			String upperType = geom.getGeometryType().toUpperCase();
			if (upperType.equals("LINESTRING") || upperType.equals("MULTILINESTRING")) {
				if (condition.equals("over")) {
					if (geom.getLength() < tolerance) {
						if (geometryI.contains(geometryJ)) {
							returnGeom = geometryI.intersection(geometryJ);
						} else {
							returnGeom = geometryI.intersection(geometryJ.getBoundary());
						}
					}
				}
				if (condition.equals("under")) {
					if (geom.getLength() > tolerance) {
						if (geometryI.contains(geometryJ)) {
							returnGeom = geometryI.intersection(geometryJ);
						} else {
							returnGeom = geometryI.intersection(geometryJ.getBoundary());
						}
					}
				}
				if (condition.equals("equal")) {
					if (geom.getLength() != tolerance) {
						if (geometryI.contains(geometryJ)) {
							returnGeom = geometryI.intersection(geometryJ);
						} else {
							returnGeom = geometryI.getBoundary().intersection(geometryJ);
						}
					}
				}
			}
		}
		if (typeJ.equals("Polygon") || typeJ.equals("MultiPolygon")) {
			if (!geometryI.equals(geometryJ)) {
				if (geometryI.intersects(geometryJ) || geometryI.overlaps(geometryJ) || geometryI.within(geometryJ)
						|| geometryI.contains(geometryJ)) {
					Geometry geometry = geometryI.intersection(geometryJ);
					String upperType = geometry.getGeometryType().toUpperCase();
					if (upperType.equals("POLYGON") || upperType.equals("MULTIPOLYGON")) {
						if (condition.equals("over")) {
							if (geometry.getArea() < tolerance) {
								returnGeom = geometry;
							}
						}
						if (condition.equals("under")) {
							if (geometry.getArea() > tolerance) {
								returnGeom = geometry;
							}
						}
						if (condition.equals("equal")) {
							if (geometry.getArea() != tolerance) {
								returnGeom = geometry;
							}
						}
					}
				}
			}
		}
		return returnGeom;
	}

	public List<BasicErrorFeature> validateConIntersected(DTFeature feature, DTFeature reFeature) {

		List<BasicErrorFeature> errFeatures = new ArrayList<BasicErrorFeature>();

		SimpleFeature sfi = feature.getSimefeature();
		SimpleFeature sfj = reFeature.getSimefeature();
		List<AttributeFilter> filters = feature.getFilter();
		boolean isTrue = false;

		if (filters != null) {
			if (FeatureFilter.filter(sfi, filters)) {

				filters = reFeature.getFilter();

				if (filters != null) {
					isTrue = FeatureFilter.filter(sfj, filters);
				} else {
					isTrue = true;
				}
			} else {
				isTrue = false;
			}
		} else {
			isTrue = true;
		}
		if (isTrue) {
			GeometryFactory geometryFactory = new GeometryFactory();
			Geometry geom = (Geometry) sfi.getDefaultGeometry();
			Geometry reGeom = (Geometry) sfj.getDefaultGeometry();
			if (geom.intersects(reGeom)) {
				String geomStr = geom.toString();
				String layerID = feature.getLayerID();
				Geometry returnGeom = geom.intersection(reGeom);
				Coordinate[] coordinates = returnGeom.getCoordinates();
				for (int i = 0; i < coordinates.length; i++) {
					Coordinate coordinate = coordinates[i];
					Geometry errPoint = geometryFactory.createPoint(coordinate);
					BasicErrorFeature err = new BasicErrorFeature();
					err.setErrPoint(errPoint);
					err.setGeomStr(geomStr);
					err.setLayerID(layerID);
					err.setValidation(BMQAOptions.Type.ERR_GRAPHIC_CON_INTERSECTED.getValidation());
					err.setType(BMQAOptions.Type.ERR_GRAPHIC_CON_INTERSECTED.getType());
					err.setItem(BMQAOptions.Type.ERR_GRAPHIC_CON_INTERSECTED.getItem());
					err.setName(BMQAOptions.Type.ERR_GRAPHIC_CON_INTERSECTED.getName());
					errFeatures.add(err);
				}
				return errFeatures;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public List<BasicErrorFeature> validateConOverDegree(DTFeature feature, OptionTolerance tolerance) {

		List<BasicErrorFeature> errFeatures = new ArrayList<BasicErrorFeature>();

		SimpleFeature sf = feature.getSimefeature();
		List<AttributeFilter> filters = feature.getFilter();
		boolean isTrue = false;

		if (filters != null) {
			isTrue = FeatureFilter.filter(sf, filters);
		} else {
			isTrue = true;
		}
		if (isTrue) {
			Double value = tolerance.getValue();
			String conditon = tolerance.getCondition();
			Geometry geom = (Geometry) sf.getDefaultGeometry();
			String geomStr = geom.toString();
			String layerID = feature.getLayerID();
			Coordinate[] coordinates = geom.getCoordinates();
			int coorSize = coordinates.length;
			for (int i = 0; i < coorSize - 2; i++) {
				boolean isError = false;
				Coordinate a = coordinates[i];
				Coordinate b = coordinates[i + 1];
				Coordinate c = coordinates[i + 2];
				if (!a.equals2D(b) && !b.equals2D(c) && !c.equals2D(a)) {
					double angle = Angle.toDegrees(Angle.angleBetween(a, b, c));
					if (conditon.equals("over")) {
						if (angle < value) {
							isError = true;
						}
					} else if (conditon.equals("under")) {
						if (angle > value) {
							isError = true;
						}
					} else if (conditon.equals("equal")) {
						if (angle != value) {
							isError = true;
						}
					}
					if (isError) {
						GeometryFactory factory = new GeometryFactory();
						Point errPoint = factory.createPoint(b);
						BasicErrorFeature err = new BasicErrorFeature();
						err.setErrPoint(errPoint);
						err.setGeomStr(geomStr);
						err.setLayerID(layerID);
						err.setValidation(BMQAOptions.Type.ERR_GRAPHIC_CON_OVERDEGREE.getValidation());
						err.setType(BMQAOptions.Type.ERR_GRAPHIC_CON_OVERDEGREE.getType());
						err.setItem(BMQAOptions.Type.ERR_GRAPHIC_CON_OVERDEGREE.getItem());
						err.setName(BMQAOptions.Type.ERR_GRAPHIC_CON_OVERDEGREE.getName());
						errFeatures.add(err);
					}
				}
			}
		}
		if (errFeatures.size() != 0) {
			return errFeatures;
		} else {
			return null;
		}
	}

	public List<BasicErrorFeature> validateNodeMiss(DTFeature feature, SimpleFeatureCollection sfc, DTLayer reLayer,
			OptionTolerance tolerance) {

		SimpleFeature sf = feature.getSimefeature();
		if (sf.getAttribute("NF_ID").toString().equals("aaa")) {
			System.out.println(sf.getAttribute("NF_ID").toString());
		}

		boolean isTrue = false;
		List<AttributeFilter> filters = feature.getFilter();
		if (filters != null) {
			isTrue = FeatureFilter.filter(sf, filters);
		} else {
			isTrue = true;
		}
		List<BasicErrorFeature> errFeatures = new ArrayList<BasicErrorFeature>();
		if (isTrue) {
			OptionFilter relationFilter = reLayer.getFilter();
			List<AttributeFilter> relationConditions = null;
			if (relationFilter != null) {
				relationConditions = relationFilter.getFilter();
			}
			Double value = tolerance.getValue();
			String conditon = tolerance.getCondition();

			Geometry geom = (Geometry) sf.getDefaultGeometry();
			String geomStr = geom.toString();
			String layerID = feature.getLayerID();
			String typeName = feature.getTypeName();

			Coordinate[] tCoors = geom.getCoordinates();
			Coordinate tFirCoor = tCoors[0];
			Coordinate tLasCoor = tCoors[tCoors.length - 1];

			GeometryFactory factory = new GeometryFactory();
			Geometry firPt = factory.createPoint(tFirCoor);
			Geometry lasPt = factory.createPoint(tLasCoor);

			boolean firTrue = false;
			boolean lasTrue = false;
			boolean firInter = true;
			boolean lasInter = true;
			boolean notInter = false;

			SimpleFeatureCollection relationSfc = reLayer.getSimpleFeatureCollection();
			SimpleFeatureIterator rIterator = relationSfc.features();
			while (rIterator.hasNext()) {
				SimpleFeature reSf = rIterator.next();
				if (FeatureFilter.filter(reSf, relationConditions)) {
					Geometry rGeom = (Geometry) reSf.getDefaultGeometry();
					if (rGeom == null) {
						continue;
					}
					String rGeomType = rGeom.getGeometryType();
					if (!rGeomType.equals("Polygon") && !rGeomType.equals("MultiPolygon")) {
						continue;
					}
					if (!rGeom.intersects(geom)) {
						notInter = true;
					} else {
						notInter = false;
						Geometry boundary = rGeom.getBoundary();
						firInter = rGeom.intersects(firPt);
						if (boundary.intersects(firPt.buffer(value))) {
							firTrue = true;
						}
						lasInter = rGeom.intersects(lasPt);
						if (boundary.intersects(lasPt.buffer(value))) {
							lasTrue = true;
						}
						break;
					}
				}
			}
			rIterator.close();
			if (notInter) {
				return null;
			}
			if (firTrue && lasTrue) {
				return null;
			} else {
				// nodemiss
				String featureID = sf.getID();
				boolean firErr = true;
				boolean lasErr = true;
				SimpleFeatureIterator sfIter = sfc.features();
				while (sfIter.hasNext()) {
					SimpleFeature tSimpleFeature = sfIter.next();
					if (featureID.equals(tSimpleFeature.getID())) {
						continue;
					}
					Geometry selfGeom = (Geometry) tSimpleFeature.getDefaultGeometry();
					if (!firTrue) {
						if (conditon.equals("over")) {
							if (Math.abs(firPt.distance(selfGeom)) > value) {
								firErr = false;
							}
						} else if (conditon.equals("under")) {
							if (Math.abs(firPt.distance(selfGeom)) < value) {
								firErr = false;
							}
						} else if (conditon.equals("equal")) {
							if (Math.abs(firPt.distance(selfGeom)) == value) {
								firErr = false;
							}
						}
					}
					if (!lasTrue) {
						if (conditon.equals("over")) {
							if (Math.abs(lasPt.distance(selfGeom)) > value) {
								lasErr = false;
							}
						} else if (conditon.equals("under")) {
							if (Math.abs(lasPt.distance(selfGeom)) < value) {
								lasErr = false;
							}
						} else if (conditon.equals("equal")) {
							if (Math.abs(lasPt.distance(selfGeom)) == value) {
								lasErr = false;
							}
						}
					}
				}
				sfIter.close();
				if (!firTrue && firErr && firInter) {
					BasicErrorFeature err = new BasicErrorFeature();
					err.setErrPoint(firPt);
					err.setGeomStr(geomStr);
					err.setLayerID(layerID);
					err.setValidation(BMQAOptions.Type.ERR_GRAPHIC_CENTERLINE_MISS.getValidation());
					err.setType(BMQAOptions.Type.ERR_GRAPHIC_CENTERLINE_MISS.getType());
					err.setItem(BMQAOptions.Type.ERR_GRAPHIC_CENTERLINE_MISS.getItem());
					err.setName(typeName + BMQAOptions.Type.ERR_GRAPHIC_CENTERLINE_MISS.getName());
					errFeatures.add(err);
				}
				if (!lasTrue && lasErr && lasInter) {
					BasicErrorFeature err = new BasicErrorFeature();
					err.setErrPoint(lasPt);
					err.setGeomStr(geomStr);
					err.setLayerID(layerID);
					err.setValidation(BMQAOptions.Type.ERR_GRAPHIC_CENTERLINE_MISS.getValidation());
					err.setType(BMQAOptions.Type.ERR_GRAPHIC_CENTERLINE_MISS.getType());
					err.setItem(BMQAOptions.Type.ERR_GRAPHIC_CENTERLINE_MISS.getItem());
					err.setName(typeName + BMQAOptions.Type.ERR_GRAPHIC_CENTERLINE_MISS.getName());
					errFeatures.add(err);
				}
				if (errFeatures.size() > 0) {
					return errFeatures;
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}
}
