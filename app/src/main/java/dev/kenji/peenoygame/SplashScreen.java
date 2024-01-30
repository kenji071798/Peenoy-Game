package dev.kenji.peenoygame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 3000; // 2 seconds

    public static String gameURL = "";
    public static String appStatus = "";
    public static String apiResponse = "";

    SharedPreferences MyPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        MyPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        VideoView videoView = findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splashh));
        videoView.start();


        RequestQueue connectAPI = Volley.newRequestQueue(this);
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("appid", "5G");
            requestBody.put("package", getPackageName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String endPoint = "https://backend.madgamingdev.com/api/gameid" + "?appid=5G&package=" + getPackageName();

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, endPoint, requestBody,
                response -> {
                    apiResponse = response.toString();

                    try {
                        JSONObject jsonData = new JSONObject(apiResponse);
                        String decryptedData = MCrypt.decrypt(jsonData.getString("data"),"21913618CE86B5D53C7B84A75B3774CD");
                        JSONObject gameData = new JSONObject(decryptedData);

                        appStatus = jsonData.getString("gameKey");
                        gameURL = gameData.getString("gameURL");

                        MyPrefs.edit().putString("gameURL", gameURL).apply();

                        // Using a Handler to delay the transition to the next activity
                        new Handler().postDelayed(() -> {

                            if(Boolean.parseBoolean(appStatus))
                            {
                                Intent intent = new Intent(SplashScreen.this, WebActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, SPLASH_TIME_OUT);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }, error -> {
            Log.d("API:RESPONSE", error.toString());
        });

        connectAPI.add(jsonRequest);
    }
}
