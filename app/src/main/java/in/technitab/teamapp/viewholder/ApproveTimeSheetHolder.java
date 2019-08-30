package in.technitab.teamapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.technitab.teamapp.R;

public class ApproveTimeSheetHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.name)
    public TextView name;
    @BindView(R.id.date)
    public TextView date;
    @BindView(R.id.task_name)
    public TextView taskName;
    @BindView(R.id.time_spent)
    public EditText timeSpent;
    @BindView(R.id.description)
    public ImageView description;
    @BindView(R.id.is_billable)
    public CheckBox isBillable;

    public ApproveTimeSheetHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
