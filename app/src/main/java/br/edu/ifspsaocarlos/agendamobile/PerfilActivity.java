package br.edu.ifspsaocarlos.agendamobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PerfilActivity extends Activity {

    String sexo;
    EditText dataNascimentoText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_perfil);

        // Busca os dados do perfil para serem mostrados na tela:
        SharedPreferences sharedPreferences = PerfilActivity.this.getApplicationContext().getSharedPreferences("perfil_agenda", Context.MODE_PRIVATE);
        sexo = sharedPreferences.getString("sexo", null);

        // Data de Nascimento
        int ano = sharedPreferences.getInt("ano", 0);
        int mes = sharedPreferences.getInt("mes", 0);
        int dia = sharedPreferences.getInt("dia", 0);

        dataNascimentoText = (EditText)findViewById(R.id.editDataNascimento);

        // Posiciona o textView no valor armazenado:
        if (ano > 0 && mes > 0 && dia > 0)
            dataNascimentoText.setText(dia + "/" + (mes + 1) +"/" + ano);

        final Spinner combo = (Spinner) this.findViewById(R.id.spinnerSexo);

        List<String> list = new ArrayList<String>();
        list.add("Masculino");
        list.add("Feminino");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list);
        combo.setAdapter(adapter);

        // Posiciona o spinner no valor armazenado:
        if (sexo != null) {
            if (sexo.equals("Masculino"))
                combo.setSelection(0);
            else
                combo.setSelection(1);
        }

        // Evento Click do Spinner:
        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                sexo = combo.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.perfil_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {	
		
		case R.id.SalvarPerfil: {

            // Obtem a data informada pelo usuário:
            String dataNascimento = String.valueOf(dataNascimentoText.getText());

            // Faz a validação da data:
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date dt = df.parse(dataNascimento);
                Calendar ca = Calendar.getInstance();
                ca.setTime(dt);
                // Log.i("Teste", "Data Formatada: " + ca.toString());

                // Gravar o perfil:
                SharedPreferences.Editor editor = this.getApplicationContext().getSharedPreferences("perfil_agenda", Context.MODE_PRIVATE).edit();
                editor.putString("sexo", sexo);

                editor.putInt("ano", ca.get(ca.YEAR));
                editor.putInt("mes", ca.get(ca.MONTH)); // Obs.: Inicia com ZERO (0-11)
                editor.putInt("dia", ca.get(ca.DAY_OF_MONTH));
                editor.commit();

                Intent resultIntent = new Intent();
                setResult(RESULT_OK,resultIntent);
                finish();

            } catch (ParseException e) {
                Toast.makeText(this, "Informe a data corretamente.", Toast.LENGTH_LONG).show();
            }

		}
		return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}	
}
