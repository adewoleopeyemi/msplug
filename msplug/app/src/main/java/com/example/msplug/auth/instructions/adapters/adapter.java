package com.example.msplug.auth.instructions.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.msplug.R;
import com.example.msplug.auth.instructions.models.Model;

import java.util.List;

public class adapter extends PagerAdapter {
    private List<Model> models;
    private LayoutInflater LayoutInflater;
    private Context context;

    public adapter(List<Model> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater = LayoutInflater.from(context);
        View view = LayoutInflater.inflate(R.layout.instruction_item, container, false);
        TextView title, desc;
        title = view.findViewById(R.id.title);
        desc = view.findViewById(R.id.desc);

        title.setText(models.get(position).getTitle());
        desc.setText(models.get(position).getDesc());
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
