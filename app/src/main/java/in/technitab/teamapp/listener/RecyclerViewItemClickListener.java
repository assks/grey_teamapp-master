package in.technitab.teamapp.listener;

import android.support.v7.widget.RecyclerView;

public interface RecyclerViewItemClickListener {
    void onClickListener(RecyclerView.ViewHolder viewHolder, int position);
}
