package com.jonathanbravo.taxicosto.mapa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class recorridoJSONParser {

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> ruta_lista = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray rutas_json = null;
        JSONArray legs_json = null;
        JSONArray jSteps = null;
        JSONObject distancia_json = null;
        JSONObject duracion_json = null;

        try {

            rutas_json = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<rutas_json.length();i++){
                legs_json = ( (JSONObject)rutas_json.get(i)).getJSONArray("legs");

                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                for(int j=0;j<legs_json.length();j++){

                    /** Getting distance from the json data */
                    distancia_json = ((JSONObject) legs_json.get(j)).getJSONObject("distance");
                    HashMap<String, String> distancia_hm= new HashMap<String, String>();
                    distancia_hm.put("distance", distancia_json.getString("text"));

                    /** Getting duration from the json data */
                    duracion_json = ((JSONObject) legs_json.get(j)).getJSONObject("duration");
                    HashMap<String, String> duracion_hm = new HashMap<String, String>();
                    duracion_hm.put("duration", duracion_json.getString("text"));

                    /** Adding distance object to the path */
                    path.add(distancia_hm);

                    /** Adding duration object to the path */
                    path.add(duracion_hm);

                    jSteps = ( (JSONObject)legs_json.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                }
                ruta_lista.add(path);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return ruta_lista;
    }

    /**
     * Method to decode polyline points
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