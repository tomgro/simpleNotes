package pl.oke;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class And3Activity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		View newnote = findViewById(R.id.new_button);
		newnote.setOnClickListener(this);
		
		View listnote = findViewById(R.id.list_button);
		listnote.setOnClickListener(this);
		
		View editnote = findViewById(R.id.edit_button);
		editnote.setOnClickListener(this);
		
		View delnote = findViewById(R.id.delete_button);
		delnote.setOnClickListener(this);

		View aboutButton = findViewById(R.id.about_button);
		aboutButton.setOnClickListener(this);

		View exit = findViewById(R.id.exit_button);
		exit.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_button:
			Intent i1 = new Intent(this, NewActivity.class);
			
			startActivity(i1);
			break;
		case R.id.list_button:
			Intent i5 = new Intent(this, EditActivity.class);
			i5.putExtra("action", "list");
			startActivity(i5);
			break;
		case R.id.edit_button:
			Intent i2 = new Intent(this, EditActivity.class);
			startActivity(i2);
			break;
		case R.id.delete_button:
			Intent i3 = new Intent(this, EditActivity.class);
			i3.putExtra("action", "delete");
			startActivity(i3);
			break;
		case R.id.about_button:
			Intent i4 = new Intent(this, AboutActivity.class);
			startActivity(i4);
			break;
		case R.id.exit_button:
			this.finish();
			break;

		default:
			break;
		}
	}
}