package org.naturenet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.naturenet.R;
import org.naturenet.data.model.Users;


public class CommunitiesFragment extends Fragment {

    public static final String FRAGMENT_TAG = "communities_fragment";
    public static final String USER_EXTRA = "user";

    private DatabaseReference mFirebase = FirebaseDatabase.getInstance().getReference();
    private ListView mCommunitiesListView = null;
    private FirebaseListAdapter mAdapterOrig, mAdapter;
    private Users user;
    private EditText searchText;
    private boolean activeSearch = false;

    MainActivity main;
    TextView toolbar_title, peopleCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_communities, container, false);
        main = (MainActivity) getActivity();
        toolbar_title = (TextView) main.findViewById(R.id.app_bar_main_tv);
        toolbar_title.setText(R.string.communities_title);
        peopleCount = (TextView) root.findViewById(R.id.people_count_tv_communities);
        searchText = (EditText) root.findViewById(R.id.searchText);

        mCommunitiesListView = (ListView) root.findViewById(R.id.communities_list);

        return root;


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Query query = mFirebase.child(Users.NODE_NAME).orderByChild("latest_contribution").limitToLast(20);
        mAdapterOrig = new UsersAdapter(main, query);
        mCommunitiesListView.setAdapter(mAdapterOrig);

        mCommunitiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //if the user has a search query entered
                if(activeSearch)
                    user = (Users) mAdapter.getItem(i); //get the clicked user from mAdapter
                else
                    user = (Users) mAdapterOrig.getItem(i); //otherwise, get the clicked user from the original adapter

                Intent userIntent = new Intent(getActivity(), UsersDetailActivity.class);
                userIntent.putExtra(USER_EXTRA, user);
                startActivity(userIntent);

            }
        });

        mFirebase.child(Users.NODE_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long i = dataSnapshot.getChildrenCount();
                peopleCount.setText("(" + String.valueOf(i) + ")");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String search = editable.toString();
                if(search.length() > 0){
                    Query q = mFirebase.child(Users.NODE_NAME).orderByChild("display_name").startAt(search).endAt(search+"\uf8ff");
                    mAdapter = new UsersAdapter(main, q);
                    mCommunitiesListView.setAdapter(mAdapter);
                    activeSearch = true;
                }else {
                    //when no search text is available, reuse original adapter
                    mCommunitiesListView.setAdapter(mAdapterOrig);
                    activeSearch = false;
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAdapter==null)
            mAdapterOrig.cleanup();
        else {
            mAdapter.cleanup();
            mAdapterOrig.cleanup();
        }
    }

}