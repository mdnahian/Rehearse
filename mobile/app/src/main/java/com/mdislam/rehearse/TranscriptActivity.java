package com.mdislam.rehearse;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by mdnah on 5/21/2017.
 */

public class TranscriptActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript);

        Transcript transcript = (Transcript) getIntent().getSerializableExtra("transcript");

    }
}
