package br.edu.ifspsaocarlos.agendamobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import br.edu.ifspsaocarlos.agendamobile.data.ContatoDAO;
import br.edu.ifspsaocarlos.agendamobile.model.Contato;

public class DetalheActivity extends Activity {
	private Contato c;
	private ContatoDAO cDAO;
    private Anuncio anuncio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalhe);

        // Classe que carrega o anuncio:
        anuncio = new Anuncio(this);

        // Banner:
        AdView mAdView = (AdView) findViewById(R.id.smartAdView);
        mAdView.loadAd(anuncio.requisicaoAnuncio());

		if (getIntent().hasExtra("contact")) {
			this.c = (Contato) getIntent().getSerializableExtra("contact");

			EditText nameText = (EditText)findViewById(R.id.editText1);
			nameText.setText(c.getNome());

			EditText foneText = (EditText)findViewById(R.id.editText2);
			foneText.setText(c.getFone());

			EditText emailText = (EditText)findViewById(R.id.editText3);
			emailText.setText(c.getEmail());
		}

		cDAO = new ContatoDAO(this);
		cDAO.open();
	}

//	public void onSaveClick(View v) {
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.detalhe_menu, menu);
		
		// Caso nao seja passado um contato, significa que se trata da insercao de um novo contato.
		// Neste caso, deve-se omitir o botao de remocao do Menu:
		if (this.c == null) {
			MenuItem item = menu.findItem(R.id.RemoverContato);
			item.setVisible(false);
		}
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {	
		
		case R.id.SalvarContato: {
			String name  = ((EditText) findViewById(R.id.editText1)).getText().toString();
			String fone  = ((EditText) findViewById(R.id.editText2)).getText().toString();
			String email = ((EditText) findViewById(R.id.editText3)).getText().toString();

			if (c==null) {
				c = new Contato();
				c.setNome(name);
				c.setFone(fone);
				c.setEmail(email);

				cDAO.createContact(c);
				Toast.makeText(this, "Inclu�do com sucesso",Toast.LENGTH_SHORT).show();
			}
			else {
				c.setNome(name);
				c.setFone(fone);
				c.setEmail(email);

				cDAO.updateContact(c);
				Toast.makeText(this, "Alterado com sucesso", Toast.LENGTH_SHORT).show();
			}

			Intent resultIntent = new Intent();
			setResult(RESULT_OK,resultIntent);     
			finish();
		}
		return true;
			
        case R.id.RemoverContato: {
        	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetalheActivity.this);
            
        	alertDialogBuilder.setTitle("Aten��o");
         	alertDialogBuilder
        	.setMessage("Remover o contato?")
        	.setCancelable(false)
        	.setPositiveButton("Sim",new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog,int id) {
        			
        			cDAO.deleteContact(c);   
        			Intent resultIntent = new Intent();
        			setResult(RESULT_OK,resultIntent);     
        			// Toast.makeText(getApplicationContext(), "Removido com sucesso", Toast.LENGTH_SHORT).show();
        			finish();        			
        		}
        	})
        	.setNegativeButton("N�o",new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog,int id) {
        			dialog.cancel();
        		}
        	});

        	AlertDialog alertDialog = alertDialogBuilder.create();
        	alertDialog.show();    
        }

        return true;
        
		default:
			return super.onOptionsItemSelected(item);
		}
	}	
}
