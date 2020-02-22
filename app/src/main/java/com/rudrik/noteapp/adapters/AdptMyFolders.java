package com.rudrik.noteapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rudrik.noteapp.R;
import com.rudrik.noteapp.activities.NotesActivity;
import com.rudrik.noteapp.models.Folder;
import com.tr4android.recyclerviewslideitem.SwipeAdapter;
import com.tr4android.recyclerviewslideitem.SwipeConfiguration;

import java.util.List;

import static com.rudrik.noteapp.MyApplication.SEL_FOLDER;
import static com.rudrik.noteapp.MyApplication.bg;
import static com.rudrik.noteapp.MyApplication.db;

public class AdptMyFolders extends SwipeAdapter {

    Context context;
    List<Folder> list;

    public AdptMyFolders(Context context, List<Folder> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyFolderViewHolder onCreateSwipeViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_folder, parent, true);

        return new MyFolderViewHolder(v);
    }

    @Override
    public void onBindSwipeViewHolder(RecyclerView.ViewHolder viewHolder, int pos) {
        if (!list.isEmpty()) {
            if (viewHolder instanceof MyFolderViewHolder) {

                MyFolderViewHolder vh = (MyFolderViewHolder) viewHolder;

                Folder folder = list.get(pos);

                vh.cardFolder.setOnClickListener(v -> {
                    Intent i = new Intent(context, NotesActivity.class);
                    i.putExtra(SEL_FOLDER, list.get(pos));
                    context.startActivity(i);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                });

                vh.tvFolderName.setText(folder.getfName());

                vh.tvTotalNotes.setText(String.valueOf(folder.totalNotes));
            }
        }
    }

    @Override
    public int getItemCount() {
//        Log.e("LIST SIZE: ", String.valueOf(list.size()));
        return list.size();
    }

    @Override
    public SwipeConfiguration onCreateSwipeConfiguration(Context context, int position) {
        Interpolator interpolator = new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return 2;
            }
        };

        if (!list.isEmpty()) {
            return new SwipeConfiguration.Builder(context)
//                    .setRightDrawableResource(R.drawable.ic_search)
                    .setLeftDrawableResource(R.drawable.ic_delete)
                    .setSwipeBehaviour(10, null)
                    .build();
        }
        return null;
    }

    @Override
    public void onSwipe(int position, int direction) {
        if (!list.isEmpty()) {
            if (direction == -1) {
                //  delete
                System.out.println("delete");
                bg.execute(() -> {
                    db.foldersDao().deleteFolder(list.get(position));
                });
            }

            if (direction == 1) {
                //  edit
//                Intent i = new Intent(context, MapsActivity.class);
//                String strPlace = new Gson().toJson(myFolders.get(position));
//                i.putExtra("PLACE", strPlace);
//                context.startActivity(i);


            }
        }
    }


    class MyFolderViewHolder extends RecyclerView.ViewHolder {

        CardView cardFolder;
        TextView tvFolderName;
        TextView tvTotalNotes;

        public MyFolderViewHolder(@NonNull View iv) {
            super(iv);

            cardFolder = (CardView) iv.findViewById(R.id.cardNote);
            tvFolderName = (TextView) iv.findViewById(R.id.tvNoteName);
            tvTotalNotes = (TextView) iv.findViewById(R.id.tvTotalNotes);
        }

    }
}

