package com.example.test;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import petrov.kristiyan.colorpicker.ColorPicker;

public class Note_Detail extends AppCompatActivity {

    DatabaseNote database;

    private Note note;

    private EditText editText_Title;
    private TextView textView_Time;
    private ImageView imageView_TimeReminder;
    private GridView gridView_Image;
    private EditText editText_Contents;
    private TextView textView_AddItem;

    private ImageView imageView_File;

    private ImageView imageView_Reminder;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;


   // private BottomNavigationView bottomNavigationView;

    private Toolbar toolBar;
    private Window window;

    //gridview image
    private static final int REQUEST_CODE_CAMERA=1000;
    private static final int REQUEST_CODE_FOLDER=1001;

    private GridView gridView;
    private GridAdapter gridAdapter;
    private ArrayList<Bitmap> bitmaps;


    private  ImageView imageView_Color;

    private  ImageView imageView_Save;

    private int Action;//sua hay them moi // 1 them 0 sua

    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note__detail);
        Init();
    }

    //region Init

    //khởi tạo
    private void Init(){
        database = new DatabaseNote(Note_Detail.this,null,null,1);
        bitmaps=new ArrayList<>();

        constraintLayout=(ConstraintLayout)findViewById(R.id.Note_Main);
        note=getIntent().getParcelableExtra("note");

        Action=getIntent().getIntExtra("EditorCreate",0);

        editText_Title = (EditText) findViewById(R.id.editText_Title);
        editText_Title.setText(note.getTitle());

        //lay thoi gian hoen tai
        textView_Time = (TextView) findViewById(R.id.textView_Time);
        if(Action==1) {

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            String dateTime = simpleDateFormat.format(calendar.getTime());
            textView_Time.setText(dateTime);


        }
        else if(Action==0) {
            textView_Time.setText(note.getTimecreate());

        }
        imageView_TimeReminder=(ImageView)findViewById(R.id.imageView_Time_Reminder);
        if(note.getTimereminder().equals(""))
            imageView_TimeReminder.setVisibility(View.GONE);
        else
            imageView_TimeReminder.setVisibility(View.VISIBLE);

        //grid view
        gridView_Image = (GridView) findViewById(R.id.gridView_Image);
        gridView = (GridView) findViewById(R.id.gridView_Image);

        //gridView.setAdapter(new GridAdapter(Note_Detail.this, bitmaps));
        getGridImageData(bitmaps);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Dialog dialog=new Dialog(Note_Detail.this);
                dialog.setContentView(R.layout.dialog_delete_image);
                ImageView gridImagDelete = (ImageView) dialog.findViewById(R.id.gridImgDelete);
                gridImagDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {




                        RemoveImage(bitmaps,position);
                        dialog.cancel();
                    }
                });
                dialog.show();
                return false;
            }
        });

        editText_Contents = (EditText) findViewById(R.id.editText_Contents);

        editText_Contents.setText(note.getContents());

        //sự kiện click vào image file
        imageView_File = (ImageView) findViewById(R.id.imageView_File);
        imageView_File.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(Note_Detail.this, imageView_File);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_file, popupMenu.getMenu());

                // hiển thị icon cho popup menu
                setForceShowIcon(popupMenu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_Camera: {
                                //ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},IMAGE_PICK_CODE);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

                                        String[] permission = {Manifest.permission.CAMERA};
                                        requestPermissions(permission, REQUEST_CODE_CAMERA);
                                    } else {
                                        takePhotoByCamera();
                                    }
                                } else {
                                    takePhotoByCamera();
                                }
                            }
                            return true;

                            case R.id.item_Gallery: {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                                        String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                                        requestPermissions(permission, REQUEST_CODE_FOLDER);
                                    } else {
                                        pickImageFromGallery();
                                    }
                                } else {
                                    pickImageFromGallery();
                                }
                            }
                            return true;


                            default:
                                return false;
                        }
                    }
                });

                popupMenu.show();


            }
        });

        imageView_Save=(ImageView) findViewById(R.id.imageView_Save);
        imageView_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNoteData();
                open_Mainactivity();
                finish();
            }
        });

        toolBar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);


        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        if(note.getColor()==android.graphics.Color.parseColor("#FEE1FF")) {
            //toolBar.setBackground(new ColorDrawable(getResources().getColor(R.color.white)));
            if (Build.VERSION.SDK_INT >= 21) {
                window = this.getWindow();
                window.setStatusBarColor(Color.parseColor("#854D7F"));
                toolBar.setBackgroundColor(Color.parseColor("#DE81D5"));
                imageView_Save.setColorFilter(Color.parseColor("#DE81D5"));
                constraintLayout.setBackgroundColor(Color.parseColor("#FFF7FE"));

            }
        }
        else if(note.getColor()==android.graphics.Color.parseColor("#E6FCFA")) {
            //toolBar.setBackground(new ColorDrawable(getResources().getColor(R.color.white)));
            if (Build.VERSION.SDK_INT >= 21) {
                window = this.getWindow();
                window.setStatusBarColor(Color.parseColor("#2B6D6D"));
                toolBar.setBackgroundColor(Color.parseColor("#49B6B6"));
                imageView_Save.setColorFilter(Color.parseColor("#49B6B6"));
                constraintLayout.setBackgroundColor(Color.parseColor("#E6FCFA"));

            }
        }
        else if(note.getColor()==android.graphics.Color.parseColor("#FFFA92")) {
            //toolBar.setBackground(new ColorDrawable(getResources().getColor(R.color.white)));
            if (Build.VERSION.SDK_INT >= 21) {
                window = this.getWindow();
                window.setStatusBarColor(Color.parseColor("#997D2D"));
                toolBar.setBackgroundColor(Color.parseColor("#FFD14B"));
                imageView_Save.setColorFilter(Color.parseColor("#FFD14B"));
                constraintLayout.setBackgroundColor(Color.parseColor("#FFFBEF"));

            }
        }
        else if(note.getColor()==android.graphics.Color.parseColor("#ECEFFF")) {
            //toolBar.setBackground(new ColorDrawable(getResources().getColor(R.color.white)));
            if (Build.VERSION.SDK_INT >= 21) {
                window = this.getWindow();
                window.setStatusBarColor(Color.parseColor("#59448F"));
                toolBar.setBackgroundColor(Color.parseColor("#9272EF"));
                imageView_Save.setColorFilter(Color.parseColor("#9272EF"));
                constraintLayout.setBackgroundColor(Color.parseColor("#ECEFFF"));

            }
        }
        else if(note.getColor()==android.graphics.Color.parseColor("#FFFFFF")) {
            //toolBar.setBackground(new ColorDrawable(getResources().getColor(R.color.white)));
            if (Build.VERSION.SDK_INT >= 21) {
                window = this.getWindow();
                window.setStatusBarColor(Color.parseColor("#1588CB"));
                toolBar.setBackgroundColor(Color.parseColor("#21A9F5"));
                imageView_Save.setColorFilter(Color.parseColor("#21A9F5"));
                constraintLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));

            }
        }

        // sự kiện reminder

        imageView_Reminder =  (ImageView) findViewById(R.id.imageView_Reminder);
        imageView_Reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_Reminder();
            }
        });

        //su kien color
        imageView_Color =  (ImageView) findViewById(R.id.imageView_Color);
        imageView_Color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseColorDialog();
            }
        });


    }
    //open main activity
    public void open_Mainactivity() {
        Intent intent=new Intent(Note_Detail.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    // add icon trong popup
    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Lấy dữ liệu note

    //lấy hình ảnh của note
    private  void getGridImageData(ArrayList<Bitmap> bitmaps) {
        //select data
        Cursor noteData = database.GetData("SELECT * FROM ImagesGridView WHERE Idnote="+note.getId()+"");
        //Toast.makeText(Note_Detail.this, String.valueOf(note.getId()), Toast.LENGTH_SHORT).show();
        bitmaps.clear();
        while (noteData.moveToNext()){
            // bye[] sang bitmap
            byte[] imageitem=noteData.getBlob(2);
            Bitmap bitmap= BitmapFactory.decodeByteArray(imageitem,0,imageitem.length);
            bitmaps.add(bitmap);
        }
        gridAdapter =new GridAdapter(Note_Detail.this,bitmaps);
        gridView.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();

    }
    //endregion

    //region Lưu note
    //Lưu thông tin note
    private  void saveNoteData(){

        note.setTitle(editText_Title.getText().toString());
        note.setContents(editText_Contents.getText().toString());
        if(bitmaps.size()==0)
            note.setImages(0);
        else  note.setImages(1);
        note.setTimecreate(textView_Time.getText().toString());
        //kieerm tra dieu kien xoa
        if(note.getTitle().equals("")&&note.getContents().equals("")/*&&note.getChecklist()==0&&note.getImages()==0*/){
            //k co du lieu
            if (Action == 1) {//neu la them, thi xoa du lieu
                database.DeleteData(note.getId());
                DeleteReminder();
            }
            else if(Action==0) { }
        }
        else {
            database.UpdateData(note);
            database.DeleteImages(note.getId());
            saveGridImage();
        }
    }
    //Lưu hình ảnh có trong note
    private  void saveGridImage(){
        for (int i=0;i<bitmaps.size();i++){
            Bitmap bmp = getResizedBitmap(bitmaps.get(i),500);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            bmp.recycle();
            database.InsertImage(note.getId(),byteArray);
        }
    }

    //endregion

    //region Thêm hình cho note
    //gridview image
    private void  pickImageFromGallery(){

        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Choose image"),REQUEST_CODE_FOLDER);
    }

    private void takePhotoByCamera(){

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(Intent.createChooser(intent,"Camera"),REQUEST_CODE_CAMERA);
    }

    private void addImagetoGridView(Bitmap b, ArrayList<Bitmap> bitmaps){
        bitmaps.add(b);
        gridAdapter =new GridAdapter(Note_Detail.this,bitmaps);
        gridView.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_CODE_FOLDER:{
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED) {

                    pickImageFromGallery();
                }
                else{
                    Toast.makeText(Note_Detail.this, "Denied", Toast.LENGTH_SHORT).show();
                }
            }
            case REQUEST_CODE_CAMERA:{
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    takePhotoByCamera();
                }
                else{
                    Toast.makeText(Note_Detail.this, "Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK&&requestCode==REQUEST_CODE_FOLDER&& data!=null){
            ClipData clipData=data.getClipData();
            if(clipData!=null){
                for(int i=0;i<clipData.getItemCount();i++) {
                    Uri uri= clipData.getItemAt(i).getUri();
                    try {
                        InputStream inputStream=getContentResolver().openInputStream(uri);
                        Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                        addImagetoGridView(bitmap,bitmaps);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                Uri uri= data.getData();
                try {
                    InputStream inputStream=getContentResolver().openInputStream(uri);
                    Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                    addImagetoGridView(bitmap,bitmaps);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
        if (resultCode==RESULT_OK&&requestCode==REQUEST_CODE_CAMERA && data!=null){
            Bitmap bitmap=(Bitmap) data.getExtras().get("data");
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 450, 450, true);
            addImagetoGridView(resized,bitmaps);
        }

    }

    //remove image in gridview
    public void RemoveImage(ArrayList<Bitmap> bitmaps,int position) {
        bitmaps.remove(position);
        gridAdapter =new GridAdapter(Note_Detail.this,bitmaps);
        gridView.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();
    }
    //resize bitmap
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    public int getScreenWidth(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return width-90;
    }
    //endregion

    //region Thay đổi màu note
    public void chooseColorDialog(){
        final ColorPicker colorPicker =new ColorPicker(Note_Detail.this);
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

                                    note.setColor(Color.parseColor("#FEE1FF"));
                                    saveNoteChangeTheme();

                                    finish();
                                    Intent starterIntent= getIntent();
                                    startActivity(starterIntent);
                                    break;

                                }
                            case 1:
                                if(Build.VERSION.SDK_INT>=21){

                                    note.setColor(Color.parseColor("#E6FCFA"));
                                    saveNoteChangeTheme();

                                    finish();
                                    Intent starterIntent= getIntent();
                                    startActivity(starterIntent);
                                    break;

                                }
                            case 2:
                                if(Build.VERSION.SDK_INT>=21){

                                    note.setColor(Color.parseColor("#FFFA92"));
                                    saveNoteChangeTheme();

                                    finish();
                                    Intent starterIntent= getIntent();
                                    startActivity(starterIntent);
                                    break;

                                }
                            case 3:
                                if(Build.VERSION.SDK_INT>=21){

                                    note.setColor(Color.parseColor("#ECEFFF"));
                                    saveNoteChangeTheme();
                                    finish();
                                    Intent starterIntent= getIntent();
                                    startActivity(starterIntent);
                                    break;

                                }
                            case 4:
                                if(Build.VERSION.SDK_INT>=21){

                                    note.setColor(Color.parseColor("#FFFFFF"));
                                    saveNoteChangeTheme();
                                    finish();
                                    Intent starterIntent= getIntent();
                                    startActivity(starterIntent);
                                    break;

                                }
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();
    }
    private  void saveNoteChangeTheme(){
        note.setTitle(editText_Title.getText().toString());
        note.setContents(editText_Contents.getText().toString());
        if(bitmaps.size()==0)
            note.setImages(0);
        else  note.setImages(1);
        note.setTimecreate(textView_Time.getText().toString());
        database.UpdateData(note);
        database.DeleteImages(note.getId());
        saveGridImage();
    }
    //endregion

    //region Nhắc nhở cho note
    //dialog reminder
    public  void Dialog_Reminder(){
        final Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.dialog_reminder);

        final TextView textView_DateReminder=( TextView)dialog.findViewById(R.id.textView_DateReminder);
        final TextView textView_TimeReminder=( TextView)dialog.findViewById(R.id.textView_TimeReminder);

        final TextView textView_DeleteReminder=( TextView)dialog.findViewById(R.id.textView_DeleteReminder);
        TextView textView_CancelReminder=( TextView)dialog.findViewById(R.id.textView_CancelReminder);
        TextView textView_SaveReminder=( TextView)dialog.findViewById(R.id.textView_SaveReminder);



        //lấy thời gian cho textview
        calendar=Calendar.getInstance();
        if(note.getTimereminder().equals("")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

            String dateTime = simpleDateFormat.format(calendar.getTime());
            //underline text

            textView_DateReminder.setText(dateTime);
            simpleDateFormat = new SimpleDateFormat("hh:mm a");
            dateTime = simpleDateFormat.format(calendar.getTime());
            //underline text

            textView_TimeReminder.setText(dateTime);

        }
        else{
            //SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a dd/MM/yyyy");

            try {
                Date date =sdf.parse(note.getTimereminder());
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

            String dateTime = simpleDateFormat.format(calendar.getTime());
            //underline text

            textView_DateReminder.setText(dateTime);

            simpleDateFormat = new SimpleDateFormat("hh:mm a");
            dateTime = simpleDateFormat.format(calendar.getTime());
            //underline text

            textView_TimeReminder.setText(dateTime);

            textView_DeleteReminder.setVisibility(View.VISIBLE);

        }




        //show Timepickerdialog
        textView_TimeReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog=new TimePickerDialog(Note_Detail.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                        String formatedTime = sdf.format(calendar.getTime());

                        textView_TimeReminder.setText(formatedTime);
                    }
                },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }


        });

        //show datepicker dialog

        textView_DateReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(Note_Detail.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String formatedDate = sdf.format(calendar.getTime());


                        textView_DateReminder.setText(formatedDate);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

            }

        });

        //remainder manager

        textView_CancelReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  CancelReminder();
                dialog.cancel();
            }
        });
        dialog.show();
        textView_SaveReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a dd/MM/yyyy");
                String timeReminder = sdf.format(calendar.getTime());

                note.setTimereminder(timeReminder);
                imageView_TimeReminder.setVisibility(View.VISIBLE);
                Toast.makeText(Note_Detail.this, "Set reminder at "+timeReminder, Toast.LENGTH_SHORT).show();
                StarReminder();
                //saveNoteData();
                dialog.cancel();
            }
        });
        textView_DeleteReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                note.setTimereminder("");
                imageView_TimeReminder.setVisibility(View.GONE);
                Toast.makeText(Note_Detail.this, "Deleted reminder", Toast.LENGTH_SHORT).show();
                DeleteReminder();
                // saveNoteData();
                dialog.cancel();
            }
        });
    }
    public void StarReminder(){

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Note_Detail.this, ReminderReceiver.class);
        intent.putExtra("idNote",note.getId());
        intent.putExtra("title",note.getTitle());
        intent.putExtra("contents",note.getContents());
        pendingIntent = PendingIntent.getBroadcast(Note_Detail.this, note.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
    public void DeleteReminder(){
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Note_Detail.this, ReminderReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(Note_Detail.this, note.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }
    //endregion

    //region Override
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_detail_action_mode,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_Delete:
                AlertDialog.Builder Xoa= new AlertDialog.Builder(this);
                Xoa.setMessage("Delete your note?");
                Xoa.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //database.QueryData("DELETE FROM Notes WHERE Id ='"+Id+"'");
                        database.DeleteData(note.getId());
                        database.DeleteImages(note.getId());

                        open_Mainactivity();
                        Toast.makeText(Note_Detail.this,"Deleted",Toast.LENGTH_SHORT).show();
                    }
                });
                Xoa.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                Xoa.show();


                return true;

            //sự kiện button back
            case android.R.id.home:
                saveNoteData();
                open_Mainactivity();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveNoteData();
        open_Mainactivity();
    }

    //endregion
}
