package in.technitab.teamapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.technitab.teamapp.R;

import in.technitab.teamapp.listener.RecyclerViewItemClickListener;
import in.technitab.teamapp.model.Project;
import in.technitab.teamapp.model.UserTask;


public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> mProjectTaskArrayList;
    private RecyclerViewItemClickListener listener;
    private Context context;
    private int viewFrom;
    private TripAdapter.TripListener mItemClickListener;
    private final int PROJECT = 1, TRIP = 2;



    public TaskAdapter(Context context, int viewFrom, ArrayList<Object> mProjectTaskArrayList) {
        this.mProjectTaskArrayList = mProjectTaskArrayList;
        this.viewFrom = viewFrom;
        this.context = context;
    }

    public void setTaskListener(RecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_expense_row, parent, false);
        return new TaskAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == PROJECT) {
            final TeamTaskAdapter.MyViewHolder myViewHolder = (TeamTaskAdapter.MyViewHolder) holder;
            Project assignProject = (Project) mProjectTaskArrayList.get(position);
            myViewHolder.projectName.setText(assignProject.getProjectName());
            String description = viewFrom == 1 ? assignProject.getCustomerName() : assignProject.getProjectType();
            myViewHolder.customerName.setText(description);
            myViewHolder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  listener.onClickListener(myViewHolder, myViewHolder.getAdapterPosition());
                }
            });
        } else {
            final TeamTaskAdapter.MyViewHolder myViewHolder = (TeamTaskAdapter.MyViewHolder) holder;
            UserTask assignProject = (UserTask) mProjectTaskArrayList.get(position);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(assignProject.getDeadlines());
            stringBuilder.append("  |   ");
            stringBuilder.append(assignProject.getPriority());
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
            return TRIP;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.project_name)
        public TextView projectName;
        @BindView(R.id.customer_name)
        public TextView customerName;
        @BindView(R.id.action_button)
        public ImageButton actionButton;
        @BindView(R.id.rootLayout)
        public RelativeLayout rootLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void SetOnItemClickListener(TripAdapter.TripListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}

