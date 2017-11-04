package com.testapp.cryptocompare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Currency> currencyList = new ArrayList<>();
    private RecyclerView mMainRecycler;
    private Button mRetryBtn;
    private TextView mHeaderText;

    private CurrencyAdapter mAdapter;
    private ProgressDialog mProgresss;

    String jsonURL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayHolder.selectedCurrencyList = getArrayVal(this);


        if (ArrayHolder.selectedCurrencyList.size()==0){

            Intent createIntent = new Intent(MainActivity.this, CreateCardActivity.class);
            createIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(createIntent);
            finish();

        }else{

            StringBuilder builder = new StringBuilder();
            for (CustomArray curList : ArrayHolder.selectedCurrencyList){

                builder.append(curList.getCurr()+",");

            }

            jsonURL = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,ETH&tsyms="+builder.toString();

            mAdapter = new CurrencyAdapter(currencyList);

            new GetCurrencies().execute();

            if (jsonURL!=null){

                final Handler ha=new Handler();
                ha.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refresh();

                        ha.postDelayed(this, 20000);
                    }
                }, 20000);

            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_create_card) {

            startActivity(new Intent(MainActivity.this, CreateCardActivity.class));

            return true;
        }

        if (id == R.id.action_refresh) {

            refresh();

            return true;
        }

        if (id == R.id.action_clear_cards) {

            currencyList.clear();
            ArrayHolder.selectedCurrencyList.clear();
            mAdapter.notifyDataSetChanged();

            CreateCardActivity.storeArrayVal(ArrayHolder.selectedCurrencyList,MainActivity.this);

            mHeaderText.setText("No cards available, Create a new card.");

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mHeaderText.getLayoutParams();
            lp.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
            mHeaderText.setLayoutParams(lp);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.MyViewHolder> {


        private List<Currency> currencyList;

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView etheriumText, btcCurrency, currText, cardHeader;
            public ImageView removeBtn;

            public MyViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                etheriumText = view.findViewById(R.id.eth_text);
                btcCurrency = view.findViewById(R.id.btc_text);
                cardHeader = view.findViewById(R.id.card_header);
                currText = view.findViewById(R.id.card_header2);
                removeBtn = view.findViewById(R.id.remove_btn);
            }

            @Override
            public void onClick(View view) {

                final int selectedItemPosition = mMainRecycler.getChildPosition(view);

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose conversion type:");
                builder.setCancelable(false);
                final ListView optionList = new ListView(MainActivity.this);
                final String[] optionsArray = new String[]{"Bitcoin", "Etherium"};
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_selectable_list_item, optionsArray);
                optionList.setAdapter(adapter);

                builder.setView(optionList);
                builder.setCancelable(true);

                optionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        String option = adapter.getItem(position);

                        if (option.equals("Bitcoin")) {

                            Intent btcIntent = new Intent(MainActivity.this, BitcoinCoversionActivity.class);
                            btcIntent.putExtra("currency", currencyList.get(selectedItemPosition).getTitle());
                            btcIntent.putExtra("bitcoin", currencyList.get(selectedItemPosition).getBtcValue());
                            startActivity(btcIntent);


                        } else if (option.equals("Etherium")) {

                            Intent ethIntent = new Intent(MainActivity.this, EtheriumConversionActivity.class);
                            ethIntent.putExtra("currency", currencyList.get(selectedItemPosition).getTitle());
                            ethIntent.putExtra("etherium", currencyList.get(selectedItemPosition).getEthValue());
                            startActivity(ethIntent);

                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();

            }

        }


        public CurrencyAdapter(List<Currency> currencyList) {
            this.currencyList = currencyList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.currency_card, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            Currency cs = currencyList.get(position);
            holder.etheriumText.setText("ETH:\n "+cs.getEthValue());
            holder.btcCurrency.setText("BTC:\n "+cs.getBtcValue());
            holder.cardHeader.setText(ArrayHolder.selectedCurrencyList.get(position).getTitle());
            holder.currText.setText("( "+cs.getTitle()+" )");

            holder.removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    currencyList.remove(position);
                    ArrayHolder.selectedCurrencyList.remove(position);
                    mAdapter.notifyDataSetChanged();

                    CreateCardActivity.storeArrayVal(ArrayHolder.selectedCurrencyList,MainActivity.this);

                }
            });
        }

        @Override
        public int getItemCount() {
            return currencyList.size();
        }

    }

    private class GetCurrencies extends AsyncTask<Void, Void, Void> {

        String jsonStr = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgresss = new ProgressDialog(MainActivity.this);
            mProgresss.setCanceledOnTouchOutside(false);
            mProgresss.setMessage("Loading data from Server...");
            mProgresss.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler handler = new HttpHandler();

            jsonStr = handler.makeServiceCall(jsonURL);

            if (jsonStr != null) {
                try {
                    JSONObject btcObj = new JSONObject(jsonStr).getJSONObject("BTC");
                    JSONObject ethObj = new JSONObject(jsonStr).getJSONObject("ETH");


                    for (int i =0; i < ArrayHolder.selectedCurrencyList.size(); i++){

                        Double btcVal = btcObj.getDouble(ArrayHolder.selectedCurrencyList.get(i).getCurr());
                        Double ethVal = ethObj.getDouble(ArrayHolder.selectedCurrencyList.get(i).getCurr());
                        Currency sCurrency = new Currency(ArrayHolder.selectedCurrencyList.get(i).getCurr(),btcVal , ethVal);
                        currencyList.add(sCurrency);

                    }

                } catch (final JSONException e) {


                }
                mProgresss.dismiss();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (jsonStr!=null){

                mProgresss.dismiss();

                setContentView(R.layout.activity_main);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                mHeaderText = (TextView) findViewById(R.id.headerText);
                mMainRecycler = (RecyclerView) findViewById(R.id.main_recyclerview);
                mMainRecycler.setItemAnimator(new DefaultItemAnimator());
                mMainRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                mHeaderText.setVisibility(View.VISIBLE);
                mMainRecycler.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, CreateCardActivity.class));
                    }
                });


            }else {

                mProgresss.dismiss();

                setContentView(R.layout.error_layout);

                mRetryBtn = (Button) findViewById(R.id.retry_btn);
                mRetryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new GetCurrencies().execute();

                    }
                });

                Toast.makeText(MainActivity.this, "Failed to connect to the server. Check your internet connection.", Toast.LENGTH_SHORT).show();

            }

        }
    }

    private class RefreshCurrencies extends AsyncTask<Void, Void, Void> {

        String jsonStr = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler handler = new HttpHandler();

            jsonStr = handler.makeServiceCall(jsonURL);

            if (jsonStr != null) {
                try {
                    JSONObject btcObj = new JSONObject(jsonStr).getJSONObject("BTC");
                    JSONObject ethObj = new JSONObject(jsonStr).getJSONObject("ETH");

                    currencyList.clear();

                    for (int i =0; i < ArrayHolder.selectedCurrencyList.size(); i++){

                        Double btcVal = btcObj.getDouble(ArrayHolder.selectedCurrencyList.get(i).getCurr());
                        Double ethVal = ethObj.getDouble(ArrayHolder.selectedCurrencyList.get(i).getCurr());
                        Currency sCurrency = new Currency(ArrayHolder.selectedCurrencyList.get(i).getCurr(),btcVal , ethVal);
                        currencyList.add(sCurrency);

                    }

                } catch (final JSONException e) {


                }
                mProgresss.dismiss();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (jsonStr!=null){

                mMainRecycler.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

                mProgresss.dismiss();

            }else {

            }

        }
    }

    private void refresh(){

        new RefreshCurrencies().execute();

    }

    public static ArrayList getArrayVal( Context ctx)
    {
        SharedPreferences shref = ctx.getSharedPreferences("currArrayValues", Context.MODE_PRIVATE);

        ArrayList<CustomArray>   rArray;
        Gson gson;
        gson = new GsonBuilder().create();
        String response = shref.getString("currArray", null);

        if (response!=null){

            Type type = new TypeToken<ArrayList<CustomArray>>(){}.getType();
            rArray = gson.fromJson(response, type);

        }else {

            rArray = new ArrayList<>();

        }

        return rArray;

    }


}

