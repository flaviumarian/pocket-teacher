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

public class OnboardingSliderAdapterT extends PagerAdapter {

    private Context context;

    public OnboardingSliderAdapterT(Context context){
        this.context = context;
    }

    // Arrays for slides
    public int[] images = {
            R.drawable.onboarding_profile,
            R.drawable.onboarding_upload_files,
            R.drawable.onboarding_follow,
            R.drawable.onboarding_get_started};

    private String[] headings = {
            "PROFILE",
            "FILES",
            "FOLLOWERS",
            "GET STARTED"};

    private String[] descriptions = {
            "Set up a profile so students can learn more about you and the things you can provide for them.",
            "Upload and manage files for the subjects you have and share them with the world!",
            "Approve or delete follow requests from students in order to allow or deny access to your own files or allow anyone by setting your profile public.",
            "Organize your files in subjects and let everyone know about them!"
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
