package br.edu.ifspsaocarlos.agendamobile;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

import br.edu.ifspsaocarlos.agendamobile.data.ContatoDAO;
import br.edu.ifspsaocarlos.agendamobile.model.Contato;

public class MainActivity extends ListActivity {
    private ContatoDAO cDAO;
    private InterstitialAd iAd;
    private Anuncio anuncio;

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);

        // Foi necessário utilizar o setContentView para poder carregar e exibir o banner na criação da activity:
        setContentView(R.layout.activity_main);

        // Classe que carrega o anuncio:
        anuncio = new Anuncio(this);

        // Monta o Banner:
        AdView mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(anuncio.requisicaoAnuncio());

        // Inicializa iAd (Intersticial):
        iAd = new InterstitialAd(this);
        iAd.setAdUnitId(this.getString(R.string.interstitial_ad_unit_id));

		cDAO = new ContatoDAO(this);
		cDAO.open();
		
		// Copia os contatos do Android para o DB SqLite (apenas para gera��o de massa de dados para testes):
		// insertContacts();
		
		 Intent intent=getIntent();
		 if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			 String query = intent.getStringExtra(SearchManager.QUERY);
		     buildSearchListView(query);
		 }
		 else
			 buildListView();
	}

    @Override
    public void onResume(){
        super.onResume();

        // Carrega o anúncio InterstitialAd (se necessário):

        if (!(iAd.isLoaded())) {
            iAd.loadAd(anuncio.requisicaoAnuncio());
            Log.i("Teste", "Carregamento do anúncio");
        }
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (cDAO != null) {
			cDAO.close();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;

		switch (item.getItemId()) {
		
		case R.id.adicionarContato:
			// Clique no bot�o Adcionar Contato (R.id.adicionarContato)
			i = new Intent(this, DetalheActivity.class);
			startActivityForResult(i, 0);

            // Mostra o banner Interstitial:
            if (iAd.isLoaded()) {
                iAd.show();
            }
			return true;
			
        case R.id.pesqContato:    
			// Clique no bot�o de Pesquisa (R.id.pesqContato)
        	onSearchRequested();
        	return true;

            case R.id.perfil:
                // Clique no bot�o alterar o perfil
                i = new Intent(this, PerfilActivity.class);
                startActivityForResult(i, 0);
                return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}


	public void onListItemClick(ListView l, View v, int position, long id) {
		DisciplinaArrayAdapter adapter = (DisciplinaArrayAdapter) getListAdapter();
		Contato contact = (Contato) adapter.getItem(position);
		Intent i = new Intent(this, DetalheActivity.class);
		i.putExtra("contact",contact);
		startActivityForResult(i, 0);
	}
	
	@Override  
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {  
		super.onCreateContextMenu(menu, v, menuInfo);  
		MenuInflater m = getMenuInflater();  
		m.inflate(R.menu.context_menu, menu);  
	}  

	@Override  
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		DisciplinaArrayAdapter adapter = (DisciplinaArrayAdapter) getListAdapter();
		Contato contact = (Contato) adapter.getItem(info.position);
		switch(item.getItemId()){  
		case R.id.delete_item:  
			cDAO.deleteContact(contact); 
			Toast.makeText(getApplicationContext(), "Removido com sucesso", Toast.LENGTH_SHORT).show();
			buildListView();
			return true;  
		} 
		return super.onContextItemSelected(item);  
	}  

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){ 
			buildListView();	
		}
	}

	private void buildListView() {
		List<Contato> values = cDAO.buscaTodosContatos();
		DisciplinaArrayAdapter adapter = new DisciplinaArrayAdapter(this, values);
		setListAdapter(adapter);
	}
	
	private void buildSearchListView(String query) {
		List<Contato> values = cDAO.buscaContato(query);
		DisciplinaArrayAdapter adapter = new DisciplinaArrayAdapter(this, values);
		adapter = new DisciplinaArrayAdapter(this, values);
		setListAdapter(adapter);
	}
	
	private void insertContacts() {
		
		Contato c;
		ContentResolver cr = getContentResolver();
		
		// Busca os contatos do Android (apenas os que tiverem o campo "Nome" com valor):
		Cursor contatos = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ECLAIR) {
			contatos = cr.query(ContactsContract.Contacts.CONTENT_URI,null, ContactsContract.Contacts.DISPLAY_NAME + " is not null", null, null);
		}
		if (contatos.getCount() > 0) {
			while (contatos.moveToNext()) {
				
				String id   = contatos.getString(contatos.getColumnIndex(ContactsContract.Contacts._ID));
				String name = contatos.getString(contatos.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String fone = null;
				
				// Caso o contato n�o exista, ele deve ser inserido no DB SqLite:
				if (cDAO.buscaContato(name).isEmpty()) {

                    // Caso possuia telefone, executa a query para busc�-los, passando o ID do contato:
					Cursor telefones = null;
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ECLAIR) {
						telefones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);
					}
					while (telefones.moveToNext()) {
                        // O contato pode ter mais de um telefone, mas neste projeto vamos armazenar apenas um dos n�meros:

                        if (telefones.isFirst()) {
                            fone = telefones.getString(telefones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                    }
                    telefones.close();

                    // Adciona na agenda apenas contatos que tenham telefone:

                    if(fone != null) {
                        c = new Contato();
                        c.setNome(name);
                        c.setFone(fone);
                        c.setEmail("");
                        cDAO.createContact(c);
                    }

				}
			}	
		}
	}
}
