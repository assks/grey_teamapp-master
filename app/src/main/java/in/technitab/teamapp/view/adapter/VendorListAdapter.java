package in.technitab.teamapp.view.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import in.technitab.teamapp.R;
import in.technitab.teamapp.databinding.VendorItemBinding;
import in.technitab.teamapp.listener.RecyclerViewItemClickListener;
import in.technitab.teamapp.model.Vendor;
import in.technitab.teamapp.viewmodel.ItemVendorViewModel;

public class VendorListAdapter extends RecyclerView.Adapter<VendorListAdapter.VendorListViewHolder> {

    private List<Vendor> vendorList;
    private RecyclerViewItemClickListener listener;

    public VendorListAdapter() {
        this.vendorList = new ArrayList<>();
    }

    public void setListener(RecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public void setVendorList(List<Vendor> vendorList) {
        this.vendorList.addAll(vendorList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VendorListAdapter.VendorListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        VendorItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.vendor_item, viewGroup, false);
        return new VendorListViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final VendorListAdapter.VendorListViewHolder holder, int position) {
        holder.bindVendor(vendorList.get(position));
        holder.binding.executePendingBindings();
        holder.binding.vendorItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClickListener(holder, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }

    public void clearList() {
        vendorList.clear();
        notifyDataSetChanged();
    }

    class VendorListViewHolder extends RecyclerView.ViewHolder {
        VendorItemBinding binding;

        VendorListViewHolder(VendorItemBinding binding) {
            super(binding.vendorItem);
            this.binding = binding;
        }

        void bindVendor(Vendor vendor) {
            if (binding.getVendorListViewModel() == null) {
                binding.setVendorListViewModel(new ItemVendorViewModel(vendor, itemView.getContext()));
            } else {
                binding.getVendorListViewModel().setVendor(vendor);
            }
        }
    }
}
