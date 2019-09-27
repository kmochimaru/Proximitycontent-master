package com.estimote.proximitycontent.adapter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.estimote.proximitycontent.R;
import com.estimote.proximitycontent.ShowActivity;
import com.estimote.proximitycontent.dialog.DateDialog;
import com.estimote.proximitycontent.dialog.TimeDialog;
import com.estimote.proximitycontent.listener.ItemClickListener;
import com.estimote.proximitycontent.log.MyLog;
import com.estimote.proximitycontent.model.ActivityModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>{

    private final List<ActivityModel> items;
    private final String TAG = "ActivityAdapter";
    Context context;
    private AlertDialog dialog;
    private View viewSnackBar;
    private Boolean option;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    // Call method in DetailActivity Class
    private CallbackInterface mCallback;
    public interface CallbackInterface{
        void onHandleOpenFileChooser();
        void onHandleEdit(ActivityModel acitivty, String id);
    }

    public ActivityAdapter(boolean option, List<ActivityModel> items, Context context) {
        this.items = items;
        this.context = context;
        viewSnackBar = ((Activity)context).findViewById(R.id.myCoordinator);
        this.option = option;

        try{
            mCallback = (CallbackInterface) context;
        }catch(ClassCastException ex){
            Log.e("ActivityAdapter","Must implement the CallbackInterface in the Activity", ex);
        }
    }

    @Override
    public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ActivityViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_activity, parent, false));
    }

    @Override
    public void onBindViewHolder(final ActivityViewHolder holder, int position) {
        final ActivityModel model = items.get(position);
        holder.txt_activity_name.setText(model.getActivity_name());
        holder.txt_date_start.setText(model.getDate_start());
        String dateStart = "";
        String currentDate = "";
        try {
             dateStart = sdf.format(sdf.parse(model.getDate_start()));
             currentDate = sdf.format(new Date());
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.d("Date", dateStart);
        Log.d("Date", currentDate);
        if(currentDate.compareTo(dateStart) > 0) {
            holder.relativeLayout.setBackgroundResource(R.drawable.red_border);
        }else {
            holder.relativeLayout.setBackgroundResource(R.drawable.green_border);
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(!isLongClick) {
                    Intent intent = new Intent(context, ShowActivity.class);
                    intent.putExtra("activity_name", items.get(position).getActivity_name());
                    intent.putExtra("description", items.get(position).getDescription());
                    intent.putExtra("date_start", items.get(position).getDate_start());
                    intent.putExtra("date_end", items.get(position).getDate_end());
                    intent.putExtra("time_start", items.get(position).getTime_start());
                    intent.putExtra("time_end", items.get(position).getTime_end());
                    intent.putExtra("image_url", items.get(position).getImage_url());
                    context.startActivity(intent);
                }
            }
        });

        holder.textViewOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(context, holder.textViewOptions);
                popup.inflate(R.menu.options_menu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                EditDialog(model);
                                break;
                            case R.id.menu_delete:
                                DeleteDialog(model);
                                break;
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });

    }

    public void EditDialog(final ActivityModel model){

        LayoutInflater inflater = LayoutInflater.from(context);
        View mView = inflater.inflate(R.layout.activity_add, null);

        final EditText input_activity_name = mView.findViewById(R.id.activity_name);
        final EditText input_description = mView.findViewById(R.id.description);
        final EditText input_date_start = mView.findViewById(R.id.date_start);
        final EditText input_time_start = mView.findViewById(R.id.time_start);
        final EditText input_date_end = mView.findViewById(R.id.date_end);
        final EditText input_time_end = mView.findViewById(R.id.time_end);
        final Button btn_choose = mView.findViewById(R.id.btnChoose);

        //Event Date & Time Dialog
        input_date_start.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    DateDialog dateDialog = new DateDialog(v);
                    FragmentTransaction ft = ((Activity) context).getFragmentManager().beginTransaction();
                    dateDialog.show(ft, "DatePicker");
                }
            }
        });

        input_date_end.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    DateDialog dateDialog = new DateDialog(v);
                    FragmentTransaction ft = ((Activity) context).getFragmentManager().beginTransaction();
                    dateDialog.show(ft, "DatePicker");
                }
            }
        });

        input_time_start.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    TimeDialog timeDialog = new TimeDialog(v);
                    FragmentTransaction ft = ((Activity) context).getFragmentManager().beginTransaction();
                    timeDialog.show(ft, "TimePicker");
                }
            }
        });

        input_time_end.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    TimeDialog timeDialog = new TimeDialog(v);
                    FragmentTransaction ft = ((Activity) context).getFragmentManager().beginTransaction();
                    timeDialog.show(ft, "TimePicker");
                }
            }
        });

        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onHandleOpenFileChooser();
            }
        });

        //Set Data EditText
        input_activity_name.setText(model.getActivity_name());
        input_description.setText(model.getDescription());
        input_date_start.setText(model.getDate_start());
        input_time_start.setText(model.getTime_start());
        input_date_end.setText(model.getDate_end());
        input_time_end.setText(model.getTime_end());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(mView).setTitle("แก้ไขกิจกรรม")
                .setPositiveButton("แก้ไข", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String strActivityName = input_activity_name.getText().toString()==null?"":input_activity_name.getText().toString();
                        String strDateStart = input_date_start.getText().toString()==null?"":input_date_start.getText().toString();
                        String strDateEnd = input_date_end.getText().toString()==null?"":input_date_end.getText().toString();
                        String strTimeStart = input_time_start.getText().toString()==null?"":input_time_start.getText().toString();
                        String strTimeEnd = input_time_end.getText().toString()==null?"":input_time_end.getText().toString();
                        String strDescription = input_description.getText().toString()==null?"":input_description.getText().toString();
                        String strUrlImage = model.getImage_url();

                        ActivityModel activity = new ActivityModel(
                                strActivityName,
                                strDateStart,
                                strDateEnd,
                                strTimeStart,
                                strTimeEnd,
                                strDescription,
                                strUrlImage);

                        if(strActivityName.equals("") ||
                                strDateStart.equals("") ||
                                strDateEnd.equals("") ||
                                strTimeStart.equals("") ||
                                strTimeEnd.equals("") ||
                                strDescription.equals("")) {
                            Snackbar.make(viewSnackBar,"กรุณากรอกข้อมูลให้ครบ",Snackbar.LENGTH_SHORT).show();
                        }else{
                            mCallback.onHandleEdit(activity, model.getId());
                        }
                    }
                })

                .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        dialog = builder.create();
        dialog.show();

    }

    public void DeleteDialog(final ActivityModel model){

        new AlertDialog.Builder(context)
                .setTitle("ลบกิจกรรม "+model.getActivity_name())
                .setMessage("คุณต้องการลบกิจกรรมนี้หรือไม่ ?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        MyLog myLog = new MyLog("Delete Activity", mAuth.getCurrentUser().getEmail());
                        myLog.addLog();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("activity").child(model.getId());
                        myRef.removeValue();
                        Snackbar.make(viewSnackBar,"ลบกิจกรรมสำเร็จ",Snackbar.LENGTH_SHORT).show();
                    }})

                .setNegativeButton("ยกเลิก", null).show();

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        TextView txt_activity_name, txt_date_start, textViewOptions;
        RelativeLayout relativeLayout;
        private ItemClickListener itemClickListener;

        public ActivityViewHolder(final View itemView){
            super(itemView);
            txt_activity_name = itemView.findViewById(R.id.txt_activity_name);
            txt_date_start = itemView.findViewById(R.id.txt_date_start);
            textViewOptions = itemView.findViewById(R.id.textViewOptions);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            if(option == true)textViewOptions.setVisibility(View.VISIBLE);
            else textViewOptions.setVisibility(View.INVISIBLE);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), true);
            return true;
        }
    }


}


