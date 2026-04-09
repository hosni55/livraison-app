package com.supervision.livraison.activity;

import android.os.Bundle;
import android.widget.TextView;
import com.supervision.livraison.R;
import com.supervision.livraison.util.SessionManager;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SessionManager sessionManager = new SessionManager(this);
        
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
        
        TextView tvName = findViewById(R.id.tv_profile_name);
        TextView tvRole = findViewById(R.id.tv_profile_role);
        
        tvName.setText(sessionManager.getUserName());
        tvRole.setText(sessionManager.getRole());
    }
}
