package org.androidtown.ictttapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Calculator extends AppCompatActivity {

    static int price;
    static int use = 0;
    public void calculatePrice() {
        EditText usage = (EditText) findViewById(R.id.usage);
        use = Integer.parseInt(String.valueOf(usage.getText()));// 전기요금 계산할 사용량
        if (use < 100)
            price = (int) (60.7 * use);
        else if (use >= 100 && use <= 200)
            price = (int) (125.9 * use);
        else if (use > 200 && use <= 300)
            price = (int) (187.9 * use);
        else if (use > 300 && use <= 400)
            price = (int) (280.6 * use);
        else if (use > 400 && use <= 500)
            price = (int) (417.7 * use);

        else
            price = (int) (709.5 * use);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        EditText usage = (EditText) findViewById(R.id.usage);
        //use = Integer.valueOf(usage.getText());
        use = Integer.parseInt(String.valueOf(usage.getText()));// 전기요금 계산할 사용량
        price = 0;
        //주택용(저압) 기준 전기 요금 계산


        Button calculatebutton = (Button) findViewById(R.id.calculatebutton);
        calculatebutton.setOnClickListener(new View.OnClickListener() {
            TextView money = (TextView) findViewById(R.id.money);
            @Override
            public void onClick(View view) {
                calculatePrice();
                money.setText(Integer.toString(price));
            }
        });

    }


}
