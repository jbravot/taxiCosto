package com.jonathanbravo.taxicosto.mapa;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.jonathanbravo.taxicosto.R;
import com.jonathanbravo.taxicosto.configuracion.Tarifa;
import com.jonathanbravo.taxicosto.configuracion.Ubicacion;
import com.jonathanbravo.taxicosto.ruta.Ruta;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Recorrido extends FragmentActivity {

    private GoogleMap mapa_recorrido; // Might be null if Google Play services APK is not available.
    ArrayList<LatLng> lista_puntos;
    TextView distancia_text;
    TextView duracion_text;
    TextView valor_estimado_text;
    Ruta recorrido;
    String[] desde_array;
    String[] hasta_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorrido);
        setUpMapIfNeeded();

        distancia_text = (TextView) findViewById(R.id.distanciaText);
        duracion_text = (TextView) findViewById(R.id.tiempoText);
        valor_estimado_text = (TextView) findViewById(R.id.estimadoText);

        lista_puntos = new ArrayList<LatLng>();
        SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapa_recorrido = fm.getMap();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String recorido_string = extras.getString("recorrido");
            String[] recorrido_array = recorido_string.split(" ");
            desde_array = recorrido_array[0].split(",");
            hasta_array = recorrido_array[1].split(",");
            LatLng desde_tmp = new LatLng(Double.parseDouble(desde_array[0]), Double.parseDouble(desde_array[1]));
            LatLng hasta_tmp = new LatLng(Double.parseDouble(hasta_array[0]), Double.parseDouble(hasta_array[1]));

            lista_puntos.add(desde_tmp);
            addPunto( desde_tmp );

            lista_puntos.add( hasta_tmp);
            addPunto( hasta_tmp );

            Toast.makeText(getApplicationContext(), recorido_string, Toast.LENGTH_SHORT).show();
        }

        if(mapa_recorrido != null) {

            mapa_recorrido.setTrafficEnabled(true);

            //mapa_recorrido.setMyLocationEnabled(true);

            addRecorrido();

            // Setting onclick event listener for the map
            /*mapa_recorrido.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {

                    // Already two locations
                    if (lista_puntos.size() > 1) {
                        lista_puntos.clear();
                        mapa_recorrido.clear();
                    }

                    // Adding new item to the ArrayList
                    lista_puntos.add(point);

                    // Creating MarkerOptions
                    MarkerOptions options = new MarkerOptions();

                    // Setting the position of the marker
                    options.position(point);

                    /*if (lista_puntos.size() == 1) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    } else if (lista_puntos.size() == 2) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }

                    // Add new marker to the Google Map Android API V2
                    mapa_recorrido.addMarker(options);

                    // Checks, whether start and end locations are captured
                    if (lista_puntos.size() >= 2) {
                        LatLng origin = lista_puntos.get(0);
                        LatLng dest = lista_puntos.get(1);

                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin, dest);

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    }
                }
            });*/
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mapa_recorrido} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mapa_recorrido == null) {
            // Try to obtain the map from the SupportMapFragment.
            mapa_recorrido = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mapa_recorrido != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mapa_recorrido} is not null.
     */
    private void setUpMap() {
        mapa_recorrido.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        Ubicacion ubicacion = new Ubicacion();

        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        String sensor = "sensor=false";
        String region = "region="+ubicacion.getPais_codigo();
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+region;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                recorridoJSONParser parser = new recorridoJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if(result.size()<1){
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){    // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            distancia_text.setText( distance );
            duracion_text.setText("(" + duration + ")");

            Tarifa tarifa = new Tarifa();
            Float distancia =  Float.parseFloat(distance);
            Float valor = (distancia * Float.parseFloat("0.26")) +Float.parseFloat("0.35");
            valor_estimado_text.setText("Valor estimado $" + Float.toString(valor));

            // Drawing polyline in the Google Map for the i-th route
            mapa_recorrido.addPolyline(lineOptions);
        }
    }

    public void addPunto( LatLng point ){
        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(point);

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        if (lista_puntos.size() == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            //mapa_recorrido.moveCamera(CameraUpdateFactory.newLatLng(point));
        } else if (lista_puntos.size() == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        // Add new marker to the Google Map Android API V2
        mapa_recorrido.addMarker(options);
    }

    public void addRecorrido(){
        LatLng origin = lista_puntos.get(0);
        LatLng dest = lista_puntos.get(1);

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }
}
