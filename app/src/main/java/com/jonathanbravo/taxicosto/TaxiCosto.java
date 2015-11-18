package com.jonathanbravo.taxicosto;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Message;
import android.os.Handler;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;

import android.text.Editable;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jonathanbravo.taxicosto.mapa.Recorrido;
import com.jonathanbravo.taxicosto.ruta.Ruta;
import com.jonathanbravo.taxicosto.servicios.GPSServicio;
import com.jonathanbravo.taxicosto.servicios.LugaresAutocompletar;
import com.jonathanbravo.taxicosto.servicios.DireccionServicio;

public class TaxiCosto extends ActionBarActivity {

    GPSServicio appGPSServicio;
    AutoCompleteTextView autoCompView;
    TextView mi_ubicacion;
    Ruta ruta = new Ruta();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_costo);

        mi_ubicacion = (TextView) findViewById(R.id.desdeText);
        appGPSServicio = new GPSServicio(TaxiCosto.this);

        autoCompView = (AutoCompleteTextView) findViewById(R.id.hastaAutocomplete);
        autoCompView.setAdapter(new LugaresAutocompletar(this, R.layout.list_item));

        Button estimar_recorrido = (Button) findViewById(R.id.estimarButton);
        estimar_recorrido.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Editable tmp = autoCompView.getText();

                if(mi_ubicacion.getText() != null && !mi_ubicacion.getText().toString().isEmpty()){
                    if(tmp.toString() != null && !tmp.toString().isEmpty()){

                        ruta = getDestino(tmp.toString(), ruta);

                        if(ruta !=  null){
                            Intent mapaIntent = new Intent(view.getContext(), Recorrido.class);
                            String recorrido = Double.toString(ruta.getDesde_latitud())+","+Double.toString(ruta.getDesde_longitud())+" "+Double.toString(ruta.getHasta_latitud())+","+Double.toString(ruta.getHasta_longitud());
                            mapaIntent.putExtra("recorrido", recorrido);
                            startActivityForResult(mapaIntent, 0);
                        }

                    }else{
                        Toast.makeText(getApplicationContext(), "Falta su destino", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Falta su ubicación", Toast.LENGTH_SHORT).show();
                }
            }

        });

        ImageButton gpsButton = (ImageButton) findViewById(R.id.gpsButton);
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                /*ruta.setDesde_latitud( -2.1375948 );
                ruta.setDesde_longitud(-79.9106079);
                mi_ubicacion.setText("Mi casa");*/

                Location location = appGPSServicio.getLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    ruta.setDesde_latitud( location.getLatitude() );
                    ruta.setDesde_longitud(location.getLongitude());

                    DireccionServicio direccion_servicio = new DireccionServicio();
                    direccion_servicio.getAddressFromLocation(latitude, longitude, getApplicationContext(), new GeocoderHandler());
                } else {
                    showSettingsAlert();
                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_taxi_costo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                TaxiCosto.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        TaxiCosto.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            mi_ubicacion.setText(locationAddress);
        }
    }

    private Ruta getDestino( String destino, Ruta ruta ){

        DireccionServicio direccion_servicio = new DireccionServicio();
        Address addressLocation = direccion_servicio.getLocationFromAddress(destino, getApplicationContext());

        if(addressLocation != null) {
            double latitude = addressLocation.getLatitude();
            double longitude = addressLocation.getLongitude();

            ruta.setHasta_latitud(addressLocation.getLatitude());
            ruta.setHasta_longitud(addressLocation.getLongitude());

            return ruta;

        }else {
            /*ruta.setHasta_latitud(-2.1136858);
            ruta.setHasta_longitud(-79.9140466);
            return ruta;*/
            showSettingsAlert();
            return null;
        }
    }
}
