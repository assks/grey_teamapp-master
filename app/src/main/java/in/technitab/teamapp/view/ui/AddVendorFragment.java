package in.technitab.teamapp.view.ui;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import java.util.Observable;
import java.util.Observer;
import in.technitab.teamapp.R;
import in.technitab.teamapp.databinding.FragmentAddVendorBinding;
import in.technitab.teamapp.util.UserPref;
import in.technitab.teamapp.view.adapter.VendorListAdapter;
import in.technitab.teamapp.viewmodel.VendorViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddVendorFragment extends Fragment implements SearchView.OnQueryTextListener, Observer {

    private FragmentAddVendorBinding binding;
    private VendorViewModel vendorViewModel;
    int pageNumber = 1;
    private SearchView searchView;
    private MenuItem myActionMenuItem;
    private Handler handler;
    private Runnable runnable;
    VendorListAdapter adapter;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private LinearLayoutManager mLayoutManager;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 25;
    private Activity activity;
    private Resources resources;
    private UserPref userPref;

    public AddVendorFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_vendor, container, false);

        initResources();
        initDataBinding();
        setToolbar();
        setupListPeopleView(binding.vendorRecyclerView);
        setupObserver(vendorViewModel);
        return binding.getRoot();

    }

    private void initResources() {
        activity = getActivity();
        resources = activity.getResources();
        userPref = new UserPref(activity);
        handler = new Handler();
    }

    private void initDataBinding() {

        vendorViewModel = new VendorViewModel(activity, pageNumber);
        binding.setMainViewModel(vendorViewModel);

        handler = new Handler();
    }

    private void setupListPeopleView(RecyclerView listPeople) {
        adapter = new VendorListAdapter();
        listPeople.setAdapter(adapter);
        mLayoutManager = new LinearLayoutManager(activity);
        listPeople.setLayoutManager(mLayoutManager);
        listPeople.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        setupScrollListener();

    }

    private void setToolbar() {
        ((MainActivity) activity).setToolbar(resources.getString(R.string.vendor));
        ((MainActivity) activity).setToolBarSubtitle(null);
    }

    private void setupScrollListener() {

        binding.vendorRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = binding.vendorRecyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                if (loading && dy > 0) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }

                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    vendorViewModel.loadMoreVendor(++pageNumber);
                    loading = true;
                }
            }
        });
    }

    public void setupObserver(Observable observable) {
        observable.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof VendorViewModel) {
            VendorViewModel vendorViewModel = (VendorViewModel) observable;
            adapter.setVendorList(vendorViewModel.getVendorList());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.vendor, menu);

        myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setQueryHint(resources.getString(R.string.search_hint));
        searchView.setIconified(false);
        EditText searchEditText =  searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.colorIconText));
        searchEditText.setHintTextColor(getResources().getColor(R.color.colorSecondary));
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (userPref.getAccessControlId() == 2) {
            MenuItem menuItem = menu.findItem(R.id.submit_vendor);
            menuItem.setTitle(resources.getString(R.string.add_vendor));
        }
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            adapter.clearList();
            previousTotal = 0;
            vendorViewModel.fetchSearchVendor(pageNumber=1, query);
        }
        myActionMenuItem.collapseActionView();
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        handler.removeCallbacks(runnable);
        runnable = new Runnable() {
            @Override
            public void run() {
                adapter.clearList();
                previousTotal = 0;
                vendorViewModel.fetchSearchVendor(pageNumber=1, newText);
            }
        };
        handler.postDelayed(runnable, 500);
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.submit_vendor) {
                startActivity(new Intent(getActivity(), VendorListActivity.class));
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        vendorViewModel.reset();
    }

}
