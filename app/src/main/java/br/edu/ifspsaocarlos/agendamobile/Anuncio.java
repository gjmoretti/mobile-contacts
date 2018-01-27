package br.edu.ifspsaocarlos.agendamobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Gustavo on 14/03/2015.
 */
public class Anuncio {
    private Context context;
    public Anuncio(Context context) {
        this.context = context;
    }

    public AdRequest requisicaoAnuncio() {

        // Busca os dados do perfil do usuário
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("perfil_agenda", Context.MODE_PRIVATE);
        String sexo = sharedPreferences.getString("sexo", null);

        // Data de Nascimento:
        int ano = sharedPreferences.getInt("ano", 0);
        int mes = sharedPreferences.getInt("mes", 0);
        int dia = sharedPreferences.getInt("dia", 0);

        // https://developer.android.com/reference/com/google/android/gms/ads/AdRequest.html#GENDER_MALE
        int gender = 0; // Unknown gender

        if (sexo != null) {
            if (sexo.equals("Masculino"))
                gender = 1; // Male gender
            else
                gender = 2; // Female gender.
        }

        Log.i("Teste", "Gender: " + gender);

        AdRequest adRequest;
        boolean dataValida;

        // Faz a validação da data armazenada:
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dt = df.parse(dia + "/" + (mes + 1) + "/" + ano);
            Calendar ca = Calendar.getInstance();
            ca.setTime(dt);
            dataValida = true;

        } catch (ParseException e) {
            dataValida = false;
        }

        if (dataValida){
            Log.i("Teste", "Data: " + dia + "/" + (mes + 1) + "/" + ano);

            // Passando a data de nascimento, sexo e localização do usuário:
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("53777639092B3009D9DE46BC5FB91E25")
                    .setGender(gender)
                    .setBirthday(new GregorianCalendar(ano, mes + 1, dia).getTime())
                    .setLocation(userLocation())
                    .build();
        }
        else {
            // Passando o sexo e localização do usuário:
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("53777639092B3009D9DE46BC5FB91E25")
                    .setGender(gender)
                    .setLocation(userLocation())
                    .build();
        }
        return adRequest;
    }

    private Location userLocation() {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria crta = new Criteria();
        crta.setAccuracy(Criteria.ACCURACY_FINE);
        // crta.setAltitudeRequired(true);
        // crta.setBearingRequired(true);
        // crta.setCostAllowed(true);
        // crta.setPowerRequirement(Criteria.POWER_LOW);

        String provider = locationManager.getBestProvider(crta, true);
        // String provider = LocationManager.GPS_PROVIDER;

        Log.i("Teste", "Provider : " + provider);
        Location location = locationManager.getLastKnownLocation(provider);
        Log.i("Teste", "Location : " + location);
        return location;

    }


}
