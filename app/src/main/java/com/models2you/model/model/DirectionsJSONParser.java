package com.models2you.model.model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DirectionsJSONParser {
	
	/** Receives a JSONObject and returns a list of lists containing latitude and longitude */
	public List<RouteModel> parse(JSONObject jObject){
		List<RouteModel> routeModels = new ArrayList<>();
		try {
			JSONArray jRoutes = jObject.getJSONArray("routes");
			/** Traversing all routes */
			for(int i=0;i<jRoutes.length();i++){			
				JSONObject overviewPolyline = (jRoutes.getJSONObject(i)).getJSONObject("overview_polyline");
                String overviewPolylinePoints = overviewPolyline.getString("points");
                List<LatLng> listOverviewPolylinePoints = decodePoly(overviewPolylinePoints);

				RouteModel routeModel = new RouteModel(0, 0);
                routeModel.addPoints(listOverviewPolylinePoints);

                // get Bound Area from routes
                JSONObject boundJsonObj = jRoutes.getJSONObject(i).optJSONObject("bounds");
                if (boundJsonObj != null) {
                    LatLng northEastLatLng = null, southWestLatLng = null;
                    JSONObject northEastJsonObj = boundJsonObj.optJSONObject("northeast");
                    if (northEastJsonObj != null) {
                        Double northEastLat = northEastJsonObj.optDouble("lat");
                        Double southWestLat = northEastJsonObj.optDouble("lng");
                        northEastLatLng = new LatLng(northEastLat , southWestLat);
                    }
                    JSONObject southWestJsonObj = boundJsonObj.getJSONObject("southwest");
                    if (southWestJsonObj != null) {
                        Double northEastLat = southWestJsonObj.optDouble("lat");
                        Double southWestLat = southWestJsonObj.optDouble("lng");
                        southWestLatLng = new LatLng(northEastLat , southWestLat);
                    }
                    routeModel.addBoundArea(northEastLatLng , southWestLatLng);
                }
				/*
                JSONArray jLegs = (jRoutes.getJSONObject(i)).getJSONArray("legs");
				for(int j=0; j<jLegs.length(); j++){
                    int durationSec = 0;
                    double distanceMeter = 0d;
                    try {
                        JSONObject jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
                        distanceMeter = Double.parseDouble(jDistance.getString("value"));
                        JSONObject jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                        durationSec = Integer.parseInt(jDuration.getString("value"));
                    } catch (Exception ignore) {}

					routeModel = new RouteModel(durationSec, distanceMeter);
					JSONArray jSteps = ((JSONObject)jLegs.get(j)).getJSONArray("steps");

					*//** Traversing all steps *//*
					for(int k=0; k < jSteps.length();k++){
						String polyline = "";
						polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
						List<LatLng> list = decodePoly(polyline);
						routeModel.addPoints(list); //TODO check multiple polylines ?
					}
				}*/
				routeModels.add(routeModel);
                Log.d("TAG", "size " + listOverviewPolylinePoints.size() + " == " + routeModel.getPoints().size());
			}
			
		} catch (JSONException e) {			
			e.printStackTrace();
		} catch (Exception e){}
		
		return routeModels;
	}	
	
	
	/**
	 * Method to decode polyline points 
	 * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java 
	 * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}