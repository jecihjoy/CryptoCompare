package com.testapp.cryptocompare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BitcoinConversionActivty extends AppCompatActivity {

    private TextView cryptHeaderText;
    private TextView currHeaderText;
    private EditText btcInputField;
    private EditText currInputField;
    private TextView currResultText;
    private TextView btcResultText;
    private Button convertBTCToCurrBtn;
    private Button convertToBtcBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitcoin_conversion_activty);

    cryptHeaderText = (TextView) findViewById(R.id.crypt_header);
        currHeaderText = (TextView) findViewById(R.id.curr_header);
        btcInputField = (EditText) findViewById(R.id.btc_input);
        currInputField = (EditText) findViewById(R.id.curr_input);
        currResultText = (TextView) findViewById(R.id.curr_results_text);
        btcResultText = (TextView) findViewById(R.id.btc_results_text);
        convertToBtcBtn = (Button) findViewById(R.id.convert_to_btc_btn);
        convertBTCToCurrBtn = (Button) findViewById(R.id.convert_to_curr_btn);

        Bundle prevIntent = getIntent().getExtras();
        final String curr = prevIntent.getString("currency");
        final double bitcoinRate = prevIntent.getDouble("bitcoin");

        cryptHeaderText.setText("Bitcoin to "+curr);
        currHeaderText.setText(curr+" to Bitcoin");

        convertBTCToCurrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final double cryptInput = Double.parseDouble(btcInputField.getText().toString());
                currResultText.setText("Results: "+convertBTCToCurrency(cryptInput,bitcoinRate)+" "+curr);

            }
        });


        convertToBtcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final double currInput = Double.parseDouble(currInputField.getText().toString());
                btcResultText.setText("Results: "+convertToBitcoin(currInput,bitcoinRate)+" Coins ");

            }
        });

    }

    private double convertBTCToCurrency(double bitcoinVal, double rate){

        return  bitcoinVal * rate;

    }

    private double convertToBitcoin(double currencyVal, double rate){

        return currencyVal / rate;

    }
}


