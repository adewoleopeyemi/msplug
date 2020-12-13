package com.example.msplug.dashboard.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.msplug.R;
import com.example.msplug.background_service.BackgroundService;
import com.example.msplug.retrofit.client.Client;
import com.example.msplug.retrofit.endpoints.endpoint_login.apistatusupdate;
import com.example.msplug.retrofit.endpoints.endpoint_status_update.statusbody;
import com.example.msplug.retrofit.endpoints.endpoint_status_update.statusresponse;
import com.example.msplug.utils.sharedPrefences.PreferenceUtils;

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
    private Switch toggleBtn;
    private LottieAnimationView dummyAnim;
    private static final int REQUEST_PHONE_CALL = 1;

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
        toggleBtn = view.findViewById(R.id.toggleBtn);
        dummyAnim = view.findViewById(R.id.dummy_anim);

        if (PreferenceUtils.getStatus(getActivity()) == "online" ){
            toggleBtn.setChecked(true);
        }
        else{
            toggleBtn.setChecked(false);
        }
        dummyAnim.playAnimation();
        toggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    service_off = false;
                    startBackgroundService("Connected");
                    PreferenceUtils.saveStatus("online", getContext());
                }
                else{
                    service_off = true;
                    stopBackgroundService("Disconnected");
                    PreferenceUtils.saveStatus("offline", getContext());
                }
            }
        });
        return view;
    }


    private void updateStatus(String status){
        Retrofit retrofit = Client.getRetrofit("https://www.msplug.com/api/");
        apistatusupdate statusUpdate = retrofit.create(apistatusupdate.class);

        statusbody statusbody = new statusbody();
        statusbody.setStatus(status);

        Call<statusresponse> call = statusUpdate.updateStatus("PB0WB6J6",statusbody);
        call.enqueue(new Callback<statusresponse>() {
            @Override
            public void onResponse(Call<statusresponse> call, Response<statusresponse> response) {
                statusresponse resp = (statusresponse) response.body();
                String respJson = resp.toString();
                if (status.equals("offline")){
                    Toast.makeText(getActivity(), "Device disconnected", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "Connected with "+resp.getName(), Toast.LENGTH_SHORT).show();
                }

                Log.d("Home Fragment", "onResponse: "+resp.getName());
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


    private void stopBackgroundService(String status) {
        updateStatus("offline");
        Intent serviceIntent = new Intent(getActivity(), BackgroundService.class);
        //ContextCompat.stopForegroundService(getActivity(), serviceIntent);
        getActivity().stopService(serviceIntent);
        Toast.makeText(getActivity(), "device disconnected", Toast.LENGTH_SHORT).show();
    }

    private void startBackgroundService(String status) {
        updateStatus("online");
        Intent serviceIntent = new Intent(getActivity(), BackgroundService.class);
        serviceIntent.putExtra(ONLINE_STATUS, status);
        ContextCompat.startForegroundService(getActivity(), serviceIntent);
        //getActivity().startService(serviceIntent);
    }
}