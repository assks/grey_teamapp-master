package in.technitab.teamapp.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;

import in.technitab.teamapp.model.Vendor;

public class ItemVendorViewModel extends BaseObservable {
    private Vendor vendor;
    private Context context;

    public ItemVendorViewModel(Vendor vendor, Context context) {
        this.vendor = vendor;
        this.context = context;
    }

    public String getDescription() {
        return vendor.getVendorType() + " | " + vendor.getPaymentMode()+" | "+vendor.getBillingCity();
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
        this.notifyChange();
    }


    public String getDisplayName() {
        return vendor.getDisplayName();
    }


}
