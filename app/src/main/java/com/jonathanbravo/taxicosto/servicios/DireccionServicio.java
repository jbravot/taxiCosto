package com.jonathanbravo.taxicosto.servicios;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DireccionServicio {
    private static final String TAG = "LocationAddress";

    public static void getAddressFromLocation(final double latitude, final double longitude, final Context context, final Handler handler) {

        Thread thread = new Thread() {
            @Override
            public void run() {

                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;

                try {
                    List<Address> direcciones_lista = geocoder.getFromLocation(latitude, longitude, 1);

                    if (direcciones_lista != null && direcciones_lista.size() > 0) {
                        Address direccion = direcciones_lista.get(0);
                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i < direccion.getMaxAddressLineIndex(); i++) {
                            sb.append(direccion.getAddressLine(i)).append(", ");
                        }

                        sb.append(direccion.getLocality()).append(", ");
                        //sb.append(direccion.getPostalCode()).append(", ");
                        sb.append(direccion.getCountryName());
                        result = sb.toString();
                    }

                } catch (IOException e) {

                    Log.e(TAG, "Unable connect to Geocoder", e);

                } finally {

                    Message mensaje = Message.obtain();
                    mensaje.setTarget(handler);

                    if (result != null) {
                        mensaje.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        mensaje.setData(bundle);

                    } else {

                        mensaje.what = 1;
                        Bundle bundle = new Bundle();

                        result = "Unable to get address for this lat-long.";

                        bundle.putString("address", result);
                        mensaje.setData(bundle);

                    }

                    mensaje.sendToTarget();
                }
            }
        };

        thread.start();
    }

    public static Address getLocationFromAddress(String strAddress, final Context context) {

        Geocoder geocoder = new Geocoder(context);
        List<Address> address;
        Address location = null;

        try {
            address = geocoder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            try{
                location = address.get(0);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
            }catch(Exception e){
                Log.e("DIRECCIONSERVICIO", "Dirección invalida.");
            }

        }catch (IOException e) {
            e.printStackTrace();
            Log.e("DIRECCIONSERVICIO", "Unable connect to Geocoder", e);
        }

        return location;
    }
}
