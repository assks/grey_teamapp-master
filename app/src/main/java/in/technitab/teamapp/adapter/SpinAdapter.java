package in.technitab.teamapp.adapter;



import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import in.technitab.teamapp.R;
import in.technitab.teamapp.model.BookingMode;
import in.technitab.teamapp.model.Customer;
import in.technitab.teamapp.model.ProjectActivity;
import in.technitab.teamapp.model.TaskSpinner;
import in.technitab.teamapp.model.Urgency;
import in.technitab.teamapp.model.User;
import in.technitab.teamapp.model.fetch_hotal_pojo;
import in.technitab.teamapp.model.userproject;


public class SpinAdapter extends ArrayAdapter<Object> {
    private List<Object> values;

    public SpinAdapter(Context context, int textViewResourceId,
                       List<Object> values) {
        super(context, textViewResourceId, values);
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setPadding(2, label.getPaddingTop(), label.getPaddingRight(), label.getPaddingBottom());
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, parent.getContext().getResources().getDimension(R.dimen.mediumTextSize));

        label.setTextColor(Color.BLACK);
        if (values.get(position) instanceof String) {
            String value = (String) values.get(position);
            label.setText(value);
        } else if (values.get(position) instanceof Customer) {
            Customer customer = ((Customer) values.get(position));
            label.setText(customer.getCustomerName());
        } else if (values.get(position) instanceof BookingMode) {
            BookingMode bookingMode = (BookingMode) values.get(position);
            label.setText(bookingMode.getTitle());
        } else if (values.get(position) instanceof Urgency) {
            Urgency urgency = (Urgency) values.get(position);
            label.setText(urgency.getName());
        } else if (values.get(position) instanceof TaskSpinner) {
            TaskSpinner taskSpinner = (TaskSpinner) values.get(position);
            label.setText(taskSpinner.getName());
        } else if (values.get(position) instanceof ProjectActivity) {
            ProjectActivity projectActivity = (ProjectActivity) values.get(position);
            label.setText(projectActivity.getActivityType());
        }else if (values.get(position) instanceof User) {
            User user = (User) values.get(position);
            label.setText(user.getName());
        }else if (values.get(position) instanceof fetch_hotal_pojo) {
            fetch_hotal_pojo hotel = (fetch_hotal_pojo) values.get(position);
            label.setText(hotel.getContactName());
        }else if (values.get(position) instanceof userproject) {
            userproject project = (userproject) values.get(position);
            label.setText(project.getProjectName());
        }
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, parent.getContext().getResources().getDimension(R.dimen.mediumTextSize));
        if (values.get(position) instanceof String) {
            String value = (String) values.get(position);
            label.setText(value);
        } else if (values.get(position) instanceof Customer) {
            Customer customer = ((Customer) values.get(position));
            label.setText(customer.getCustomerName());
        } else if (values.get(position) instanceof BookingMode) {
            BookingMode bookingMode = (BookingMode) values.get(position);
            label.setText(bookingMode.getTitle());
        } else if (values.get(position) instanceof Urgency) {
            Urgency urgency = (Urgency) values.get(position);
            label.setText(urgency.getName());
        } else if (values.get(position) instanceof TaskSpinner) {
            TaskSpinner taskSpinner = (TaskSpinner) values.get(position);
            label.setText(taskSpinner.getName());
        } else if (values.get(position) instanceof ProjectActivity) {
            ProjectActivity projectActivity = (ProjectActivity) values.get(position);
            label.setText(projectActivity.getActivityType());
        }else if (values.get(position) instanceof User) {
            User user = (User) values.get(position);
            label.setText(user.getName());
        } else if (values.get(position) instanceof fetch_hotal_pojo) {
            fetch_hotal_pojo hotel = (fetch_hotal_pojo) values.get(position);
            label.setText(hotel.getContactName());
        } else if (values.get(position) instanceof userproject) {
            userproject hotel = (userproject) values.get(position);
            label.setText(hotel.getProjectName());
        }
        return label;
    }
}

