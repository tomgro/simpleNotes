package pl.oke;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class EditActivity extends Activity {
	Cursor noteCursor;
	DatabaseAdapter myDBAdapter;
	ListView noteListView;
	List<Note> notes = new ArrayList<Note>();
	boolean del = false;
	EditActivity self=this;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editnote);

		noteListView = (ListView) findViewById(R.id.editListView);

		myDBAdapter = new DatabaseAdapter(getApplicationContext());
		myDBAdapter.open();

		fillNotesList();
		fillListView();

		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
			// Log.i( "dd","Extra:" + extras.getString("note") );
			String sdel = extras.getString("action");
			if (sdel.equals("delete"))
				del = true;
		}

		noteListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {

				if (del) {
					myDBAdapter.deleteNote(Long.parseLong(notes.get(pos).getId()));
					self.finish();
				} else {
					Intent i1 = new Intent(EditActivity.this, NewActivity.class);
					i1.putExtra("note", (Serializable) notes.get(pos));
					startActivity(i1);
					noteListView.invalidate();
				}
				// Toast.makeText(getApplicationContext(),
				// notes.get(pos).getContent().toString(),
				// Toast.LENGTH_SHORT).show();
			}
		});

	}

	private void fillNotesList() {
		noteCursor = myDBAdapter.getAllEntries();
		startManagingCursor(noteCursor);
		updateNotesList();
	}

	private void updateNotesList() {
		noteCursor.requery();

		notes.clear();

		if (noteCursor.moveToFirst()) {
			do {
				String id = noteCursor.getString(noteCursor
						.getColumnIndex(DatabaseAdapter.KEY_ID));
				String content = noteCursor.getString(noteCursor
						.getColumnIndex(DatabaseAdapter.KEY_CONTENT));
				String lastMod = noteCursor.getString(noteCursor
						.getColumnIndex(DatabaseAdapter.KEY_LASTMOD));
				Note newNote = new Note(id, content, lastMod);
				notes.add(newNote);
			} while (noteCursor.moveToNext());
		}
	}

	private void fillListView() {
		List<String> shortcuts = new ArrayList<String>();
		for (Note note : notes) {
			String con = note.getContent();
			if (con.length() > 20) {
				con = con.substring(0, 20) + "...";

			}

			shortcuts.add(con);
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getApplicationContext(), android.R.layout.simple_list_item_1,
				shortcuts);
		noteListView.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		myDBAdapter.close();
		super.onDestroy();
	}

}
