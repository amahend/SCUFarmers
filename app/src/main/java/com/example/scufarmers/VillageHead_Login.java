package com.example.scufarmers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class VillageHead_Login extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button Login;
    private Button FarmerLogin;
    private Button SignUp;

    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_village_head_login);

        email = (EditText)findViewById(R.id.etEmail);
        password = (EditText)findViewById(R.id.etPassword);
        Login = (Button)findViewById(R.id.btnLogin);
        FarmerLogin = (Button)findViewById(R.id.btnFarmerLogin);
        SignUp = (Button)findViewById(R.id.btnSignUp);

        mQueue = Volley.newRequestQueue(this);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String StringEmail = email.getText().toString().trim();
                String StringPassword = password.getText().toString().trim();
                System.out.println("+++++++++++");
                System.out.println(StringEmail);
                System.out.println("+++++++++++");
                returnUserCredentials(StringEmail, StringPassword);
            }
        });

        FarmerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openFarmerLogin(); }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openRegister(); }
        });
    }

    private void returnUserCredentials(String emailParameter, String passwordParameter) {
        String validateUserURL = "https://us-central1-farmers-d71d5.cloudfunctions.net/user/users";
        final Boolean[] flag = {false};

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, validateUserURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("users");
                    String userID = "";
                    String fullName = "";
                    String tempEmail = "";
                    String tempPassword = "";
                    String age = "";
                    String role = "";
                    String gender = "";
                    String village = "";
                    String loggedIn = "";
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject users = jsonArray.getJSONObject(i);

                        userID = users.getString("id");
                        fullName = users.getString("fullName");
                        tempEmail = users.getString("email");
                        tempPassword = users.getString("password");
                        age = users.getString("age");
                        role = users.getString("role");
                        gender = users.getString("gender");
                        village = users.getString("village");
                        loggedIn = users.getString("loggedIn");

//                        System.out.println("OUTSIDE");
//                        System.out.println("**********");
//                        System.out.println(emailParameter);
//                        System.out.println(tempEmail);
//                        System.out.println(emailParameter.getClass().getName());
//                        System.out.println(tempEmail.getClass().getName());
//
//                        System.out.println("**********");
//                        System.out.println(passwordParameter);
//                        System.out.println(tempPassword);
//                        System.out.println(passwordParameter.getClass().getName());
//                        System.out.println(tempPassword.getClass().getName());
//
//                        System.out.println("**********");
//                        System.out.println(role);
//                        System.out.println(role.getClass().getName());
//                        System.out.println("Farmer");

                        if (emailParameter.equals(tempEmail) && passwordParameter.equals(tempPassword) && role.equals("Village head")) {
                            System.out.println("INSIDE");
                            // flag[0] = true;
                            flag[0] = true;
                            break;
                        }

                        System.out.println(flag[0]);

                    }

                    if (!flag[0]) {
                        // Toast.makeText(VillageHead_Login.this, "Invalid credentials or user does not exist", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(VillageHead_Login.this, "Welcome " + emailParameter + "!", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(VillageHead_Login.this, VillageHead_Menu.class);
                        intent1.putExtra("USERID", userID);
                        intent1.putExtra("EMAIL", tempEmail);
                        intent1.putExtra("FULLNAME", fullName);
                        updateLoginStatusTrue(userID);
                        startActivity(intent1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(VillageHead_Login.this, "Error getting villages from db", Toast.LENGTH_SHORT).show();
            }
        });

        mQueue.add(request);

//        if (!flag[0]) {
//            Toast.makeText(VillageHead_Login.this, "Invalid credentials or user does not exist", Toast.LENGTH_LONG).show();
//        }
    }

    private void updateLoginStatusTrue(String userID) throws JSONException {
        String updateLoginStatusURL = "https://us-central1-farmers-d71d5.cloudfunctions.net/user/" + userID;
        JSONObject jsonCheckout = new JSONObject();
        jsonCheckout.put("loggedIn", "true");
        final String requestBody = jsonCheckout.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, updateLoginStatusURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        mQueue.add(stringRequest);
    }

    public void openFarmerLogin(){
        Intent intent2 = new Intent(VillageHead_Login.this, Farmer_Login.class);
        startActivity(intent2);
    }

    public void openRegister(){
        Intent intent3 = new Intent(VillageHead_Login.this, Register_Account.class);
        startActivity(intent3);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}