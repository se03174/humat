package com.example.humat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RecomActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener, MapView.OpenAPIKeyAuthenticationResultListener, MapView.POIItemEventListener{
    private static final String TAG = "RecomActivity";
    private MapView mapView1;
    private ViewGroup mapViewContainer;
    ArrayList<Document> resturantList = new ArrayList<>();
    private double mCurrentLng; //Long = X, Lat = Y
    private double mCurrentLat;
    MapPoint currentMapPoint;
    private boolean first = false;        //첫진입인지 확인
    private boolean isTrackingMode = false;

    private Button trackonBTN2, trackoffBTN2;


    // 바텀 네비게이션 뷰
    //private BottomNavigationView bottomNavigationView;

    //어댑터
    private ListView dataLST;
    //    private ArrayList<ItemData> dataArrays;
//    private Adapter adapter;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String, String>> arrDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recom);
        init();

        //requestSearchLocal(mCurrentLng,mCurrentLat,"냉면");
        Intent intent = getIntent();
        String reName = intent.getStringExtra("reName");
        requestSearchLocal(Fibase.LON,Fibase.LAT,reName);
    }

    public void mapCreate(){        //mapview 추가
        Log.i("TAG", "mapCreate()");
        mapView1 = new MapView(RecomActivity.this);
        mapViewContainer.addView(mapView1);
//        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Fibase.LON, Fibase.LAT);      // 첫시작이 위치로딩이 너무 오래걸림
//        mapView1.setMapCenterPoint(mapPoint, true);

        mapView1.setCurrentLocationEventListener(this);
        mapView1.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        // 맵뷰 마커찍기
        Log.d("RecomActivity", resturantList.size() + "");
        int tagNum = 10;
        for (Document document : resturantList) {
            MapPOIItem marker = new MapPOIItem();
            marker.setItemName(document.getPlaceName());
            marker.setTag(tagNum++);
            double x = Double.parseDouble(document.getY());
            double y = Double.parseDouble(document.getX());
            //카카오맵은 참고로 new MapPoint()로  생성못함. 좌표기준이 여러개라 이렇게 메소드로 생성해야함
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
            marker.setMapPoint(mapPoint);
            marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
            marker.setCustomImageResourceId(R.drawable.map_pin_blue); // 마커 이미지.
            marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
            marker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
            mapView1.addPOIItem(marker);
        }
    }

    public void mapStop(){      //mapview 빼기
        Log.i("TAG", "mapStop()");
        mapViewContainer.removeView(mapView1);
    }

    public void init(){
        mapView1 = new MapView(RecomActivity.this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view2);
        mapViewContainer.addView(mapView1);

        trackonBTN2 = findViewById(R.id.trackonBTN3);
        trackoffBTN2 = findViewById(R.id.trackoffBTN3);

        Toast.makeText(this, "맵을 로딩중입니다", Toast.LENGTH_SHORT).show();

        //맵 리스너 (현재위치 업데이트)
        mapView1.setCurrentLocationEventListener(this);
        //setCurrentLocationTrackingMode (지도랑 현재위치 좌표 찍어주고 따라다닌다.)
        mapView1.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        dataLST = findViewById(R.id.dataLST);
        arrDatas = new ArrayList<HashMap<String, String>>();
    }

    public void onClick(View v){
        if(v.getId()==trackonBTN2.getId()){
            Toast.makeText(RecomActivity.this, "TrackingMode ON",Toast.LENGTH_SHORT).show();
            isTrackingMode = true;
            mapView1.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
        }else if(v.getId()==trackoffBTN2.getId()){
            Toast.makeText(RecomActivity.this, "TrackingMode OFF",Toast.LENGTH_SHORT).show();
            isTrackingMode = false;
            mapView1.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        }
    }

    public void requestSearchLocal(double x, double y, String query) {        // 제목으로 검색
        Log.d("sss", Double.toString(x));
        Log.d("sss", Double.toString(y));
        resturantList.clear();
        arrDatas.clear();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<CategoryResult> call = apiInterface.getSearchLocationDetail(getString(R.string.restapi_key), query, x + "", y + "", 5);
        call.enqueue(new Callback<CategoryResult>() {
            @Override
            public void onResponse(@NonNull Call<CategoryResult> call, @NonNull Response<CategoryResult> response) {
                Log.d("sss", "onResponse");
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getDocuments() != null) {
                        Log.d("sss", "resturant Success");
                        resturantList.addAll(response.body().getDocuments());
                    }

                    //마커 찍기
                    Log.d("sss", resturantList.size() + "");
                    int tagNum = 10;
                    for (Document document : resturantList) {
                        MapPOIItem marker = new MapPOIItem();
                        marker.setItemName(document.getPlaceName());
                        marker.setTag(tagNum++);
                        double x = Double.parseDouble(document.getY());
                        double y = Double.parseDouble(document.getX());
                        //카카오맵은 참고로 new MapPoint()로  생성못함. 좌표기준이 여러개라 이렇게 메소드로 생성해야함
                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                        marker.setMapPoint(mapPoint);
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
                        marker.setCustomImageResourceId(R.drawable.map_pin_blue); // 마커 이미지.
                        marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                        marker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
                        mapView1.addPOIItem(marker);

                        HashMap<String, String> map = new HashMap<>();
                        map.put("name", document.getPlaceName());
                        map.put("address", document.getAddressName());
                        arrDatas.add(map);
                        Log.i("eee", document.getPlaceName()+document.getAddressName());
                        Log.i("eee", Integer.toString(arrDatas.size()));
                    }
                    adapter = new SimpleAdapter(RecomActivity.this, arrDatas, R.layout.item_data2, new String[]{"name","address"}, new int[]{R.id.nameTXT2, R.id.addressTXT2});
                    dataLST.setAdapter(adapter);

                    // listview 이벤트리스너
                    dataLST.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            LinearLayout ll = (LinearLayout) view;
                            TextView dataTXT = (TextView)ll.findViewById(R.id.nameTXT2);
                            String reName = dataTXT.getText().toString();
                            Log.d("ff", reName);
                            Intent intent = new Intent(RecomActivity.this, RecomActivity2.class);
                            intent.putExtra("reName", reName);
                            mapStop();
                            startActivity(intent);

                        }
                    });

                } else{
                    Log.d("sss", Integer.toString(response.code()));
                }

            }

            @Override
            public void onFailure(Call<CategoryResult> call, Throwable t) {
                Log.d("sss", "네트워크 오류");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(first) {
            mapCreate();
        }
        first = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapViewContainer.removeAllViews();
        mapView1.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapView1.setShowCurrentLocationMarker(false);
    }


    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
        currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);
        mapView.setMapCenterPoint(currentMapPoint, true);
        mCurrentLat = mapPointGeo.latitude;
        mCurrentLng = mapPointGeo.longitude;
        Log.d("MainActivity", "현재위치 => " + mCurrentLat + "  " + mCurrentLng);
        if (!isTrackingMode) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        }
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
        Log.i("MainActivity", "onCurrentLocationUpdateFailed");
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
        Log.i("MainActivity", "onCurrentLocationUpdateCancelled");
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
    }


    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int i, String s) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
}