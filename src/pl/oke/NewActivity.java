package pl.oke;


import java.security.Timestamp;
import java.util.Calendar;
import java.util.Date;

import pl.oke.tools.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NewActivity extends Activity implements OnClickListener {
	EditText et=null;
	DatabaseAdapter myDBAdapter = null;
	Note note=null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newnote);
        
        View cancel = findViewById(R.id.new_cancel);
		cancel.setOnClickListener(this);
		
		View save = findViewById(R.id.new_save);
		save.setOnClickListener(this);
		et = (EditText)findViewById(R.id.editText1);
		myDBAdapter = new DatabaseAdapter(getApplicationContext());
		
		Bundle extras = getIntent().getExtras();
		note=null;
	    if(extras != null){
	        //Log.i( "dd","Extra:" + extras.getString("note") );
	    	note = (Note)extras.getSerializable("note");
	    	et.setText(note.getContent());
	    	et.setSelection(note.getContent().length());
	    }
	}
	
	

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_save:
			String content = et.getText().toString();
			long ts = new Date().getTime();
			myDBAdapter.open();
			
			if(note==null) {
				if(!Utils.isEmpty(content))
					myDBAdapter.insertNote(new Note(content, Long.toString(ts)));
			} else {
				if(Utils.isEmpty(content)) content=(String)getResources().getString(R.string.empty);
				myDBAdapter.updateNote(Long.parseLong(note.getId()), new Note(content, Long.toString(ts)));
			}
		
			myDBAdapter.close();
			this.finish();
			break;
		case R.id.new_cancel:
			this.finish();
			break;

		default:
			break;
		}
	}
}
