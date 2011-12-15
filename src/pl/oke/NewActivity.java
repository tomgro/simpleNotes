package pl.oke;

import java.util.Date;

import pl.oke.tools.Utils;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class NewActivity extends Activity {
	EditText et = null;
	DatabaseAdapter myDBAdapter = null;
	Note note = null;

	private int NOTIFICATION_ID = 1;
	private Notification notification;
	private NotificationManager notificationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newnote);

		et = (EditText) findViewById(R.id.editText1);
		myDBAdapter = new DatabaseAdapter(getApplicationContext());

		Bundle extras = getIntent().getExtras();
		note = null;
		if (extras != null) {
			note = (Note) extras.getSerializable("note");
			et.setText(note.getContent());
			et.setSelection(note.getContent().length());
		}

		// Stwórz Manager
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// Stwórz powiadomienie
		int ico = R.drawable.btnbgnotnine;
		String notiticationTitle = "Note was saved";
		long whenNotify = System.currentTimeMillis();

		notification = new Notification(ico, notiticationTitle, whenNotify);
		notification.number = 1;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		addNewOrUpdate();
		doNotify();
		notification.number++;
	}

	private void addNewOrUpdate() {
		String content = et.getText().toString();
		long ts = new Date().getTime();
		myDBAdapter.open();

		if (note == null) {
			if (!Utils.isEmpty(content))
				myDBAdapter.insertNote(new Note(content, Long.toString(ts)));
		} else {
			if (Utils.isEmpty(content))
				content = (String) getResources().getString(R.string.empty);
			myDBAdapter.updateNote(Long.parseLong(note.getId()), new Note(
					content, Long.toString(ts)));
		}

		myDBAdapter.close();
	}

	public void doNotify() {
		// Ustaw informacje wydarzenia
		Context context = getApplicationContext();
		String expandedNotificationTitle = "Simple Note message";
		String expandedNotificationText = "Note was saved";
		Intent intent = new Intent(context, EditActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);

		notification.setLatestEventInfo(context, expandedNotificationTitle,
				expandedNotificationText, pendingIntent);

		// Ustaw flagi
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Wyœwietl powiadomienie
		notificationManager.notify(NOTIFICATION_ID, notification);
	}
}
