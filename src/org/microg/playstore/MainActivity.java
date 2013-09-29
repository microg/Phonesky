package org.microg.playstore;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.android.vending.R;
import org.microg.playstore.fragments.DocDetailsFragment;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.empty);
		getSupportFragmentManager().beginTransaction().replace(R.id.container, new DocDetailsFragment("com.nianticproject.ingress")).commit();
	}
}
