package br.edu.ifspsaocarlos.agendamobile.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class SQLiteHelper extends SQLiteOpenHelper{

	public static final String DATABASE_NAME   = "agenda.db";
	public static final String DATABASE_TABLE  = "contatos";
	public static final String KEY_ID          = "id";
	public static final String KEY_NAME        = "nome";
	public static final String KEY_FONE        = "fone";
	public static final String KEY_EMAIL       = "email";
	public static final String KEY_ANIVERSARIO = "aniversario";
	public static final String TAG             = SQLiteHelper.class.getName();
	public static final int DATABASE_VERSION   = 2;
	
	// DB Version 1:
	public static final String DATABASE_VERSION_001 = "CREATE TABLE " + DATABASE_TABLE +" ("+ KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME + " TEXT NOT NULL, " + KEY_FONE + " TEXT, " + KEY_EMAIL + " TEXT);";
	
	// DB Version 2:
	public static final String DATABASE_VERSION_002 = "ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " + KEY_ANIVERSARIO + " TEXT;";	

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}	
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		// Cria o DB:
		database.execSQL(DATABASE_VERSION_001);
		
		// Atualiza para a vers�o 2:
        database.execSQL(DATABASE_VERSION_002);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int 	newVersion) {
		
    	Log.i(TAG, "Vers�o atual do DB: " + Integer.toString(database.getVersion()));    	
    	Log.i(TAG, "Atualizando Banco de Dados da vers�o " + oldVersion + " para " + newVersion);
    	
    	switch (oldVersion) {
		case 1: {
			
			String sql = DATABASE_VERSION_002;   	    	
	        database.execSQL(sql); 
			        
			Log.i(TAG, "Campo " + KEY_ANIVERSARIO + " criado com sucesso");			        

			// Seta a vers�o do BD para vers�o 2:
			database.setVersion(DATABASE_VERSION);
			    
			break;
		}	

		default:
			break;
		}		
	}
}
