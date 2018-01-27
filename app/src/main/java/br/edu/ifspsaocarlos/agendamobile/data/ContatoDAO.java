package br.edu.ifspsaocarlos.agendamobile.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import br.edu.ifspsaocarlos.agendamobile.model.Contato;


public class ContatoDAO {
	private Context context;  
	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;

	public ContatoDAO(Context context) {
		this.context = context;
	}

	public void open() throws SQLException {
		dbHelper = new SQLiteHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
		database.close();
	}

	public List<Contato> buscaTodosContatos() {

		List<Contato> contacts = new ArrayList<Contato>();

		Cursor cursor = database.query(SQLiteHelper.DATABASE_TABLE, new String[] { SQLiteHelper.KEY_ID, SQLiteHelper.KEY_NAME, SQLiteHelper.KEY_FONE, SQLiteHelper.KEY_EMAIL}, null, null, null, null, SQLiteHelper.KEY_NAME);

		if (cursor!=null)
		{
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Contato contato = new Contato();
				contato.setId(cursor.getInt(0));
				// contato.setId(cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_ID)));
				
				contato.setNome(cursor.getString(1));
				// contato.setId(cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_NAME)));
				
				contato.setFone(cursor.getString(2));
				contato.setEmail(cursor.getString(3));
				contacts.add(contato);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return contacts;	    
	}

	public  List<Contato>  buscaContato(String parametro)
	{

		List<Contato> contacts = new ArrayList<Contato>();
		
		// Cursor cursor = database.query(SQLiteHelper.DATABASE_TABLE, new String[] { SQLiteHelper.KEY_ID, SQLiteHelper.KEY_NAME, SQLiteHelper.KEY_FONE, SQLiteHelper.KEY_EMAIL}, SQLiteHelper.KEY_NAME + " like ? ", new String[] { parametro+"%" }, null, null, SQLiteHelper.KEY_NAME);
		
		StringBuilder sql = new StringBuilder(300);    	
		sql.append(" SELECT id, nome, fone, email, aniversario ");
		sql.append(" FROM contatos ");
		sql.append(" where nome like ('%parametro%') ");
		sql.append(" union ");
		sql.append(" SELECT id, nome, fone, email, aniversario "); 
		sql.append(" FROM contatos ");
		sql.append(" where fone like ('%parametro%')");
		
		String query = sql.toString();		
		query = query.replace("parametro", parametro);
		
		// Log.i("Teste", query);
		
		Cursor cursor = database.rawQuery(query, null);
		
		if (cursor!=null)
		{
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Contato contato = new Contato();
				contato.setId(cursor.getInt(0));
				contato.setNome(cursor.getString(1));
				contato.setFone(cursor.getString(2));
				contato.setEmail(cursor.getString(3));
				contacts.add(contato);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return contacts;	    
	}



	public void updateContact(Contato c) {
		ContentValues updateValues = new ContentValues();
		updateValues.put(SQLiteHelper.KEY_NAME, c.getNome());
		updateValues.put(SQLiteHelper.KEY_FONE, c.getFone());
		updateValues.put(SQLiteHelper.KEY_EMAIL, c.getEmail());
		database.update(SQLiteHelper.DATABASE_TABLE, updateValues, SQLiteHelper.KEY_ID + "=" + c.getId(), null);
	}

	public void createContact( Contato c) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.KEY_NAME, c.getNome());
		values.put(SQLiteHelper.KEY_FONE, c.getFone());
		values.put(SQLiteHelper.KEY_EMAIL, c.getEmail());

		database.insert(SQLiteHelper.DATABASE_TABLE, null, values);
	}

	public void deleteContact(Contato c) {
		database.delete(SQLiteHelper.DATABASE_TABLE, SQLiteHelper.KEY_ID + "="+ c.getId(), null);
	}
	
}
