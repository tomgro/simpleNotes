package pl.oke;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
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
	boolean list = false;
	EditActivity self = this;
	static final private int ALERT_DIALOG_BUTTONS = 2;
	Note selectedNote = null;

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
			if (sdel.equals("list"))
				list = true;
		}

		if (del)
			setTitle("Delete");
		if (list) {
			setTitle("List");

		}
		registerForContextMenu(noteListView);

		noteListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {

				if (del) {
					myDBAdapter.deleteNote(Long.parseLong(notes.get(pos)
							.getId()));
					// self.finish();
					refreshView();
				} else if (list) {
					// TODO
				} else { // edit
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

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case R.id.cm_edit:
			Intent i1 = new Intent(EditActivity.this, NewActivity.class);
			i1.putExtra("note", (Serializable) notes.get(info.position));
			startActivity(i1);
			// Toast.makeText(getApplicationContext(),
			// "Opcja item01 na elemencie: " + shortcuts.get(info.position),
			// Toast.LENGTH_LONG).show();
			break;

		case R.id.cm_delete:
			// item.setOnMenuItemClickListener(self.onCreateDialog(ALERT_DIALOG_BUTTONS));

			showDialog(ALERT_DIALOG_BUTTONS);
			// onCreateDialog(ALERT_DIALOG_BUTTONS);
			selectedNote = notes.get(info.position);

			// Toast.makeText(getApplicationContext(),
			// "Opcja item02 na elemencie: " + shortcuts.get(info.position),
			// Toast.LENGTH_LONG).show();
			break;

		default:
			break;
		}
		return true;
	}

	/*
	 * private OnClickListener listener = new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { if(v ==
	 * (Button)findViewById(R.id.btnNewAlertDialogButton))
	 * showDialog(ALERT_DIALOG_BUTTONS);
	 * 
	 * } };
	 */

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {

		case ALERT_DIALOG_BUTTONS:
			// Okno dialogowe z przyciskami
			builder.setTitle("Delete note");
			builder.setMessage("Are you sure?");
			builder.setCancelable(false);

			builder.setPositiveButton("Yes", new Dialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					myDBAdapter.deleteNote(Long.parseLong(selectedNote.getId()));

					refreshView();
				}
			});
			builder.setNegativeButton("No", new Dialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {

				}
			});

			dialog = builder.create();
			break;

		default:
			dialog = null;
			break;
		}
		return dialog;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

		menu.setHeaderTitle(shortcuts.get(info.position));

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshView();
	}

	public void refreshView() {
		// TODO Auto-generated method stub
		fillNotesList();
		fillListView();
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

	List<String> shortcuts = null;

	private void fillListView() {
		shortcuts = new ArrayList<String>();
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
