package com.licence.pocketteacher.miscellaneous;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.licence.pocketteacher.R;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class OnboardingSliderAdapterS extends PagerAdapter {

    private Context context;
    public OnboardingSliderAdapterS(Context context){
        this.context = context;
    }

    // Arrays for slides
    private int[] images = {
            R.drawable.onboarding_profile,
            R.drawable.onboarding_search ,
            R.drawable.onboarding_follow,
            R.drawable.onboarding_get_started};

    private String[] headings = {
            "PROFILE",
            "SEARCH",
            "FOLLOW",
            "GET STARTED"};

    private String[] descriptions = {
            "Set up a profile to let the world know what you are made of.",
            "You can find teachers based on either name, university, subjects of interest or domains that might be useful for you.",
            "Found someone you like? Request to follow them.\nOnce you are approved, you can access their files and see whenever they post something new. ",
            "Once you have found someone that you wish to access content from, you can request to follow them. Once they approve, you can access their files."
    };

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_onboarding, container, false);


        ImageView imageView = view.findViewById(R.id.imageView);
        TextView headingTV = view.findViewById(R.id.headingTV);
        TextView descriptionTV = view.findViewById(R.id.descriptionTV);

        imageView.setImageResource(images[position]);
        headingTV.setText(headings[position]);
        descriptionTV.setText(descriptions[position]);

        container.addView(view);


        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
