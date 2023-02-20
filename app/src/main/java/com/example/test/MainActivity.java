package com.example.test;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import petrov.kristiyan.colorpicker.ColorPicker;


public class MainActivity extends AppCompatActivity {

    private DatabaseNote database;
    private RecyclerView recyclerView;
    private ArrayList<Note> noteList;
    private ActionMode actionMode;
    private RecyclerAdapter recyclerAdapter;
    private Toolbar toolbar;
    private FloatingActionButton fab_button;
    private TextView textView_Nothing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
    }
    //region Init

    private void Init(){
        textView_Nothing=(TextView)findViewById(R.id.textView_Nothing);

        noteList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        database = new DatabaseNote(this,null,null,1);
        getNoteData();

        toolbar= (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        fab_button=(FloatingActionButton)findViewById(R.id.fab_button);
        fab_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewNote();
            }
        });
        fab_button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(actionMode!=null){
                    return false;
                }
                actionMode=startSupportActionMode(mActionModeCallBack);
                return true;
            }
        });
    }

    //endregion
    //region Lấy dữ liệu từ sql

    private  void getNoteData() {
        //select data
        Cursor noteData = database.GetData("SELECT * FROM Notes ORDER BY Id DESC ");
        noteList.clear();
        while (noteData.moveToNext()){
            int Id = noteData.getInt(0);
            String Title=noteData.getString(1);
            String Contents= noteData.getString(2);
            int Images= noteData.getInt(3);
            String Timemeninder=noteData.getString(4);
            String Timecreate=noteData.getString(5);
            int Color=noteData.getInt(6);

            noteList.add(new Note(Id,Title,Contents,Images ,Timemeninder,Timecreate,Color));
        }
        if(noteList.size()!=0) textView_Nothing.setVisibility(View.GONE);
        else textView_Nothing.setVisibility(View.VISIBLE);
        recyclerAdapter =new RecyclerAdapter(this,noteList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();
        recyclerAdapter.setOnClickListener(new RecyclerAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view,  int pos) {
                if (recyclerAdapter.getSelectedItemCount() > 0) {
                    enableActionMode(pos);
                } else {
                    Note note = noteList.get(pos);
                    Intent intent=new Intent(MainActivity.this,Note_Detail.class);
                    intent.putExtra("note",note);
                    intent.putExtra("EditorCreate",0);
                    startActivity(intent);
                    ActivityCompat.finishAffinity(MainActivity.this);
                    // read the inbox which removes bold from the row

                }
            }
            @Override
            public void onItemLongClick(View view, int pos) {
                enableActionMode(pos);
            }
        });
    }
    public Bitmap getNoteImage(int Id){
        Cursor noteData = database.GetData("SELECT * FROM ImagesGridView WHERE Idnote="+Id+" ORDER BY Id ASC LIMIT 1");
        // bye[] sang bitmap
        Bitmap bitmap=null;
        while (noteData.moveToNext()) {
            byte[] imageitem=noteData.getBlob(2);
            bitmap= BitmapFactory.decodeByteArray(imageitem,0,imageitem.length);
        }
        return bitmap;

    }

    //endregion
    //region Tạo mới, xóa, thay đổi màu sắc, chọn note

    // tạo note mới
    public void createNewNote(){
        database.InsertData(new Note(0,"","",0,"","", Color.parseColor("#FFFFFF")));
        Cursor noteData = database.GetData("SELECT * FROM Notes ORDER BY Id DESC LIMIT 1");
        while (noteData.moveToNext()) {
            int Id = noteData.getInt(0);
            String Title = noteData.getString(1);
            String Contents = noteData.getString(2);
            int Images = noteData.getInt(3);
            String Timereminder = noteData.getString(4);
            String Timecreate = noteData.getString(5);
            int Color = noteData.getInt(6);
            Note newNote = new Note(Id, Title, Contents, Images,Timereminder, Timecreate, Color);

            Intent intent=new Intent(MainActivity.this,Note_Detail.class);
            intent.putExtra("note",newNote);
            intent.putExtra("EditorCreate",1);
            startActivity(intent);
            ActivityCompat.finishAffinity(this);
        }

    }
    // xóa note
    public void deleteNotes(final ActionMode mode){
        //tạo alert dialog xóa
        AlertDialog.Builder Xoa= new AlertDialog.Builder(this);
        List<Integer> selectedItemPositions = recyclerAdapter.getSelectedItems();
        Xoa.setMessage("Delete "+ String.valueOf(selectedItemPositions.size())+" notes?");
        Xoa.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<Integer> selectedItemPositions = recyclerAdapter.getSelectedItems();
                for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {

                    Note note = noteList.get(selectedItemPositions.get(i));
                    database.DeleteData(note.getId());
                    database.DeleteImages(note.getId());

                    //xóa remindeer

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(MainActivity.this, ReminderReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, note.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmManager.cancel(pendingIntent);

                }

                Toast.makeText(MainActivity.this,"Deleted",Toast.LENGTH_SHORT).show();
                getNoteData();
                mode.finish();
            }
        });
        Xoa.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        Xoa.show();
    }
    // hàm thay đổi màu sắc note
    public void chooseColor(final ActionMode mode){
        final ColorPicker colorPicker =new ColorPicker(MainActivity.this);
        ArrayList<String> colors=new ArrayList<>();
        colors.add("#DE81D5");
        colors.add("#49B6B6");
        colors.add("#FFD14B");
        colors.add("#9272EF");
        colors.add("#FFFFFF");
        colorPicker.setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        switch (position) {
                            case 0:
                                if(Build.VERSION.SDK_INT>=21){
                                    List<Integer> selectedItemPositions = recyclerAdapter.getSelectedItems();
                                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {

                                        Note note = noteList.get(selectedItemPositions.get(i));
                                        note.setColor(Color.parseColor("#FEE1FF"));
                                        database.UpdateData(note);
                                    }

                                    mode.finish();
                                    getNoteData();
                                    break;

                                }
                            case 1:
                                if(Build.VERSION.SDK_INT>=21){


                                    List<Integer> selectedItemPositions = recyclerAdapter.getSelectedItems();
                                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {

                                        Note note = noteList.get(selectedItemPositions.get(i));
                                        note.setColor(Color.parseColor("#E6FCFA"));
                                        database.UpdateData(note);
                                    }

                                    mode.finish();
                                    getNoteData();
                                    break;


                                }
                            case 2:
                                if(Build.VERSION.SDK_INT>=21){


                                    List<Integer> selectedItemPositions = recyclerAdapter.getSelectedItems();
                                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {

                                        Note note = noteList.get(selectedItemPositions.get(i));
                                        note.setColor(Color.parseColor("#FFFA92"));
                                        database.UpdateData(note);
                                    }

                                    mode.finish();
                                    getNoteData();
                                    break;
                                }
                            case 3:
                                if(Build.VERSION.SDK_INT>=21){


                                    List<Integer> selectedItemPositions = recyclerAdapter.getSelectedItems();
                                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {

                                        Note note = noteList.get(selectedItemPositions.get(i));
                                        note.setColor(Color.parseColor("#ECEFFF"));
                                        database.UpdateData(note);
                                    }

                                    mode.finish();
                                    getNoteData();
                                    break;

                                }
                            case 4:
                                if(Build.VERSION.SDK_INT>=21){


                                    List<Integer> selectedItemPositions = recyclerAdapter.getSelectedItems();
                                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {

                                        Note note = noteList.get(selectedItemPositions.get(i));
                                        note.setColor(Color.parseColor("#FFFFFF"));
                                        database.UpdateData(note);
                                    }

                                    mode.finish();
                                    getNoteData();
                                    break;

                                }
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();
    }
    // chọn nhiều note
    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(mActionModeCallBack);
        }
        toggleSelection(position);
    }
    private void toggleSelection(int position) {
        recyclerAdapter.toggleSelection(position);
        int count = recyclerAdapter.getSelectedItemCount();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    //endregion
    //region Override

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.seach_menu_new, menu);
        MenuItem menuItem = menu.findItem(R.id.search_menu);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.search_menu){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //Action mode
    private ActionMode.Callback mActionModeCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.action_mode,menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.item_DeleteActionMode: {
                    deleteNotes(mode);
                    return true;
                }
                case R.id.item_ColorActionMode: {
                    chooseColor(mode);
                    return true;
                }
                default: return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            recyclerAdapter.clearSelections();
            actionMode=null;
        }
    };
    // Thoát ứng dụng
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            ActivityCompat.finishAffinity(this);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    //endregion
}
