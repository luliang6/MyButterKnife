package com.lu.mybutterknife;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.lu.lib_annotation.ButterKnife;
import com.lu.lib_annotations.BindView;


public class MainActivity extends AppCompatActivity {

	@BindView(R.id.tv)
	public TextView mTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		mTv.setText("####");
	}
}
