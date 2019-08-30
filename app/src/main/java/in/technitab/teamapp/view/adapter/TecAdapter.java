package in.technitab.teamapp.view.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.technitab.teamapp.R;
import in.technitab.teamapp.databinding.ItemTecBinding;
import in.technitab.teamapp.listener.RecyclerViewItemClickListener;
import in.technitab.teamapp.model.Tec;

public class TecAdapter extends RecyclerView.Adapter<TecAdapter.ItemTecViewHolder> {

    private Context mContext;
    private List<Tec> mTecList;
    private RecyclerViewItemClickListener listener;

    public TecAdapter() {
        this.mTecList = new ArrayList<>();
    }

    public void setTecList(List<Tec> mTecList) {
        this.mTecList.addAll(mTecList);
        notifyDataSetChanged();
    }

    public void setListener(RecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemTecViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        ItemTecBinding itemTecBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_tec, parent, false);
        return new ItemTecViewHolder(itemTecBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemTecViewHolder mainHolder, int position) {

        Tec tec = mTecList.get(position);
        mainHolder.binding.fromTo.setText(getYMDDate(tec.getClaimStartDate(), tec.getClaimEndDate()));
        mainHolder.binding.userNote.setText(mContext.getResources().getString(R.string.user_note_value, tec.getUserNote()));
        mainHolder.binding.remark.setText(mContext.getResources().getString(R.string.admin_note, tec.getRemark()));
        mainHolder.binding.remark.setVisibility(tec.getRemark().isEmpty() ? View.GONE : View.VISIBLE);
        mainHolder.binding.userNote.setVisibility(tec.getUserNote().isEmpty() ? View.GONE : View.VISIBLE);

        mainHolder.binding.status.setVisibility(tec.getUserNote().isEmpty() && tec.getRemark().isEmpty() ? View.GONE : View.VISIBLE);

        if (!tec.getStatus().equalsIgnoreCase(mContext.getResources().getString(R.string.draft))) {
            mainHolder.binding.date.setText(getDate(tec.getSubmitDate()));
            mainHolder.binding.month.setText(getMonth(tec.getSubmitDate()));
        } else {
            mainHolder.binding.date.setText("-");
            mainHolder.binding.month.setText("");
        }
        mainHolder.binding.type.setText(tec.getProjectName());
        String description = "Trip: " + tec.getTripId() + " | Tec " + tec.getTecId() + " | " + tec.getStatus() + " | " + mContext.getResources().getString(R.string.double_value, tec.getTotalAmount());

        mainHolder.binding.description.setText(description);

        mainHolder.binding.itemTec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClickListener(mainHolder, mainHolder.getAdapterPosition());
                }
            }
        });

        mainHolder.binding.executePendingBindings();

    }


    @Override
    public int getItemCount() {
        return mTecList.size();
    }

    private String getDate(String startDate) {
        SimpleDateFormat stringFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
        String d = "";
        try {
            Date date = stringFormat.parse(startDate);
            d = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }


    private String getMonth(String startDate) {
        SimpleDateFormat stringFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM", Locale.getDefault());
        String d = "";
        try {
            Date date = stringFormat.parse(startDate);
            d = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    private String getYMDDate(String claimStartDate, String claimEndDate) {
        String strClaimStart, strClaimEnd;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM", Locale.getDefault());
        try {
            Date d1 = simpleDateFormat.parse(claimStartDate);
            if (claimEndDate.equalsIgnoreCase("0000-00-00")) {
                strClaimEnd = "-";
            } else {
                Date d2 = simpleDateFormat.parse(claimEndDate);
                strClaimEnd = dateFormat.format(d2);
            }

            strClaimStart = dateFormat.format(d1);

        } catch (ParseException e) {
            e.printStackTrace();
            strClaimStart = "-";
            strClaimEnd = "-";
        }

        return mContext.getResources().getString(R.string.tec_claim_start_end, strClaimStart, strClaimEnd);
    }

    public void clearList() {
        mTecList.clear();
        notifyDataSetChanged();
    }

    public List<Tec> getTecList() {
        return mTecList;
    }

    public class ItemTecViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemTecBinding binding;

        ItemTecViewHolder(ItemTecBinding binding) {
            super(binding.itemTec);
            this.binding = binding;
            binding.status.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (binding.descriptionLayout.getVisibility() == View.GONE) {
                binding.descriptionLayout.setVisibility(View.VISIBLE);
                binding.status.animate().rotation(180).start();
            } else {
                binding.descriptionLayout.setVisibility(View.GONE);
                binding.status.animate().rotation(0).start();
            }
        }
    }
}
