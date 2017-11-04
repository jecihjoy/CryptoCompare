package com.testapp.cryptocompare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EtheriumConversionActivity extends AppCompatActivity {

    private TextView ethHeaderText;
    private TextView ethCurrHeaderText;
    private EditText ethInputField;
    private EditText ethCurrInputField;
    private TextView ethCurrResultText;
    private TextView ethResultText;
    private Button convertEthToCurrBtn;
    private Button convertToEthBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etherium_conversion);

        ethHeaderText = (TextView) findViewById(R.id.eth_header);
        ethCurrHeaderText = (TextView) findViewById(R.id.curr_to_eth_header);
        ethInputField = (EditText) findViewById(R.id.eth_input);
        ethCurrInputField = (EditText) findViewById(R.id.curr_eth_input);
        ethCurrResultText = (TextView) findViewById(R.id.eth_curr_results_text);
        ethResultText = (TextView) findViewById(R.id.eth_results_text);
        convertEthToCurrBtn = (Button) findViewById(R.id.convert_to_curr_eth);
        convertToEthBtn = (Button) findViewById(R.id.convert_to_eth_btn);

        Bundle prevIntent = getIntent().getExtras();
        final String ethCurr = prevIntent.getString("currency");
        final double etheriumRate = prevIntent.getDouble("etherium");

        ethHeaderText.setText("Etherium to "+ethCurr);
        ethCurrHeaderText.setText(ethCurr+" to Etherium");

        convertEthToCurrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final double cryptInput = Double.parseDouble(ethInputField.getText().toString());
                ethCurrResultText.setText("Results: "+convertEthToCurrency(cryptInput,etheriumRate)+" "+ethCurr);

            }
        });

        convertToEthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final double currInput = Double.parseDouble(ethCurrInputField.getText().toString());
                ethResultText.setText("Results: "+convertToEtherium(currInput,etheriumRate)+" Coins ");

            }
        });

    }

    private double convertEthToCurrency(double ethVal, double rate){

        return  ethVal * rate;

    }

    private double convertToEtherium(double currencyVal, double rate){

        return currencyVal / rate;

    }

}

