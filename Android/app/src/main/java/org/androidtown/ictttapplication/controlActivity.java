package org.androidtown.ictttapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

public class controlActivity extends AppCompatActivity {

    private static int r1;
    private static int r2;
    private static int r3;

    public static int arr[] = {0,0,0};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Button[] onbtn =

        Button on1 = (Button) findViewById(R.id.onbutton1);
        Button on2 = (Button) findViewById(R.id.onbutton2);
        Button on3 = (Button) findViewById(R.id.onbutton3);
        Button off1 = (Button) findViewById(R.id.offbutton1);
        Button off2 = (Button) findViewById(R.id.offbutton2);
        Button off3 = (Button) findViewById(R.id.offbutton3);

        ImageView turnon1 = (ImageView) findViewById(R.id.turnon1);
        ImageView turnoff1 = (ImageView) findViewById(R.id.turnoff1);
        ImageView turnon2 = (ImageView) findViewById(R.id.turnon2);
        ImageView turnoff2 = (ImageView) findViewById(R.id.turnoff2);
        ImageView turnon3 = (ImageView) findViewById(R.id.turnon3);
        ImageView turnoff3 = (ImageView) findViewById(R.id.turnoff3);



        Button bt = (Button)findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            Button bt = (Button)findViewById(R.id.button);
            @Override
            public void onClick(View view) {
                setState_auto();
                finish();
            }
        });


        //Switch 객체선언
        Switch switch1 = (Switch)findViewById(R.id.switch1);
        Switch switch2 = (Switch)findViewById(R.id.switch2);
        Switch switch3 = (Switch)findViewById(R.id.switch3);

        switch1.setOnClickListener(new View.OnClickListener() {
            Switch switch1 = (Switch)findViewById(R.id.switch1);
            @Override
            public void onClick(View v) {
                if(switch1.isChecked())
                    arr[0]=1;
                else
                    arr[0]=0;
            }
        });
        switch2.setOnClickListener(new View.OnClickListener() {
            Switch switch1 = (Switch) findViewById(R.id.switch1);

            @Override
            public void onClick(View v) {
                if (switch1.isChecked())
                    arr[1] = 1;
                else
                    arr[1] = 0;
            }
        });
        switch3.setOnClickListener(new View.OnClickListener() {
            Switch switch1 = (Switch) findViewById(R.id.switch1);

            @Override
            public void onClick(View v) {
                if (switch1.isChecked())
                    arr[2] = 1;
                else
                    arr[2] = 0;
            }
        });

        //상태값 저장
        getState();

        if (r1 == 0 && r2 == 0 && r3 == 0) {
            turnon1.setVisibility(View.INVISIBLE);
            turnoff1.setVisibility(View.VISIBLE);

            turnon2.setVisibility(View.INVISIBLE);
            turnoff2.setVisibility(View.VISIBLE);

            turnon3.setVisibility(View.INVISIBLE);
            turnoff3.setVisibility(View.VISIBLE);
        }
        else if (r1 == 0 && r2 == 0 && r3 == 1){
            turnon1.setVisibility(View.INVISIBLE);
            turnoff1.setVisibility(View.VISIBLE);

            turnon2.setVisibility(View.INVISIBLE);
            turnoff2.setVisibility(View.VISIBLE);

            turnon3.setVisibility(View.VISIBLE);
            turnoff3.setVisibility(View.INVISIBLE);
        }
        else if (r1 == 0 && r2 == 1 && r3 == 0){
            turnon1.setVisibility(View.INVISIBLE);
            turnoff1.setVisibility(View.VISIBLE);

            turnon2.setVisibility(View.VISIBLE);
            turnoff2.setVisibility(View.INVISIBLE);

            turnon3.setVisibility(View.INVISIBLE);
            turnoff3.setVisibility(View.VISIBLE);
        }
        else if (r1 == 0 && r2 == 1 && r3 == 1){
            turnon1.setVisibility(View.INVISIBLE);
            turnoff1.setVisibility(View.VISIBLE);

            turnon2.setVisibility(View.VISIBLE);
            turnoff2.setVisibility(View.INVISIBLE);

            turnon3.setVisibility(View.VISIBLE);
            turnoff3.setVisibility(View.INVISIBLE);
        }
        else if (r1 == 1 && r2 == 0 && r3 == 0){
            turnon1.setVisibility(View.VISIBLE);
            turnoff1.setVisibility(View.INVISIBLE);

            turnon2.setVisibility(View.INVISIBLE);
            turnoff2.setVisibility(View.VISIBLE);

            turnon3.setVisibility(View.INVISIBLE);
            turnoff3.setVisibility(View.VISIBLE);
        }
        else if (r1 == 1 && r2 == 0 && r3 == 1){
            turnon1.setVisibility(View.VISIBLE);
            turnoff1.setVisibility(View.INVISIBLE);

            turnon2.setVisibility(View.INVISIBLE);
            turnoff2.setVisibility(View.VISIBLE);

            turnon3.setVisibility(View.VISIBLE);
            turnoff3.setVisibility(View.INVISIBLE);
        }
        else if (r1 == 1 && r2 == 1 && r3 == 0){
            turnon1.setVisibility(View.VISIBLE);
            turnoff1.setVisibility(View.INVISIBLE);

            turnon2.setVisibility(View.VISIBLE);
            turnoff2.setVisibility(View.INVISIBLE);

            turnon3.setVisibility(View.INVISIBLE);
            turnoff3.setVisibility(View.VISIBLE);
        }

        else if (r1 == 1 && r2 == 1 && r3 == 1){
            turnon1.setVisibility(View.VISIBLE);
            turnoff1.setVisibility(View.INVISIBLE);

            turnon2.setVisibility(View.VISIBLE);
            turnoff2.setVisibility(View.INVISIBLE);

            turnon3.setVisibility(View.VISIBLE);
            turnoff3.setVisibility(View.INVISIBLE);
        }

        //스위치1의 ON 버튼을 눌렀을 때
        on1.setOnClickListener(new View.OnClickListener() {//맨위 on 버튼
            ImageView turnon1 = (ImageView) findViewById(R.id.turnon1);
            ImageView turnoff1 = (ImageView) findViewById(R.id.turnoff1);

            @Override
            public void onClick(View view) {
                turnon1.setVisibility(View.VISIBLE);
                turnoff1.setVisibility(View.INVISIBLE);


                r1 = 1;
                setState();

            }
        });

        //스위치1의 OFF 버튼을 눌렀을 때
        off1.setOnClickListener(new View.OnClickListener() {//맨위 off 버튼
            ImageView turnon1 = (ImageView) findViewById(R.id.turnon1);
            ImageView turnoff1 = (ImageView) findViewById(R.id.turnoff1);

            @Override
            public void onClick(View view) {
                turnon1.setVisibility(View.INVISIBLE);
                turnoff1.setVisibility(View.VISIBLE);
                r1 = 0;
                setState();
            }
        });

        //스위치2의 ON 버튼을 눌렀을 때
        on2.setOnClickListener(new View.OnClickListener() {//두번째 on 버튼
            ImageView turnon2 = (ImageView) findViewById(R.id.turnon2);
            ImageView turnoff2 = (ImageView) findViewById(R.id.turnoff2);

            @Override
            public void onClick(View view) {
                turnon2.setVisibility(View.VISIBLE);
                turnoff2.setVisibility(View.INVISIBLE);
                r2 = 1;
                setState();
            }
        });

        //스위치 2의 OFF 버튼을 눌렀을 때
        off2.setOnClickListener(new View.OnClickListener() {//두번째 off 버튼
            ImageView turnon2 = (ImageView) findViewById(R.id.turnon2);
            ImageView turnoff2 = (ImageView) findViewById(R.id.turnoff2);

            @Override
            public void onClick(View view) {
                turnon2.setVisibility(View.INVISIBLE);
                turnoff2.setVisibility(View.VISIBLE);
                r2 = 0;
                setState();
            }
        });

        //스위치 3의 ON 버튼을 눌렀을 때
        on3.setOnClickListener(new View.OnClickListener() {//세번째 on 버튼
            ImageView turnon3 = (ImageView) findViewById(R.id.turnon3);
            ImageView turnoff3 = (ImageView) findViewById(R.id.turnoff3);

            @Override
            public void onClick(View view) {
                turnon3.setVisibility(View.VISIBLE);
                turnoff3.setVisibility(View.INVISIBLE);
                r3 = 1;
                setState();
            }
        });

        //스위치 3의 OFF 버튼을 눌렀을 때
        off3.setOnClickListener(new View.OnClickListener() {//세번째 off 버튼
            ImageView turnon3 = (ImageView) findViewById(R.id.turnon3);
            ImageView turnoff3 = (ImageView) findViewById(R.id.turnoff3);

            @Override
            public void onClick(View view) {
                turnon3.setVisibility(View.INVISIBLE);
                turnoff3.setVisibility(View.VISIBLE);
                r3 = 0;
                setState();
            }
        });
    }

    /*
    saved_state
    0b000 0 48 - off off off
    0b001 1 49 - off off on
    0b010 2 50 - off on off
    0b011 3 51 - off on on
    0b100 4 52 - on off off
    0b101 5 53 - on off on
    0b110 6 54 - on on off
    0b111 7 55 - on on on
     */

    private void getState() {

        String state = MainActivity.saved_state;

        if (state.equals("00")) {
            r1 = 0;
            r2 = 0;
            r3 = 0;
        } else if (state.equals("11")) {
            r1 = 0;
            r2 = 0;
            r3 = 1;
        } else if (state.equals("22")) {
            r1 = 0;
            r2 = 1;
            r3 = 0;
        } else if (state.equals("33")) {
            r1 = 0;
            r2 = 1;
            r3 = 1;
        } else if (state.equals("44")) {
            r1 = 1;
            r2 = 0;
            r3 = 0;
        } else if (state.equals("55")) {
            r1 = 1;
            r2 = 0;
            r3 = 1;
        } else if (state.equals("66")) {
            r1 = 1;
            r2 = 1;
            r3 = 0;
        } else if (state.equals("77")) {
            r1 = 1;
            r2 = 1;
            r3 = 1;
        }
    }

    private void setState() {

        String result = null;

        //r1 r2 r3 비교 후 Set State
        if (r1 == 0 && r2 == 0 && r3 == 0)
            result = "00";
        else if (r1 == 0 && r2 == 0 && r3 == 1)
            result = "11";
        else if (r1 == 0 && r2 == 1 && r3 == 0)
            result = "22";
        else if (r1 == 0 && r2 == 1 && r3 == 1)
            result = "33";
        else if (r1 == 1 && r2 == 0 && r3 == 0)
            result = "44";
        else if (r1 == 1 && r2 == 0 && r3 == 1)
            result = "55";
        else if (r1 == 1 && r2 == 1 && r3 == 0)
            result = "66";
        else if (r1 == 1 && r2 == 1 && r3 == 1)
            result = "77";


        MainActivity.saved_state = result;
    }

    private void setState_auto() {

        String result = null;

        //r1 r2 r3 비교 후 Set State
        if (arr[0] == 0 && arr[1]== 0 && arr[2] == 0)
            result = "00";
        else if (arr[0] == 0 && arr[1]== 0 && arr[2] == 1)
            result = "11";
        else if (arr[0] == 0 && arr[1]== 1 && arr[2] == 0)
            result = "22";
        else if (arr[0] == 0 && arr[1]== 1 && arr[2] == 1)
            result = "33";
        else if (arr[0] == 1 && arr[1]== 0 && arr[2] == 0)
            result = "44";
        else if (arr[0] == 1 && arr[1]== 0 && arr[2] == 1)
            result = "55";
        else if (arr[0] == 1 && arr[1]== 1 && arr[2] == 0)
            result = "66";
        else if (arr[0] == 1 && arr[1]== 1 && arr[2] == 1)
            result = "77";


        MainActivity.auto_setting_saved_state = result;
    }

}
