package com.example.msplug.dashboard.messages;

import android.graphics.ColorSpace;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.msplug.R;
import com.example.msplug.dashboard.messages.responses.ResponsesFragment;
import com.example.msplug.dashboard.messages.responses.models.ModelResponse;
import com.example.msplug.retrofit.client.Client;
import com.example.msplug.retrofit.endpoints.endpoint_login.apistatusupdate;
import com.example.msplug.retrofit.endpoints.endpoint_request_list.requestlistresponse;
import com.example.msplug.retrofit.endpoints.endpoint_status_update.statusbody;
import com.example.msplug.retrofit.endpoints.endpoint_status_update.statusresponse;
import com.example.msplug.retrofit.endpoints.endpoint_user_request_list.apiuserrequestlistbody;
import com.example.msplug.retrofit.endpoints.endpoint_user_request_list.userrequestlistbody;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link messagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class messagesFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    ArrayList<ModelResponse> arrayList;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public messagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment messagesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static messagesFragment newInstance(String param1, String param2) {
        messagesFragment fragment = new messagesFragment();
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
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        arrayList = new ArrayList<>();
        ArrayList<String> pagenames = new ArrayList<>();
        pagenames.add("USSD response");
        pagenames.add("SMS response");
        prepareViewPager(viewPager, arrayList);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }


    private void prepareViewPager(ViewPager viewPager, ArrayList<ModelResponse> arrayList) {
        MainAdapter adapter = new MainAdapter(getChildFragmentManager());
        ArrayList<String> pagenames = new ArrayList<>();
        pagenames.add("USSD response");
        pagenames.add("SMS response");
        ResponsesFragment fragment = new ResponsesFragment();
        for (int i=0; i<pagenames.size(); i++){
            Bundle bundle = new Bundle();
            bundle.putString("request_type", pagenames.get(i));
            fragment.setArguments(bundle);
            adapter.addFragment(fragment, pagenames.get(i));
            fragment = new ResponsesFragment();
        }

        viewPager.setAdapter(adapter);
    }

    private class MainAdapter extends FragmentPagerAdapter {
        ArrayList<String> arrayListx = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        public void addFragment(Fragment fragment, String title){
            arrayListx.add(title);
            fragmentList.add(fragment);
        }
        public MainAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return arrayListx.get(position);
        }

    }
}