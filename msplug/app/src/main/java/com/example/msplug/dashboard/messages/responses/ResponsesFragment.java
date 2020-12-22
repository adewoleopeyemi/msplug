package com.example.msplug.dashboard.messages.responses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.ColorSpace;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.msplug.R;
import com.example.msplug.dashboard.messages.responses.adapter.AdapterResponses;
import com.example.msplug.dashboard.messages.responses.models.ModelResponse;
import com.example.msplug.retrofit.client.Client;
import com.example.msplug.retrofit.endpoints.endpoint_request_list.requestlistresponse;
import com.example.msplug.retrofit.endpoints.endpoint_user_request_list.apiuserrequestlistbody;
import com.example.msplug.utils.sharedPrefences.PreferenceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.HeaderMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResponsesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResponsesFragment extends Fragment {
    private RecyclerView recyclerView;
    ArrayList<ModelResponse> REQ;
    ProgressDialog pd;
    RelativeLayout animRel;
    LottieAnimationView msgloading;
    SwipeRefreshLayout swipeRefreshLayout;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Activity mActivity;

    public ResponsesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResponsesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResponsesFragment newInstance(String param1, String param2) {
        ResponsesFragment fragment = new ResponsesFragment();
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
        View view = inflater.inflate(R.layout.fragment_responses, container, false);
        String request_type = getArguments().getString("request_type");
        REQ = new ArrayList<>();
        animRel = view.findViewById(R.id.animRel);
        msgloading = view.findViewById(R.id.msgloading);
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorComedyBlue));
        msgloading.playAnimation();
        fetchUserRequestList(request_type);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchUserRequestList(request_type);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        recyclerView = view.findViewById(R.id.recycler_view);
        return view;
    }

    private void fetchUserRequestList(String request_type) {
        Retrofit retrofit = Client.getRetrofit("https://www.msplug.com/api/");
        apiuserrequestlistbody requestlist = retrofit.create(apiuserrequestlistbody.class);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Token "+PreferenceUtils.getToken(getContext()));
        Call<List<requestlistresponse>> call = requestlist.getRequestList(headers, PreferenceUtils.getDeviceID(getActivity()));
        call.enqueue(new Callback<List<requestlistresponse>>() {
            @Override
            public void onResponse(Call<List<requestlistresponse>> call, Response<List<requestlistresponse>> response) {
                List<requestlistresponse> resp = response.body();

                for (int i=0; i<resp.size(); i++){
                    requestlistresponse requestlist = resp.get(i);
                    if (request_type.equals("USSD response")&& requestlist.getRequest_type().equals("USSD")&&requestlist.getStatus().equals("completed")){
                        ModelResponse object = new ModelResponse();
                        object.setId(requestlist.getId());
                        object.setCommand(requestlist.getCommand());
                        object.setDevice(requestlist.getDevice());
                        object.setDevice_name(requestlist.getDevice_name());
                        object.setReceipient(requestlist.getReceipient());
                        object.setSim_slot(requestlist.getSim_slot());
                        object.setRequest_type(requestlist.getRequest_type());
                        object.setResponse_message(requestlist.getResponse_message());
                        object.setRequest_date(requestlist.getRequest_date());
                        REQ.add(object);
                    }
                    else if (request_type.equals("SMS response")&& requestlist.getRequest_type().equals("SMS")&&requestlist.getStatus().equals("completed")){
                        ModelResponse object = new ModelResponse();
                        object.setId(requestlist.getId());
                        object.setCommand(requestlist.getCommand());
                        object.setDevice(requestlist.getDevice());
                        object.setDevice_name(requestlist.getDevice_name());
                        object.setReceipient(requestlist.getReceipient());
                        object.setSim_slot(requestlist.getSim_slot());
                        object.setRequest_type(requestlist.getRequest_type());
                        object.setResponse_message(requestlist.getResponse_message());
                        object.setRequest_date(requestlist.getRequest_date());
                        REQ.add(object);
                    }
                }
                AdapterResponses adapterResponses = new AdapterResponses(getActivity(), REQ);
                LinearLayoutManager lm = new LinearLayoutManager(getActivity());
                adapterResponses.notifyDataSetChanged();
                recyclerView.setLayoutManager(lm);
                recyclerView.setAdapter(adapterResponses);
                animRel.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<requestlistresponse>> call, Throwable t) {

            }
        });
    }
}