package exercise;



import com.baidu.mapapi.SDKInitializer;
import com.example.exercise.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	private Button btn1;
	private Button btn2;
	private Button btn3;
	private Button btn4;
	private Button btn5;
	private Button btn6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btn1 = (Button) findViewById(R.id.exerRecord);
		btn2 = (Button) findViewById(R.id.exerciseInfo);
		btn3 = (Button) findViewById(R.id.routeQuery);
		btn4 = (Button) findViewById(R.id.pointElevation);
		btn5 = (Button) findViewById(R.id.lineElevation);
		btn6 = (Button) findViewById(R.id.Button01);
		button();
	}
	private void button(){
		btn1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				startActivity(new Intent(MainActivity.this,ExerciseRecord.class));
			}
		});
		btn2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				startActivity(new Intent(MainActivity.this,ExerciseInfo.class));
			}
		});
		btn3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				startActivity(new Intent(MainActivity.this,RouteQuery.class));
			}
		});
		btn4.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				startActivity(new Intent(MainActivity.this,PointElevation.class));
			}
		});
		btn5.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				startActivity(new Intent(MainActivity.this,LineElevation.class));
			}
		});
		btn6.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				startActivity(new Intent(MainActivity.this,PointElevationTest.class));
			}
		});
		
	}
	

}
