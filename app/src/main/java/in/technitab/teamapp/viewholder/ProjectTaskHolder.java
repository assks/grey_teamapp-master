package in.technitab.teamapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.technitab.teamapp.R;

public class ProjectTaskHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.project)
    public TextView project;
    @BindView(R.id.task_name)
    public TextView taskName;
    @BindView(R.id.activity)
    public TextView activity;
    @BindView(R.id.time_spent)
    public EditText timeSpent;
    @BindView(R.id.description)
    public Button description;
    @BindView(R.id.is_billable)
    public CheckBox isBillable;
    @BindView(R.id.timesheetstatus)
    public EditText timesheetStatus;


    public ProjectTaskHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
