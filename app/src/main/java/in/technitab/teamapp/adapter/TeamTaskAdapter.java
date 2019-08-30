package in.technitab.teamapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.technitab.teamapp.R;

import in.technitab.teamapp.listener.RecyclerViewItemClickListener;
import in.technitab.teamapp.model.Project;
import in.technitab.teamapp.model.UserTask;
import in.technitab.teamapp.view.ui.EditTask;
import in.technitab.teamapp.view.ui.updateTask;


public class TeamTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<Object> mProjectTaskArrayList;
    private ArrayList<UserTask>mintent;
    private TripAdapter.TripListener mItemClickListener;
    private Context context;
    private int viewFrom;
    public String ass;
    private final int PROJECT = 1;


    public TeamTaskAdapter(Context context, int viewFrom, ArrayList<Object> mProjectTaskArrayList) {
        this.mProjectTaskArrayList = mProjectTaskArrayList;
        this.viewFrom = viewFrom;
        this.context = context;
        this.mintent = mintent;
    }

    public void setTaskListener(RecyclerViewItemClickListener listener) {
        RecyclerViewItemClickListener listener1 = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_expense_row, parent, false);
        return new TeamTaskAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == PROJECT) {

            final MyViewHolder myViewHolder = (MyViewHolder) holder;
            Project assignProject = (Project) mProjectTaskArrayList.get(position);

            myViewHolder.projectName.setText(assignProject.getProjectName());
            String description = viewFrom == 1 ? assignProject.getCustomerName() : assignProject.getProjectType();
            myViewHolder.customerName.setText(description);
          //  context.setListener(mItemClickListener);
            myViewHolder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  listener.onClickListener(myViewHolder, myViewHolder.getAdapterPosition());
                }
            });
        } else {
            final MyViewHolder myViewHolder = (MyViewHolder) holder;
            UserTask assignProject = (UserTask) mProjectTaskArrayList.get(position);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(assignProject.getDeadlines());
            stringBuilder.append("  |   ");
            stringBuilder.append(assignProject.getTask_deliverables());
            stringBuilder.append("  |   ");
            stringBuilder.append(assignProject.getStatus());
            stringBuilder.append("  |   ");

            stringBuilder.append(assignProject.getTask_deliverables());


            myViewHolder.projectName.setText(assignProject.getName());

            myViewHolder.customerName.setText(stringBuilder);
            myViewHolder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // listener.onClickListener(myViewHolder, myViewHolder.getAdapterPosition());
                }
            });
        }
        UserTask assignProject = (UserTask) mProjectTaskArrayList.get(position);
        ass=assignProject.getId();

        ((MyViewHolder) holder).actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu( context, ((MyViewHolder) holder).actionButton);
                popup.inflate(R.menu.task_menu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.inprogress:
                                UserTask assignProject = (UserTask) mProjectTaskArrayList.get(position);
                                 ass=assignProject.getId();
                                Log.d("usertaskid",ass);
                                Intent intent = new Intent(context, EditTask.class);
                                intent.putExtra("message", ass);
                                context.startActivity(intent);
                                return true;

                            case R.id.completed:
                                UserTask assignProject1 = (UserTask) mProjectTaskArrayList.get(position);
                                ass=assignProject1.getId();
                                Log.d("usertaskid",ass);
                                Intent i = new Intent(context, updateTask.class);
                                i.putExtra("taskid", ass);
                                context.startActivity(i);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popup.show();

            }
        });

    }



    @Override
    public int getItemCount() {
        return mProjectTaskArrayList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (mProjectTaskArrayList.get(position) instanceof Project) {
            return PROJECT;
        } else {
            return 2;
        }
    }

    public void SetOnItemClickListener(TripAdapter.TripListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.project_name)
        public TextView projectName;
        @BindView(R.id.customer_name)
        public TextView customerName;
        @BindView(R.id.action_button)
        public  Button actionButton;
        @BindView(R.id.rootLayout)
        public RelativeLayout rootLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

