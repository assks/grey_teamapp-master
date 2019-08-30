package in.technitab.teamapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.technitab.teamapp.R;

public class TecMainHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.rootLayout)
    public RelativeLayout rootLayout;
    @BindView(R.id.dateLayout)
    public LinearLayout dateLayout;
    @BindView(R.id.date)
    public TextView date;
    @BindView(R.id.status)
    public ImageView status;
    @BindView(R.id.month)
    public TextView month;
    @BindView(R.id.description)
    public TextView description;
    @BindView(R.id.type)
    public TextView type;

    public TecMainHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
