package com.kakosepise.test.kakosepise;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.mancj.materialsearchbar.MaterialSearchBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;


public class MainActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {

    ListView m_list;
    ArrayAdapter m_customerArrayAdapter;
    DatabaseController m_db;
    LayoutInflater m_inflater;
    CustomSuggestionAdapter m_customAdapter;

    // Suggestion sorcery
    MaterialSearchBar m_searchText;


    static int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //m_list = findViewById(R.id.list_view);
        //m_restButton = findViewById(R.id.rest_btn);

        m_searchText = findViewById(R.id.searchBar);

        // Database initialization
        // Step 1 - We create an empty database
        m_db = new DatabaseController(MainActivity.this);

        updateDatabase();
        //showCustomersInListView();


        m_inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        m_searchText.setOnSearchActionListener(this);
        m_customAdapter = new CustomSuggestionAdapter(m_inflater);
        m_searchText.setCustomSuggestionAdapter(m_customAdapter);


        m_searchText.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String keyWord = charSequence.toString().trim();

                if(!keyWord.equals(""))fillSuggestionsList(keyWord);
                else
                {
                    m_searchText.hideSuggestionsList();

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


//        m_restButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = "https://kakosepise.com/wp-json/wp/v2/ksp_rec/?page=1";
//
//                JsonArrayRequest m_jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray m_response) {
//                        String m_searchTerm = "";
//                        String m_title = "";
//                        String m_content = "";
//                        int m_ID = 0;
//                        int m_i =0;
//                        int m_done = 0;
//                        String m_cmpDate = "2019-05-17T10:09:28";
//                        String m_date = "";
//
//                        try {
//                            JSONObject m_term = m_response.getJSONObject(m_i);
//                            m_searchTerm = m_term.getString("slug");
//                            JSONObject m_tmpTitle = m_term.getJSONObject("title");
//                            m_title = m_tmpTitle.getString("rendered");
//                            m_ID = m_term.getInt("id");
//                            JSONObject m_tmpContent = m_term.getJSONObject("content");
//                            m_content = m_tmpContent.getString("rendered");
//                            m_date = m_term.getString("date");
//
//                            Toast.makeText(MainActivity.this, m_title + m_date, LENGTH_SHORT).show();
//
//                            m_db.addEntry(m_ID, m_content, m_title, m_searchTerm);
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//
//                    }
//
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(MainActivity.this, "FAIL", LENGTH_SHORT).show();
//                    }
//                });
//
//                RESTSingleton.getInstance(MainActivity.this).addToRequestQueue(m_jsonArrayRequest);
//
//            }
//        });


    }


    private void emptySuggestionsList() {
        m_customAdapter.clearSuggestions();
        m_searchText.setCustomSuggestionAdapter(m_customAdapter);
    }
    private void fillSuggestionsList(String _keyWord) {
        m_searchText.clearSuggestions();
        m_searchText.hideSuggestionsList();
        m_customAdapter = new CustomSuggestionAdapter(m_inflater);;
        m_customAdapter.setSuggestions(m_db.searchEntries(_keyWord));
        m_searchText.setCustomSuggestionAdapter(m_customAdapter);
        m_searchText.showSuggestionsList();
    }

    private void updateDatabase() {
        if (!m_db.isFilled()) {
            // Step 2 - We fill it up with the rows from dataInit.sql in the asset folder
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(
                        new InputStreamReader(getAssets().open(DatabaseController.m_INIT_PATH)));

                // do reading, usually loop until end of file reading
                String nextSql;
                while ((nextSql = reader.readLine()) != null) {
                    m_db.execCommand(nextSql.trim());
                }
            } catch (IOException e) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }
        }
    }

    private void showCustomersInListView() {
        // Defining an adaptor that will fill out the list view
        m_customerArrayAdapter = new ArrayAdapter<Entry>(MainActivity.this, android.R.layout.simple_list_item_1, m_db.getAllEntries());
        m_list.setAdapter(m_customerArrayAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if(!enabled)
        {
            m_searchText.hideSuggestionsList();
            m_searchText.closeSearch();
        }else
        {

        }


    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        startSearch(text.toString().trim(),true,null,true);

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}
