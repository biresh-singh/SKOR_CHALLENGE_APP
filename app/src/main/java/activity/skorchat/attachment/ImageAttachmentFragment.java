package activity.skorchat.attachment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.root.skor.R;

import adaptor.ImageAttachmentAdapter;

/**
 * Created by mac on 10/26/17.
 */

public class ImageAttachmentFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ImageAttachmentAdapter adapter;
    private GridLayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_attachment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        initRecycler();



        return view;
    }

    private void initRecycler() {
        adapter = new ImageAttachmentAdapter(getActivity());
        mLayoutManager = new GridLayoutManager(getContext(), 5);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
    }
}
