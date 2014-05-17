package com.example.navigation;

import tools.AutoCompleteDirectionsActivityAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

public class AutoCompleteDirectionsActivity extends FragmentActivity {

	public static final int RESULT_CODE = 123;
	private AutoCompleteTextView from = null;
	private AutoCompleteTextView to = null;
	public static int travelling_mode;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.directions_input);

		Button btnLoadDirections = (Button) findViewById(R.id.load_directions);
		from = (AutoCompleteTextView) findViewById(R.id.from);
		to = (AutoCompleteTextView) findViewById(R.id.to);

		from.setAdapter(new AutoCompleteDirectionsActivityAdapter(this,
				android.R.layout.simple_dropdown_item_1line));
		to.setAdapter(new AutoCompleteDirectionsActivityAdapter(this,
				android.R.layout.simple_dropdown_item_1line));

		btnLoadDirections.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!to.getText().toString().equals("")
						&& !from.getText().toString().equals("")) {

					Intent data = new Intent();
					data.putExtra("from", from.getText().toString());
					
					data.putExtra("to", to.getText().toString());
					
					travelling_mode=4;
					
					AutoCompleteDirectionsActivity.this.setResult(RESULT_CODE,
							data);
					AutoCompleteDirectionsActivity.this.finish();
				} else {
					Toast.makeText(TabActivity.mainContext,
							"Please, Enter The Destiantion Location",
							Toast.LENGTH_LONG).show();
				}

			}
		});

	}

}