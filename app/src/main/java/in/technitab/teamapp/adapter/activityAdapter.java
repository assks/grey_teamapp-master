package in.technitab.teamapp.adapter;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.technitab.teamapp.R;
import in.technitab.teamapp.listener.RecyclerViewItemClickListener;

import in.technitab.teamapp.model.User_Activity;


public class activityAdapter extends RecyclerView.Adapter<in.technitab.teamapp.adapter.activityAdapter.ViewHolder> {

    private ArrayList<User_Activity> mProjectTaskArrayList;
    private RecyclerViewItemClickListener listener;
    private Context context;

    public activityAdapter(Context context, ArrayList<User_Activity> mProjectTaskArrayList) {
        this.mProjectTaskArrayList = mProjectTaskArrayList;
        this.context = context;
    }

    public void setTaskListener(RecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public in.technitab.teamapp.adapter.activityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_add_to_do, parent, false);
        return new in.technitab.teamapp.adapter.activityAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final in.technitab.teamapp.adapter.activityAdapter.ViewHolder holder, int position) {
        final User_Activity user_activity = mProjectTaskArrayList.get(position);
        holder.projectName.setText(user_activity.getName());
        // holder.customerName.setText(user_activity.getCustomerName());

        holder.billableDuration.setText(user_activity.getEstimatedHours());
        //  holder.totalTimesheetDuration.setText(user_activity.getTotalTimesheetDuration());
        //   holder.projectProfit.setText(context.getResources().getString(R.string.project_profitability,user_activity.getProjectProfit()));
        //   holder.tecClaim.setText(context.getResources().getString(R.string.remove_decimal,user_activity.getTecClaimExpense()));
        //   holder.bookingClaim.setText(context.getResources().getString(R.string.remove_decimal,user_activity.getBookingExpense()));
          /*  holder.actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickListener(holder,holder.getAdapterPosition());
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(context, AddToDoFragment.class);
                    i.putExtra("id",User_Activity.getProjectId());
                    context.startActivity(i);
                }
            });*/


    }


    @Override
    public int getItemCount() {
        return mProjectTaskArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.project_name)
        public TextView projectName;
     /*   @BindView(R.id.customer_name)
        public TextView customerName;*/
        @BindView(R.id.total_timesheet_duration)
        public TextView totalTimesheetDuration;
        @BindView(R.id.billable_duration)
        public TextView billableDuration;
        /*@BindView(R.id.tec_claim)
        public TextView tecClaim;*/
       /* @BindView(R.id.booking_claim)
        public TextView bookingClaim;*/
       /* @BindView(R.id.project_profit)
        public TextView projectProfit;*/
        @BindView(R.id.action_button)
        public ImageButton actionButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}