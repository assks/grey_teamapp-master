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
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import in.technitab.teamapp.R;
import in.technitab.teamapp.databinding.FragmentExpenseBinding;
import in.technitab.teamapp.listener.RecyclerViewItemClickListener;
import in.technitab.teamapp.util.ConstantVariable;
import in.technitab.teamapp.util.UserPref;
import in.technitab.teamapp.view.adapter.TecAdapter;
import in.technitab.teamapp.viewmodel.TecViewModel;


public class ExpenseFragment extends Fragment implements View.OnClickListener, RecyclerViewItemClickListener, SearchView.OnQueryTextListener, Observer {

    private FragmentExpenseBinding binding;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private TecAdapter adapter;
    private Resources resources;
    private TecViewModel tecViewModel;
    private SearchView searchView;
    private MenuItem myActionMenuItem;
    private Handler handler;
    private Runnable runnable;
    private int previousTotal = 0;
    private boolean loading = true;

    private int visibleThreshold = 25;
    private int pageNumber = 1;
    private String status = "";
    private LinearLayoutManager mLayoutManager;
    private Activity activity;
    private UserPref userPref;

    public ExpenseFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_expense, container, false);
        initResources();
        setToolbar();
        initDataBinding();
        setupListPeopleView(binding.tecRecyclerView);
        setClickListener();
        setupObserver(tecViewModel);
        return binding.getRoot();
    }

    private void setToolbar() {
        ((MainActivity) activity).setToolbar(resources.getString(R.string.submitted_tec));
        ((MainActivity) activity).setToolBarSubtitle(null);
    }

    private void initDataBinding() {
        tecViewModel = new TecViewModel(activity, pageNumber, userPref.getUserId());
        binding.setTecViewModel(tecViewModel);
    }

    private void initResources() {
        activity = getActivity();
        resources = activity.getResources();
        userPref = new UserPref(activity);
        handler = new Handler();
    }

    private void setupListPeopleView(RecyclerView listPeople) {

        adapter = new TecAdapter();
        listPeople.setAdapter(adapter);
        mLayoutManager = new LinearLayoutManager(activity);
        listPeople.setLayoutManager(mLayoutManager);
        listPeople.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        setupScrollListener();
        adapter.setListener(this);

    }

    private void setClickListener() {
        binding.all.setOnClickListener(this);
        binding.draft.setOnClickListener(this);
        binding.submit.setOnClickListener(this);
        binding.open.setOnClickListener(this);
        binding.paid.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        pageNumber = 1;
        if (view.getId() == R.id.all) {
            status = "";
            changeTecActionColor(binding.all, null, null, null, null);

        } else if (view.getId() == R.id.draft) {
            status = binding.draft.getText().toString();
            changeTecActionColor(null, binding.draft, null, null, null);

        } else if (view.getId() == R.id.submit) {
            status = binding.submit.getText().toString();
            changeTecActionColor(null, null, binding.submit, null, null);

        } else if (view.getId() == R.id.open) {
            status = binding.open.getText().toString();
            binding.open.setTextColor(resources.getColor(R.color.colorAccent));
            changeTecActionColor(null, null, null, binding.open, null);
        } else {
            status = binding.paid.getText().toString();
            changeTecActionColor(null, null, null, null, binding.paid);
        }
        adapter.clearList();
        tecViewModel.fetchFilterTec(pageNumber = 1, status);
    }

    private void changeTecActionColor(TextView all, TextView draft, TextView submit, TextView open, TextView paid) {
        binding.all.setTextColor(resources.getColor(all != null ? R.color.colorSelectedTecStatus : R.color.colorDivider));
        binding.draft.setTextColor(resources.getColor(draft != null ? R.color.colorSelectedTecStatus : R.color.colorDivider));
        binding.submit.setTextColor(resources.getColor(submit != null ? R.color.colorSelectedTecStatus : R.color.colorDivider));
        binding.open.setTextColor(resources.getColor(open != null ? R.color.colorSelectedTecStatus : R.color.colorDivider));
        binding.paid.setTextColor(resources.getColor(paid != null ? R.color.colorSelectedTecStatus : R.color.colorDivider));
    }

    private void setupScrollListener() {

        binding.tecRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = binding.tecRecyclerView.getChildCount();
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

                    tecViewModel.loadMoreTec(++pageNumber);
                    loading = true;
                }

            }
        });
    }

    public void setupObserver(Observable observable) {
        observable.addObserver(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tecViewModel.reset();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof TecViewModel) {
            TecAdapter tecAdapter = (TecAdapter) binding.tecRecyclerView.getAdapter();
            TecViewModel tecViewModel = (TecViewModel) observable;
            tecAdapter.setTecList(tecViewModel.getTecList());
        }
    }

    @Override
    public void onClickListener(RecyclerView.ViewHolder viewHolder, int position) {
        Intent intent = new Intent(activity, TecEntryActivity.class);
        intent.putExtra(ConstantVariable.MIX_ID.ACTION, ConstantVariable.MIX_ID.SUBMIT);
        intent.putExtra(ConstantVariable.Tec.ID, adapter.getTecList().get(position));
        startActivityForResult(intent, 1);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_tec, menu);
        myActionMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setQueryHint(resources.getString(R.string.search_tec));
        searchView.setIconified(true);
        EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.colorIconText));
        searchEditText.setHintTextColor(getResources().getColor(R.color.colorSecondary));
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_search) {
            return false;

        } else if (item.getItemId() == R.id.action_filter) {
            tecViewModel.hideShowFilter();

            return true;

        } else
            if (item.getItemId() == R.id.action_create_tec) {
            Intent intent = new Intent(getActivity(), ProjectActivity.class);
            intent.putExtra(ConstantVariable.MIX_ID.ACTION, resources.getString(R.string.tec));
            startActivityForResult(intent, 1);

            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapter.clearList();
        tecViewModel.refreshTec(pageNumber = 1);

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tecViewModel.reset();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            adapter.clearList();
            status = "";
            tecViewModel.fetchSearchTec(pageNumber = 1, query);

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
                status = "";
                tecViewModel.fetchSearchTec(pageNumber = 1, newText);
            }
        };
        handler.postDelayed(runnable, 500);
        return false;
    }
}
