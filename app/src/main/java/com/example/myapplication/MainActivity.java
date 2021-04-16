package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.URL;

import lombok.SneakyThrows;

public class MainActivity extends AppCompatActivity {
    //змінні для обєктів дизайна
    private EditText user_fild;
    private Button main_button;
    private TextView result_info;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //силки на обєкти дизайна
        user_fild = findViewById(R.id.user_fild);
        main_button = findViewById(R.id.main_button);
        result_info = findViewById(R.id.result_info);

        //виконання події при натисканні на кнопку
        main_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //спрацьовує підказка якщо поле пусте
                if (user_fild.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();

                else {
                    //формування API для погоди
                     String city = user_fild.getText().toString();

                    String key = "f3c1fb81b178d02d6190e5fe069d1e85";

                    String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+key+"&units=metric&lang=ua";
                    //викликаєм клас для отримання погоди
                    new GetURLData().execute(url);
                    closeKybord();
                }
            }
        });
    }

    //приховувння клавіатури після вводу
    private void closeKybord() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetURLData extends AsyncTask<String, String, String> {
        //виконується до обробки URL
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                result_info.setText("Очікуйте");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //виконується під час пілключення
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader readern = null;

            try {
                //створення URL і HTTP підключення
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                //обєкт для зчитування
                try {
                    InputStream stream = connection.getInputStream();
                    readern = new BufferedReader(new InputStreamReader(stream));
                } catch (Exception e) {
                    //  result_info.setText("Місто не знайдене");
                 //   System.out.println(e.getMessage());
                }

                // генерується строка
                StringBuffer buffer = new StringBuffer();
                String line = "";

                try {
                    //зчитуєм файл і записуєм його в строку
                    while ((line = readern.readLine()) != null)
                        buffer.append(line).append("\n");
                }catch (NullPointerException ignored){

                }

                //повертаєм строку
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //закриваєм зєднання
                if (connection != null)
                    connection.disconnect();
                try {
                    if (readern != null)
                        readern.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        //виконується після отримання даних
        @SuppressLint("SetTextI18n")

        @SneakyThrows
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //конвертація JSON і вивід даних

            try {
                JSONObject jo = new JSONObject(result);
                JSONArray jsonArray = jo.getJSONArray("weather");
                JSONObject jsonWeather = jsonArray.getJSONObject(0);
                jsonWeather.getString("description");

                result_info.setText(jsonWeather.getString("description") + "\n" +
                        "Температура: " + jo.getJSONObject("main").getDouble("temp"));
           } catch (Exception e) {
                System.out.println(e.getMessage());
               // Toast.makeText(MainActivity.this, "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
                result_info.setText("Місто не знайдено"+"\n"+"Попробуйте ще раз.");
                // result_info.setText(result);
            }
        }
    }
}
