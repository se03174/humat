package com.example.humat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RecomActivity2 extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener, MapView.OpenAPIKeyAuthenticationResultListener, MapView.POIItemEventListener{
    private static final String TAG = "RecomActivity2";
    private MapView mapView2;
    private ViewGroup mapViewContainer;
    ArrayList<Document> resturantList = new ArrayList<>();
    private double mCurrentLng; //Long = X, Lat = Y
    private double mCurrentLat;
    MapPoint currentMapPoint;
    MapPoint reMapPoint;
    private ListView dataLST;
    private Button trackonBTN3, trackoffBTN3;

    private String mNum;    // 전화번호
    private boolean isTrackingMode = false;

    // 버튼
    private Button reviewBTN, callBTN, webBTN;

    String reName;  // 가게이름
    //어댑터
    private ArrayList<ItemData> dataArrays;
    private Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recom2);

        init();

        //인텐트 정보받기
        Intent intent = getIntent();
        reName = intent.getStringExtra("reName");
        requestSearchLocal(Fibase.LON,Fibase.LAT,reName);
        //requestSearchLocal(mCurrentLng,mCurrentLat,"냉면");
    }

    public void mapCreate(){        //mapview 빼기
        Log.i(TAG, "mapCreate()");
        mapView2 = new MapView(RecomActivity2.this);
        mapViewContainer.addView(mapView2);
    }

    public void mapStop(){      //mapview 추가
        Log.i(TAG, "mapStop()");
        mapViewContainer.removeView(mapView2);
    }

    public void init(){
        mapView2 = new MapView(RecomActivity2.this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view4);
        mapViewContainer.addView(mapView2);
        trackonBTN3 = findViewById(R.id.trackonBTN3);
        trackoffBTN3 = findViewById(R.id.trackoffBTN3);

        //맵 리스너 (현재위치 업데이트)
        mapView2.setCurrentLocationEventListener(this);
        mapView2.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        dataLST = findViewById(R.id.dataLST3);
        dataArrays = new ArrayList<ItemData>();

        //버튼
        reviewBTN = findViewById(R.id.reviewBTN);
        callBTN = findViewById(R.id.callBTN);
        webBTN = findViewById(R.id.webBTN);
    }

    public void Recom2Click(View v){
        Log.i("onClick",""+v.getId());
        if(v.getId() == reviewBTN.getId()) {
            Intent intent = new Intent(RecomActivity2.this, review.class);
            intent.putExtra("store_name", reName);
            startActivity(intent);
        }
        else if(v.getId() == callBTN.getId()){
            String tel = "tel:"+mNum;
            startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
        }
        else if(v.getId() == webBTN.getId()){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query="+reName));
            startActivity(intent);
        }
        if(v.getId()==trackonBTN3.getId()){
            Toast.makeText(RecomActivity2.this, "TrackingMode ON",Toast.LENGTH_SHORT).show();
            isTrackingMode = true;
            mapView2.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
        }else if(v.getId()==trackoffBTN3.getId()){
            Toast.makeText(RecomActivity2.this, "TrackingMode OFF",Toast.LENGTH_SHORT).show();
            isTrackingMode = false;
            mapView2.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        }

    }


    public void requestSearchLocal(double x, double y, String query) {        // 제목으로 검색
        Log.d("sss", Double.toString(x));
        Log.d("sss", Double.toString(y));
        resturantList.clear();
        dataArrays.clear();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<CategoryResult> call = apiInterface.getSearchLocationDetail(getString(R.string.restapi_key), query, x + "", y + "", 1);
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
                        reMapPoint = mapPoint;
                        marker.setMapPoint(mapPoint);
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
                        marker.setCustomImageResourceId(R.drawable.map_pin_blue); // 마커 이미지.
                        marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                        marker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
                        mapView2.addPOIItem(marker);

                        //전화번호 저장
                        mNum = document.getPhone();


                        // 리스트뷰
                        dataArrays.add(new ItemData(document.getPlaceName(), document.getPhone(), document.getRoadAddressName()));
                        adapter = new Adapter(RecomActivity2.this, R.layout.item_data, dataArrays);
                        dataLST.setAdapter(adapter);

                        mapView2.setMapCenterPoint(reMapPoint, true);
                    }
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
    protected void onDestroy() {
        super.onDestroy();
        mapViewContainer.removeAllViews();
        mapView2.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapView2.setShowCurrentLocationMarker(false);
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        mapStop();
//    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        //Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
        currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);
        mapView.setMapCenterPoint(currentMapPoint, true);
        mCurrentLat = mapPointGeo.latitude;
        mCurrentLng = mapPointGeo.longitude;
        Log.d("MainActivity", "현재위치 => " + mCurrentLat + "  " + mCurrentLng);
        if (!isTrackingMode) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        }
        // mLoaderLayout.setVisibility(View.GONE);

        // ------------------------------------------------------------------------------------------------
//        mCurrentLat = mapPointGeo.latitude;
//        mCurrentLng = mapPointGeo.longitude;
        // -----------------------------------------------------------------------------------------------
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
        mapStop();
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

    @Override
    public void finish(){
        mapStop();
        super.finish();
    }
}