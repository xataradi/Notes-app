package com.example.test;

import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecyclerAdapter extends  RecyclerView.Adapter implements Filterable {

    private MainActivity context;
    private List<Note> noteList;

    private List<Note> noteListFull;
    private  OnClickListener onClickListener=null;

    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(parent.getContext());
        View view;
        if(viewType==0){
            view=layoutInflater.inflate(R.layout.row_item,parent,false);
        return new ViewHolder(view);
        }
        else {
            view=layoutInflater.inflate(R.layout.row_item_1,parent,false);
            return new ViewHolder1(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if(noteList.get(position).getImages()==1){
            Note note = noteList.get(position);
            ViewHolder viewHolder=(ViewHolder)holder;
            viewHolder.textViewTitle.setText(note.getTitle());
            viewHolder.textViewContents.setText(note.getContents());

                viewHolder.imageViewItem.setImageBitmap(context.getNoteImage(note.getId()));
                viewHolder.imageViewItem.setScaleType(ImageView.ScaleType.CENTER_CROP);

            viewHolder.row.setBackgroundColor(note.getColor());
            viewHolder.left_conner.setBackgroundColor(getLeft_conner_color(note));

            if(!note.getTimereminder().equals("")){
                viewHolder.imageViewItemReminder.setVisibility(View.VISIBLE);
                viewHolder.textViewReminder.setVisibility(View.VISIBLE);
                viewHolder.textViewReminder.setText(note.getTimereminder());
            }else {
                viewHolder.imageViewItemReminder.setVisibility(View.GONE);
                viewHolder.textViewReminder.setVisibility(View.GONE);

            }
            viewHolder.row.setActivated(selected_items.get(position, false));
            viewHolder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener == null) return;
                    onClickListener.onItemClick(v,position);
                }
            });

            viewHolder.row.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onClickListener == null) return false;
                    onClickListener.onItemLongClick(v,  position);
                    return true;
                }
            });
            toggleCheckedIcon(viewHolder, position);
        }
        else{
            Note note = noteList.get(position);
            ViewHolder1 viewHolder1=(ViewHolder1)holder;
            viewHolder1.textViewTitle.setText(note.getTitle());
            viewHolder1.textViewContents.setText(note.getContents());

            viewHolder1.row.setBackgroundColor(note.getColor());
            viewHolder1.left_conner.setBackgroundColor(getLeft_conner_color(note));

            if(!note.getTimereminder().equals("")){
                viewHolder1.imageViewItemReminder.setVisibility(View.VISIBLE);
                viewHolder1.textViewReminder.setVisibility(View.VISIBLE);
                viewHolder1.textViewReminder.setText(note.getTimereminder());
            }else {
                viewHolder1.imageViewItemReminder.setVisibility(View.GONE);
                viewHolder1.textViewReminder.setVisibility(View.GONE);

            }
            viewHolder1.row.setActivated(selected_items.get(position, false));
            viewHolder1.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener == null) return;
                    onClickListener.onItemClick(v,position);
                }
            });

            viewHolder1.row.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onClickListener == null) return false;
                    onClickListener.onItemLongClick(v,  position);
                    return true;
                }
            });
            toggleCheckedIcon1(viewHolder1, position);
        }

    }



    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public interface OnClickListener{
        void onItemClick(View view, int pos);
        void onItemLongClick(View view, int pos);
    }

    @Override
    public int getItemViewType(int position) {
        if(noteList.get(position).getImages()==1) {
            return 0;
        }
        return 1;
    }

    public RecyclerAdapter( MainActivity context,List<Note> noteList) {
        this.context=context;
        this.noteList = noteList;
        noteListFull=new ArrayList<>();
        noteListFull.addAll(noteList);
        selected_items = new SparseBooleanArray();
    }
    @Override
    public Filter getFilter() {
        return myFilter;
    }

    Filter myFilter = new Filter() {

        //Automatic on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<Note> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(noteListFull);
            } else {
                for (Note movie: noteListFull) {
                    if (movie.getContents().toLowerCase().contains(charSequence.toString().toLowerCase())||movie.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(movie);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        //Automatic on UI thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            noteList.clear();
            noteList.addAll((Collection<? extends Note>) filterResults.values);
            notifyDataSetChanged();
        }
    };
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitle;
        TextView  textViewContents;
        ImageView imageViewItem;
        ImageView imageViewItemReminder;
        TextView  textViewReminder;
        ConstraintLayout row;
        ConstraintLayout left_conner;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle=itemView.findViewById(R.id.textViewTitle);
            textViewContents=itemView.findViewById(R.id.textViewContents);
            textViewReminder=itemView.findViewById(R.id.textView_ItemTimeReminder);
            imageViewItem=itemView.findViewById(R.id.imageViewItem);
            imageViewItemReminder=itemView.findViewById(R.id.imageView_ItemReminder);
            row=itemView.findViewById(R.id.row_item);
            left_conner=itemView.findViewById(R.id.left_conner);
        }
}
    class ViewHolder1 extends RecyclerView.ViewHolder{
        TextView textViewTitle;
        TextView  textViewContents;
        ImageView imageViewItemReminder;
        TextView  textViewReminder;
        ConstraintLayout row;
        ConstraintLayout left_conner;
        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            textViewTitle=itemView.findViewById(R.id.textViewTitle);
            textViewContents=itemView.findViewById(R.id.textViewContents);
            textViewReminder=itemView.findViewById(R.id.textView_ItemTimeReminder);
            imageViewItemReminder=itemView.findViewById(R.id.imageView_ItemReminder);
            row=itemView.findViewById(R.id.row_item);
            left_conner=itemView.findViewById(R.id.left_conner);
        }
    }
    public void toggleSelection(int pos) {
        current_selected_idx = pos;
        if (selected_items.get(pos, false)) {
            selected_items.delete(pos);
        } else {
            selected_items.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selected_items.clear();
        resetCurrentIndex();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selected_items.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selected_items.size());
        for (int i = 0; i < selected_items.size(); i++) {
            items.add(selected_items.keyAt(i));
        }
        return items;
    }
    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }
   private void toggleCheckedIcon(ViewHolder holder, int position) {
       Note note = noteList.get(position);
        if (selected_items.get(position, false)) {
            holder.row.setBackgroundColor(Color.parseColor("#CDCBC5"));
            holder.left_conner.setBackgroundColor(Color.parseColor("#CDCBC5"));
            if (current_selected_idx == position) resetCurrentIndex();
        } else {
            holder.row.setBackgroundColor(note.getColor());
            holder.left_conner.setBackgroundColor(getLeft_conner_color(note));
            if (current_selected_idx == position) resetCurrentIndex();
        }
    }
    private void toggleCheckedIcon1(ViewHolder1 holder, int position) {
        Note note = noteList.get(position);
        if (selected_items.get(position, false)) {
            holder.row.setBackgroundColor(Color.parseColor("#CDCBC5"));
            holder.left_conner.setBackgroundColor(Color.parseColor("#CDCBC5"));
            if (current_selected_idx == position) resetCurrentIndex();
        } else {
            holder.row.setBackgroundColor(note.getColor());
            holder.left_conner.setBackgroundColor(getLeft_conner_color(note));
            if (current_selected_idx == position) resetCurrentIndex();
        }
    }
    private int getLeft_conner_color(Note note){
        if(note.getColor()==android.graphics.Color.parseColor("#FEE1FF")) {
                return Color.parseColor("#DE81D5");
        }
        else if(note.getColor()==android.graphics.Color.parseColor("#E6FCFA")) {
                return Color.parseColor("#49B6B6");
        }
        else if(note.getColor()==android.graphics.Color.parseColor("#FFFA92")) {
                return Color.parseColor("#FFD14B");
        }
        else if(note.getColor()==android.graphics.Color.parseColor("#ECEFFF")) {
                return Color.parseColor("#9272EF");

        }
        else if(note.getColor()==android.graphics.Color.parseColor("#FFFFFF")) {
                return Color.parseColor("#21A9F5");
        }
        return Color.parseColor("#FFFFFF");
    }
}
