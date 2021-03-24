package com.example.humat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FragmentPage1 extends Fragment {
    private LottieAnimationView sun;
    Context ct;
    ListView dataLST;
    ArrayList<itemData2> arrData;
    itemDataAdapter2 adapter;

    String[] rain_else = new String[]{"짬뽕", "찜닭", "삼계탕", "감자탕", "국밥", "전골","라멘", "해장국", "우동","포차"};
    String[] rain_hot = new String[]{"아이스크림", "냉면", "삼계탕", "빙수", "햄버거", "포차","콩국수"};
    String[] freezing = new String[]{"짱뽕","국밥","감자탕","양식","찜닭","전골","샤브샤브", "포차"};
    String[] clear_hot = new String[]{"아이스크림", "냉면", "삼계탕", "빙수", "햄버거","콩국수"};
    String[] clear_cold = new String[]{"짬뽕", "찜닭", "삼계탕", "감자탕", "국밥", "전골","라멘", "해장국", "우동","포차","찌개"};
    String[] clear_normal = new String[]{"일식","분식","양식","한식","햄버거","치킨","삼겹살","족발","양꼬치","막창"};

    int hot_day = 0;
    int cold_day = 0;
    String main = null; // 기상 상태
    String description = null; // 기상 상태
    String result = "";
    double lon;
    double lat;
    double temp; // °C
    double feel_temp;
    double temp_min;
    double temp_max;
    double humidity; // %
    double wind_speed; // m/s
    double Kelvin = 273.15;
    int weather_id;
    int weather_condition=0;
    String citiy_name;

    int flag = 0;

    TextView temperate;
    TextView detail_weather;
    TextView city_TXT;

    private Button keywordBTN;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("위도", Fibase.LON+"     "+Fibase.LAT);

        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.hide();

        View v = inflater.inflate(R.layout.fragment_page_1, container, false);

        keywordBTN = v.findViewById(R.id.keywordBTN);
        temperate = v.findViewById(R.id.temp);
        detail_weather = v.findViewById(R.id.detail_weather);
        city_TXT = v.findViewById(R.id.city_name);

        LottieAnimationView animationView = v.findViewById(R.id.sun);
        ct = container.getContext();

        String resultText = "값이없음";

        try {
            resultText = new Task().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        try {
            weatherParser(resultText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dataLST = v.findViewById(R.id.dataLST);


        arrData = new ArrayList<itemData2>();

        dataLST.setOnItemClickListener(new AdapterView.OnItemClickListener() {          //
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                TextView dataTXT = ((TextView) view).findViewById(android.R.id.text1);
//                String reName = dataTXT.getText().toString();
//                Intent intent = new Intent(ct, RecomActivity.class);
//                intent.putExtra("reName", reName);
//                startActivity(intent);

                String reName = arrData.get(position).getFood_name();
                Intent intent = new Intent(ct, RecomActivity.class);
                intent.putExtra("reName", reName);
                startActivity(intent);

            }
        });


        convertKelvinToCelsius();

        result = "날씨 : " + main + "\n상세 날씨 : " + description + "\n온도 : " + temp + " °C" + "\n습도 : " + humidity + " %" + "\n풍속 : " + wind_speed + " m/s";

        city_TXT.setText(citiy_name);
        temperate.setText(Double.toString(temp) + " °C");
        detail_weather.setText(result);

        setUPAnimation(weather_id, animationView);

        classify_weather(flag, temp);

        adapter = new itemDataAdapter2(ct, R.layout.item_data3, arrData);
        dataLST.setAdapter(adapter);



        Log.i("AA", "FragmentPage1 - onCreate()");

        keywordBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ct, SearchActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    public void classify_weather(int f, double temp){
        if(f==1 && temp>=25.0){
            weather_condition = 1;  // 덥고 비
            recommended_food(rain_hot);
        }
        else if(f==1){
            weather_condition = 2;  // else 비
            recommended_food(rain_else);
        }
        else if(f==3){
            weather_condition = 3; // 눈
            recommended_food(freezing);
        }
        else if(f==2 && temp>=25.0){
            weather_condition = 4; // 덥고 맑음
            recommended_food(clear_hot);
        }
        else if(f==2 && temp<=5.0){
            weather_condition = 5; // 춥고 맑음
            recommended_food(clear_cold);
        }
        else if(f==2){
            weather_condition = 6; //보통 맑음
            recommended_food(clear_normal);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.i("AA", "FragmentPage1 - onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i("AA", "FragmentPage1 - onPause()");
    }


    public void weatherParser(String jsonString) throws JSONException {

        JSONObject jsonObject = new JSONObject(jsonString);

        try {
            JSONArray jarray = new JSONObject(jsonString).getJSONArray("weather");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jObject = jarray.getJSONObject(i);

                main = jObject.optString("main");
                description = jObject.optString("description");
                weather_id = jObject.optInt("id");
            }

            JSONObject coordObject = jsonObject.getJSONObject("coord");

            lon = coordObject.getDouble("lon");
            lat = coordObject.getDouble("lat");

            JSONObject mainObject = jsonObject.getJSONObject("main");

            temp = mainObject.getDouble("temp");
            feel_temp = mainObject.getDouble("feels_like");
            temp_min = mainObject.getDouble("temp_min");
            temp_max = mainObject.getDouble("temp_max");
            humidity = mainObject.getDouble("humidity");

            JSONObject windObject = jsonObject.getJSONObject("wind");

            wind_speed = windObject.getDouble("speed");

            citiy_name = jsonObject.getString("name");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void recommended_food(String[] food) {
        for (int i = 0; i < food.length; i++) {
            if(food[i]=="짬뽕"||food[i]=="우동"||food[i]=="콩국수"||food[i]=="라멘"||food[i]=="냉면")
            {
                arrData.add(new itemData2(food[i],R.drawable.ramen));
            }
            else if(food[i]=="감자탕"||food[i]=="국밥"||food[i]=="전골"||food[i]=="해장국"||food[i]=="샤브샤브")
            {
                arrData.add(new itemData2(food[i],R.drawable.soup));
            }
            else if(food[i]=="아이스크림"||food[i]=="빙수")
            {
                arrData.add(new itemData2(food[i],R.drawable.ice_cream));
            }
            else if(food[i]=="포차"||food[i]=="양꼬치")
            {
                arrData.add(new itemData2(food[i],R.drawable.beer));
            }
            else if(food[i]=="햄버거")
            {
                arrData.add(new itemData2(food[i],R.drawable.hamburger));
            }
            else if(food[i]=="양식"||food[i]=="삼겹살"||food[i]=="족발")
            {
                arrData.add(new itemData2(food[i],R.drawable.meat));
            }
            else if(food[i]=="한식"||food[i]=="분식")
            {
                arrData.add(new itemData2(food[i],R.drawable.gimbap));
            }
            else if(food[i]=="일식")
            {
                arrData.add(new itemData2(food[i],R.drawable.sushi));
            }
            else if(food[i]=="치킨"||food[i]=="찜닭"||food[i]=="삼계탕")
            {
                arrData.add(new itemData2(food[i],R.drawable.chicken));
            }
        }
    }

    public void food_recomend(int flag, int temp) {
        if (flag == 1) {

        } else if (flag == 2) {

        }
    }


    public void setUPAnimation(int id, LottieAnimationView animationView) {
        if (id >= 200 && id < 300) {
            animationView.setAnimation("storm.json");
            animationView.playAnimation();
            flag = 1;
        } else if (id >= 300 && id < 500) {
            animationView.setAnimation("drizzle.json");
            animationView.playAnimation();
            flag = 1;
        } else if (id >= 500 && id < 600) {
            animationView.setAnimation("rain.json");
            animationView.playAnimation();
            flag = 1;
        } else if (id >= 600 && id < 700) {
            animationView.setAnimation("freezing.json");
            animationView.playAnimation();
            flag = 3;
        } else if (id >= 700 && id < 800) {
            animationView.setAnimation("haze.json");
            animationView.playAnimation();
            flag = 2;
        } else if (id == 800) {
            animationView.setAnimation("sun.json");
            animationView.playAnimation();
            flag = 2;
        } else if (id > 800 && id <= 802) {
            animationView.setAnimation("cloudy.json");
            animationView.playAnimation();
            flag = 2;
        } else if (id > 802) {
            animationView.setAnimation("cloud.json");
            animationView.playAnimation();
            flag = 2;
        }
    }


    public void convertKelvinToCelsius() {
        temp = temp - Kelvin;
        feel_temp = feel_temp - Kelvin;
        temp_max = temp_max - Kelvin;
        temp_min = temp_min - Kelvin;

        temp = Double.parseDouble(String.format("%.2f", temp));
        feel_temp = Double.parseDouble(String.format("%.2f", feel_temp));
        temp_max = Double.parseDouble(String.format("%.2f", temp_max));
        temp_min = Double.parseDouble(String.format("%.2f", temp_min));

    }

    public class Task extends AsyncTask<String, Void, String> {

        private String str, receiveMsg;

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + Fibase.LAT + "&lon=" + Fibase.LON + "&appid=9c1aa85f8e8c650f5ccff976806d5119");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                    Log.i("receiveMsg : ", receiveMsg);

                    reader.close();
                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return receiveMsg;
        }
    }


}




