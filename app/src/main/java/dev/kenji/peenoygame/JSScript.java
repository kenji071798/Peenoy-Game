package dev.kenji.peenoygame;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
//import com.appsflyer.AppsFlyerLib;
//import com.appsflyer.attribution.AppsFlyerRequestListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JSScript {
    SharedPreferences MyPrefs;
    private final Context context;
    private static final String TAG = "AppsFlyerLibUtil";
    public JSScript(Context context) {
        this.context = context;
        MyPrefs = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);

    }

    @JavascriptInterface
    public void postMessage(String name, String data)
    {
        if("openWindow".equals(name))
        {
            try {
                JSONObject extLink = new JSONObject(data);
                Intent newWindow = new Intent(Intent.ACTION_VIEW);
                newWindow.setData(Uri.parse(extLink.getString("url")));
                context.startActivity(newWindow);
            } catch (JSONException e)
            {
                Log.d("JS:Error", e.getMessage());
            }
        }
        else
        {
            Log.d("AFEvent:",name + "     Data: "+ data);
            RequestQueue afQueue =  Volley.newRequestQueue(context);

            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("appsflyer_id", UUID.randomUUID().toString());
                requestBody.put("eventName", name);
                requestBody.put("eventValue", data);
                requestBody.put("authentication", "03GRFaVOVovPewSQfbp8B");
                requestBody.put("endpoint", context.getPackageName());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String endPoint = "https://appsflyer.madgamingdev.com/user/event" +
                    "?appsflyer_id=" + UUID.randomUUID().toString() +
                    "&eventName=" + name +
                    "&eventValue=" + data +
                    "&authentication=" + "03GRFaVOVovPewSQfbp8B" +
                    "&endpoint=" + context.getPackageName();

            JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET,
                    endPoint, requestBody,
                    response -> {

                        Toast.makeText(context, name, Toast.LENGTH_SHORT).show();

                        ObjectMapper objectMapper = new ObjectMapper();

                        try {
                            Map<String, Object> dataMap = objectMapper.readValue(response.toString(), Map.class);
                        }
                        catch (IOException e) {
                            //Log.e(GlobalCFG.appCode+":API", e.getMessage());
                        }

                    },
                    Throwable::printStackTrace)
            {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("accept", "application/json");
                    headers.put("content-type", "application/json");
                    return headers;
                }
            };
            afQueue.add(myReq);
        }

    }

}
