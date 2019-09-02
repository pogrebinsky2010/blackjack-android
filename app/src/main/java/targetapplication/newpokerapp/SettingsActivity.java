package targetapplication.newpokerapp; /**
 * Created by talaba on 7/17/16.
 */

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Talaba Pogrebinsky
 */

public class SettingsActivity extends PreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref);

    }

}


