package com.example.msplug.dashboard.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.msplug.R;
import com.example.msplug.auth.login.loginActivity;
import com.example.msplug.background_service.BackgroundService;
import com.example.msplug.retrofit.client.Client;
import com.example.msplug.retrofit.endpoints.endpoint_status_update.apistatusupdate;
import com.example.msplug.retrofit.endpoints.endpoint_status_update.statusbody;
import com.example.msplug.retrofit.endpoints.endpoint_status_update.statusresponse;
import com.example.msplug.retrofit.endpoints.update_phone_details.apiupdatephonedetails;
import com.example.msplug.retrofit.endpoints.update_phone_details.updatebody;
import com.example.msplug.utils.sharedPrefences.PreferenceUtils;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.msplug.utils.Constants.serviceStatusConstants.ONLINE_STATUS;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeFragment extends Fragment {
    //Variable used to check the state of toggle button
    private boolean service_off = true;
    private SwitchCompat toggleBtn;
    private LottieAnimationView dummyAnim;
    private static final int REQUEST_PHONE_CALL = 1;
    ImageView logout;

    ConnectivityManager cm;
    Boolean stopped;
    Boolean started;
    Boolean permToStart = true;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public homeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment homeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static homeFragment newInstance(String param1, String param2) {
        homeFragment fragment = new homeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS},1);
        }

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        stopped = false;
        started = false;
        toggleBtn = view.findViewById(R.id.toggleBtn);
        dummyAnim = view.findViewById(R.id.dummy_anim);
        logout = view.findViewById(R.id.logoutIv);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setMessage("Do you want to logout?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Deletes token
                        updateStatus("offline");
                        PreferenceUtils.saveToken(null, getContext());
                        PreferenceUtils.saveDeviceID(null, getContext());
                        stopBackgroundService("offline", true);
                        Intent i = new Intent(getActivity(), loginActivity.class);
                        startActivity(i);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }


        });


        if (PreferenceUtils.getStatus(getActivity())!= null && PreferenceUtils.getStatus(getActivity()).equals("online")){
            toggleBtn.setChecked(true);
            dummyAnim.playAnimation();
            if (!isMyServiceRunning(BackgroundService.class)){
                startBackgroundService("Connected");
            }

        }
        else{
            toggleBtn.setChecked(false);
            dummyAnim.pauseAnimation();
        }


        toggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    dummyAnim.playAnimation();
                    service_off = false;
                    permToStart = true;
                    Handler handler = new Handler();
                    Runnable periodicUpdate = new Runnable() {
                            @Override
                            public void run() {
                                NetworkInfo ni = cm.getActiveNetworkInfo();
                                if (ni != null && ni.isConnectedOrConnecting() && permToStart){
                                    if (!started){
                                        startBackgroundService("Connected");
                                        started = true;
                                        stopped = false;
                                    }
                                }
                                else{
                                    if (!stopped){
                                        stopBackgroundService("Disconneted", false);
                                        stopped = true;
                                        started = false;
                                        permToStart = true;
                                    }
                                }
                                handler.postDelayed(this, 1000);
                            }

                        };
                        periodicUpdate.run();

                        PreferenceUtils.saveStatus("online", getContext());

                }
                else if (!isChecked){
                    dummyAnim.pauseAnimation();
                    service_off = true;
                    stopBackgroundService("Disconnected", true);
                    PreferenceUtils.saveStatus("offline", getContext());
                    stopped = true;
                    started = false;
                    permToStart = false;
                }
            }
        });
        return view;
    }


    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    @SuppressLint({"MissingPermission", "NewApi"})
    private void updatePhoneDetails(){
        List<SubscriptionInfo> subscriptionInfos = SubscriptionManager.from(getContext()).getActiveSubscriptionInfoList();
        String sim_1 = (String) subscriptionInfos.get(0).getCarrierName();
        String sim_2;
        try{
            sim_2 = (String) subscriptionInfos.get(1).getCarrierName();
        }
        catch (Exception e){
            sim_2 = "Empty";
        }

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Token "+PreferenceUtils.getToken(getContext()));

        String name = getDeviceName();
        updatebody updatebody = new updatebody();
        updatebody.setName(name);
        updatebody.setSim_1(sim_1);
        updatebody.setSim_2(sim_2);
        Retrofit retrofit = Client.getRetrofit("https://www.msplug.com/api/");
        apiupdatephonedetails phonedets = retrofit.create(apiupdatephonedetails.class);
        Call<statusresponse> call = phonedets.updatephone(headers, PreferenceUtils.getDeviceID(getContext()),updatebody);
        call.enqueue(new Callback<statusresponse>() {
            @Override
            public void onResponse(Call<statusresponse> call, Response<statusresponse> response) {
                statusresponse resp = (statusresponse) response.body();
            }

            @Override
            public void onFailure(Call<statusresponse> call, Throwable t) {

            }
        });

    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private void updateStatus(String status){
        Retrofit retrofit = Client.getRetrofit("https://www.msplug.com/api/");
        apistatusupdate statusUpdate = retrofit.create(apistatusupdate.class);

        statusbody statusbody = new statusbody();
        statusbody.setStatus(status);


        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Token "+PreferenceUtils.getToken(getContext()));

        Call<statusresponse> call = statusUpdate.updateStatus(headers, PreferenceUtils.getDeviceID(getContext()),statusbody);
        call.enqueue(new Callback<statusresponse>() {
            @Override
            public void onResponse(Call<statusresponse> call, Response<statusresponse> response) {
                statusresponse resp = (statusresponse) response.body();
                String name = resp.getName();
                updatePhoneDetails();
                String respJson = resp.toString();

            }

            @Override
            public void onFailure(Call<statusresponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
        }
    }


    private void stopBackgroundService(String status, boolean stopService) {
        updateStatus("offline");
        Intent serviceIntent = new Intent(getActivity(), BackgroundService.class);
        if (!stopService){
            getActivity().stopService(serviceIntent);
            serviceIntent = new Intent(getActivity(), BackgroundService.class);
            serviceIntent.putExtra(ONLINE_STATUS, status);
            ContextCompat.startForegroundService(getActivity(), serviceIntent);

        }
        else if (stopService){
            getActivity().stopService(serviceIntent);
        }

        //ContextCompat.stopForegroundService(getActivity(), serviceIntent);
        //Toast.makeText(getActivity(), "device disconnected", Toast.LENGTH_SHORT).show();
    }

    private void startBackgroundService(String status) {
        updateStatus("online");
        Intent serviceIntent = new Intent(getActivity(), BackgroundService.class);
        serviceIntent.putExtra(ONLINE_STATUS, status);
        ContextCompat.startForegroundService(getActivity(), serviceIntent);
        //getActivity().startService(serviceIntent);
    }
}