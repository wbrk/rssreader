package ntktest.example.com.ntktest.presentation;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ntktest.example.com.ntktest.R;
import ntktest.example.com.ntktest.data.DataRetriever;
import ntktest.example.com.ntktest.data.RssDocument;
import ntktest.example.com.ntktest.data.RssItem;

public class FeedFragment extends Fragment {
    private static final String TAG = "FeedFragment";

    @BindView(R.id.list)
    RecyclerView recycler;

    @BindView(R.id.feed_root)
    View rootView;

    private Unbinder unbinder;

    private RecyclerView.Adapter adapter;
    private List<RssItem> data = new ArrayList<>();

    private DataRetriever retriever;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feed, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new FeedAdapter(data);
        recycler.setAdapter(adapter);

        recycler.setHasFixedSize(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        retriever = new DataRetriever.Builder()
                .from(Uri.parse("https://meduza.io/rss/all/"))
                .onSuccess(this::onLoad)
                .onError(this::onError)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        retriever.load();
    }

    @Override
    public void onStop() {
        super.onStop();
        retriever.cancel();
    }

    private void onLoad(RssDocument document) {
        data.addAll(document.getItems());
        adapter.notifyDataSetChanged();
    }

    private void onError(int code, Throwable t) {
        Log.i(TAG, "onError: " + t);
        Snackbar.make(rootView, "Error", Snackbar.LENGTH_SHORT).show();
    }
}
