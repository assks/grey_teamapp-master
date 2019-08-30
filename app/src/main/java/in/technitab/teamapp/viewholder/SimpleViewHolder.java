package in.technitab.teamapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.technitab.teamapp.R;

public class SimpleViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.textview)
    public TextView textView;

    public SimpleViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
