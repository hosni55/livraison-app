package com.supervision.livraison.activity;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import com.supervision.livraison.util.LocaleHelper;

/**
 * BaseActivity — All activities inherit from this to ensure locale consistency.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        // Apply the selected locale automatically
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
