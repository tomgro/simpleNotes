package pl.oke;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {
	private static final String DB_NAME = "database.db";
	private static final String DB_TABLE = "notes";
	private static final int DB_VERSION = 1;
	public static final String KEY_ID = "_id";
	public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
	public static final int CONTENT_COLUMN = 1;
	public static final int LASTMOD_COLUMN = 1;

	public static final String KEY_CONTENT = "content";
	public static final String CONTENT_OPTIONS = "TEXT NOT NULL";

	public static final String KEY_LASTMOD = "lastMod";
	public static final String LASTMOD_OPTIONS = "DATETIME NOT NULL";

	private static final String DB_CREATE = "create table " + DB_TABLE + " ("
			+ KEY_ID + " " + ID_OPTIONS + ", " +

			KEY_CONTENT + " " + CONTENT_OPTIONS + ", " + KEY_LASTMOD + " "
			+ LASTMOD_OPTIONS + ");";

	// Zmienna do przechowywania bazy danych
	private SQLiteDatabase db;

	// Kontekst aplikacji korzystaj¹cej z bazy
	private final Context context;

	// Helper od otwierania i aktualizacji bazy danych
	private DatabaseHelper myDatabaseHelper;

	// C-tor
	public DatabaseAdapter(Context _context) {
		context = _context;
		myDatabaseHelper = new DatabaseHelper(_context, DB_NAME, null,
				DB_VERSION);
	}

	// Otwieramy po³¹czenie z baz¹ danych
	public DatabaseAdapter open() {
		db = myDatabaseHelper.getWritableDatabase();
		return this;
	}

	// Zamykamy po³¹czenie z baz¹ danych
	public void close() {
		db.close();
	}

	public long insertNote(Note note) {
		// Tworzymy obiekt nowego "wiersza"
		ContentValues newNote = new ContentValues();
		// Wype³niamy wszystkie pola wiersza
		newNote.put(KEY_CONTENT, note.getContent());
		newNote.put(KEY_LASTMOD, note.getLastMod());
		// Wstawiamy wiersz do bazy
		return db.insert(DB_TABLE, null, newNote);
	}

	public boolean updateNote(long _index, Note note) {
		Log.d("update", "update " + _index + " content " +note.getContent());
		// Warunek wstawiany do klauzuli WHERE
		String where = KEY_ID + "=" + _index;
		// Tak samo jak przy metodzie insert
		ContentValues newNote = new ContentValues();
		newNote.put(KEY_CONTENT, note.getContent());
		newNote.put(KEY_LASTMOD, note.getLastMod());
		// Aktualizujemy dane wiersza zgodnego ze zmienn¹ where
		return db.update(DB_TABLE, newNote, where, null) > 0;
	}

	public boolean deleteNote(long _index) {
		String where = KEY_ID + "=" + _index;
		return db.delete(DB_TABLE, where, null) > 0;
	}

	public void deleteAll() {
		db.delete(DB_TABLE, "1", null);
	}

	// Pobieranie wszystki wpisów w postaci obiektu Cursor
	public Cursor getAllEntries() {
		String[] columns = { KEY_ID, KEY_CONTENT, KEY_LASTMOD };
		String order=KEY_LASTMOD + " DESC";
		return db.query(DB_TABLE, columns, null, null, null, null, order);
	}

	// Metoda pobieraj¹ca pojedynczy wpis zgodny z indeksem
	public Note getEntry(long _index) {
		String[] columns = { KEY_ID, KEY_CONTENT, KEY_LASTMOD };
		String where = "KEY_ID=" + _index;
		Cursor cursor = db.query(true, DB_TABLE, columns, where, null, null,
				null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}

		Note note = new Note(cursor.getString(cursor
				.getColumnIndex(KEY_ID)),
				cursor.getString(cursor
				.getColumnIndex(KEY_CONTENT)), cursor.getString(cursor
				.getColumnIndex(KEY_LASTMOD)));

		return note;
	}

	// Klasa helpera do otwierania i aktualizacji bazy danych
	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DB_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVer, int newVer) {
			_db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
			onCreate(_db);

			Log.w("ListView DatabaseAdapter", "Aktualizacja bazy z wersji "
					+ oldVer + " do " + newVer
					+ ". Wszystkie dane zostan¹ utracone.");
		}
	}
}
