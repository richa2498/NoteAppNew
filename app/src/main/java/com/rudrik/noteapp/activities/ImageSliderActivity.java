package com.rudrik.noteapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.rudrik.noteapp.R;
import com.rudrik.noteapp.adapters.SliderAdapter;
import com.rudrik.noteapp.adapters.SliderItem;
import com.rudrik.noteapp.models.Note;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.rudrik.noteapp.MyApplication.SEL_NOTE;

public class ImageSliderActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    ImageView viewImage;
    private Note selNote;

    List<SliderItem> sliderItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);

        viewPager2 = findViewById(R.id.viewPagerImageSlider);

        checkNote();

    }

    private void checkNote() {
        selNote = (Note) getIntent().getSerializableExtra(SEL_NOTE);
        if (selNote != null) {
            System.out.println("from note : " + selNote.getTitle());
        } else {
            selNote = new Note();
        }

        getImages();
    }

    private void getImages(){
        if (selNote.getImages() != null) {
            for (String path : selNote.getImages()) {
                File image = new File(path);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                if (bitmap != null) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
                    sliderItems.add(new SliderItem(bitmap));
                }
            }

            setAdapter();
        }
    }

    private void setAdapter(){
        viewPager2.setAdapter(new SliderAdapter(sliderItems,viewPager2));

        viewPager2.setClipToPadding(false );
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.20f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

            }
        });
    }
}
