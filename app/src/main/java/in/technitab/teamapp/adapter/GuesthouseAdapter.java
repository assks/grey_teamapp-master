package in.technitab.teamapp.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import in.technitab.teamapp.R;
import in.technitab.teamapp.databinding.GuesthouseBookingItemBinding;
import in.technitab.teamapp.model.GuesthouseBooking;

public class GuesthouseAdapter extends RecyclerView.Adapter<GuesthouseAdapter.GuesthouseViewHolder> {

    private List<GuesthouseBooking> guesthouseBookingList;

    public GuesthouseAdapter() {
        this.guesthouseBookingList = new ArrayList<>();
    }

    public void setGuesthouseBookingList(List<GuesthouseBooking> guesthouseList) {
        this.guesthouseBookingList.addAll(guesthouseList);
        notifyDataSetChanged();
    }

    public void clearList(){
        guesthouseBookingList.clear();
    }

    @NonNull
    @Override
    public GuesthouseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        GuesthouseBookingItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.guesthouse_booking_item,viewGroup,false);
        return new GuesthouseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GuesthouseViewHolder holder, int position) {
        holder.binding.setBooking(guesthouseBookingList.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return guesthouseBookingList.size();
    }



    class GuesthouseViewHolder extends RecyclerView.ViewHolder {
        GuesthouseBookingItemBinding binding;
         GuesthouseViewHolder(GuesthouseBookingItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
