package org.androidtown.ictttapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {
    MainActivity main = new MainActivity();
    public void putaimExtra(View v, int use){// main으로 목표사용량 인텐트 값 전달 위한 메소드
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("aim",use);
    }

    public void putmaxExtra(View v, int max){// main으로 목표사용량 인텐트 값 전달 위한 메소드
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("max",max);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Button 객체 선언
        Button distancesetbutton = (Button) findViewById(R.id.distancesetbutton);
        Button usagesetbutton = (Button) findViewById(R.id.usagesetbutton);
        Button beaconsetbutton = (Button)findViewById(R.id.beaconsetbutton);

        Button distancefinishbutton = (Button) findViewById(R.id.distancefinishbutton);
        Button usagefinishibutton = (Button) findViewById(R.id.usagefinishbutton);
        Button beaconfinishbutton = (Button)findViewById(R.id.beaconfinishbutton);

        // Edit 객체선언
        EditText distancesetedittext = (EditText)findViewById(R.id.distancesettextedit);
        EditText usagesetedittext = (EditText)findViewById(R.id.usagesettextedit);
        EditText beaconuuidsetedittext = (EditText)findViewById(R.id.beaconuuidedittext);




        // TextView 선언
        TextView distance = (TextView)findViewById(R.id.distance);
        TextView usageset = (TextView)findViewById(R.id.usageset);
         TextView beaconuuid = (TextView)findViewById(R.id.beaconuuid);

        distance.setText(Integer.toString(MainActivity.distance));
        usageset.setText(Integer.toString(MainActivity.aim));

        //초기 화면에 보지지 않아야할 콘텐츠 INVISIBLE 설정
        distancesetedittext.setVisibility(View.INVISIBLE);
        beaconuuidsetedittext.setVisibility(View.INVISIBLE);
        usagesetedittext.setVisibility(View.INVISIBLE);


        distancefinishbutton.setVisibility(View.INVISIBLE);
        beaconfinishbutton.setVisibility(View.INVISIBLE);
        usagefinishibutton.setVisibility(View.INVISIBLE);
        beaconuuid.setText(MainActivity.uuid);


        // 비콘 제어 거리 설정 버튼 리스너
        distancesetbutton.setOnClickListener(new View.OnClickListener() {
            TextView distance = (TextView) findViewById(R.id.distance);
            EditText distancesetedittext = (EditText) findViewById(R.id.distancesettextedit);
            Button distancesetbutton = (Button) findViewById(R.id.distancesetbutton);
            Button distancefinishsetbutton = (Button) findViewById(R.id.distancefinishbutton);

            @Override
            public void onClick(View view) {
                distancefinishsetbutton.setVisibility(View.VISIBLE);
                distancesetbutton.setVisibility(View.INVISIBLE);
                distance.setVisibility(View.INVISIBLE);
                distancesetedittext.setVisibility(View.VISIBLE);
            }
        });

        // 비콘 UUID 설정 버튼 리스너
        beaconsetbutton.setOnClickListener(new View.OnClickListener() {
            TextView beaconuuid = (TextView) findViewById(R.id.beaconuuid);
            EditText beaconuuidsetedittext = (EditText) findViewById(R.id.beaconuuidedittext);
            Button beaconsetbutton = (Button) findViewById(R.id.beaconsetbutton);
            Button beaconfinishbutton = (Button) findViewById(R.id.beaconfinishbutton);

            @Override
            public void onClick(View view) {
                beaconsetbutton.setVisibility(View.INVISIBLE);
                beaconfinishbutton.setVisibility(View.VISIBLE);
                beaconuuid.setVisibility(View.INVISIBLE);
                beaconuuidsetedittext.setVisibility(View.VISIBLE);
            }
        });

        //사용목표 설정 버튼 리스너
        usagesetbutton.setOnClickListener(new View.OnClickListener() {
            TextView usageset = (TextView) findViewById(R.id.usageset);
            EditText usagesetedittext = (EditText) findViewById(R.id.usagesettextedit);
            Button usagesetbutton = (Button) findViewById(R.id.usagesetbutton);
            Button usagefinishbutton = (Button) findViewById(R.id.usagefinishbutton);


            @Override
            public void onClick(View view) {
                usagefinishbutton.setVisibility(View.VISIBLE);
                usagesetbutton.setVisibility(View.INVISIBLE);
                usageset.setVisibility(View.INVISIBLE);
                usagesetedittext.setVisibility(View.VISIBLE);

            }
        });

         // 비콘 거리 설정 완료 버튼 리스너
        distancefinishbutton.setOnClickListener(new View.OnClickListener() {
            TextView distance = (TextView) findViewById(R.id.distance);
            EditText distancesettextedit = (EditText)findViewById(R.id.distancesettextedit);
            Button distancesetbutton = (Button) findViewById(R.id.distancesetbutton);
            Button distancefinishbutton = (Button) findViewById(R.id.distancefinishbutton);


            @Override
            public void onClick(View view) {
                try {
                    distancesetbutton.setVisibility(View.VISIBLE);
                    distancefinishbutton.setVisibility(View.INVISIBLE);
                    distance.setText(distancesettextedit.getText());
                    distance.setVisibility(View.VISIBLE);
                    distancesettextedit.setVisibility(View.INVISIBLE);
                    MainActivity.distance = Integer.parseInt(String.valueOf(distance.getText()));
                }catch(NumberFormatException e){
                    Toast toastview = Toast.makeText(getApplicationContext(),"바른 값 입력",Toast.LENGTH_SHORT);
                    toastview.show();
                }
            }
        });

        // 사용 목표 설정  완료 버튼 리스너
        usagefinishibutton.setOnClickListener(new View.OnClickListener() {
            TextView usageset = (TextView) findViewById(R.id.usageset);
            EditText usagesetedittext = (EditText) findViewById(R.id.usagesettextedit);
            Button usagesetbutton = (Button) findViewById(R.id.usagesetbutton);
            Button usagefinishibutton = (Button) findViewById(R.id.usagefinishbutton);

            @Override
            public void onClick(View view) {
                try{
                    usagesetbutton.setVisibility(View.VISIBLE);
                    usagefinishibutton.setVisibility(View.INVISIBLE);
                    usageset.setText(usagesetedittext.getText());
                    usageset.setVisibility(View.VISIBLE);
                    usagesetedittext.setVisibility(View.INVISIBLE);
                    MainActivity.aim = Integer.parseInt(String.valueOf(usageset.getText()));
                }catch(NumberFormatException e){
                    Toast toastview = Toast.makeText(getApplicationContext(),"바른 값 입력",Toast.LENGTH_SHORT);
                    toastview.show();
                }
            }
        });

        // 비콘 UUID 설정 완료 버튼 리스너
        beaconfinishbutton.setOnClickListener(new View.OnClickListener() {
            TextView beaconuuid = (TextView) findViewById(R.id.beaconuuid);
            EditText beaconuuidsetedittext = (EditText)findViewById(R.id.beaconuuidedittext);
            Button beaconsetbutton = (Button) findViewById(R.id.beaconsetbutton);
            Button beaconfinishsetbutton = (Button) findViewById(R.id.beaconfinishbutton);
            @Override
            public void onClick(View view) {
                beaconfinishsetbutton.setVisibility(View.INVISIBLE);
                beaconsetbutton.setVisibility(View.VISIBLE);
                beaconuuid.setVisibility(View.VISIBLE);
                beaconuuid.setText(beaconuuidsetedittext.getText());
                beaconuuidsetedittext.setVisibility(View.INVISIBLE);
                MainActivity.uuid = String.valueOf(beaconuuid.getText());
            }
        });

    }
}
