package org.naturenet.ui;

import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.naturenet.BuildConfig;
import org.naturenet.R;
import org.naturenet.data.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ProjectsFragment extends Fragment {
    MainActivity main;
    private Logger mLogger = LoggerFactory.getLogger(ProjectsFragment.class);
    private ListView mProjectsListView = null;
    private List<Project> mProjects = Lists.newArrayList();
    private DatabaseReference mFirebase = FirebaseDatabase.getInstance().getReference();
    public ProjectsFragment() {}
    public static ProjectsFragment newInstance() {
        ProjectsFragment fragment = new ProjectsFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_projects, container, false);
        main = ((MainActivity) getActivity());
        mProjectsListView = (ListView) root.findViewById(R.id.projects_list);
        readProjects();
        return root;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProjectsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                main.goToProjectActivity(mProjects.get(position));
            }
        });
    }
    private void readProjects() {
        mLogger.info("Getting projects");
        mFirebase.child(Project.NODE_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mLogger.info("Got projects, count: {}", snapshot.getChildrenCount());
                for(DataSnapshot child : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) child.getValue();
                    String id = null;
                    String icon_url = null;
                    String description = null;
                    String name = null;
                    String status = null;
                    Long latest_contribution = null;
                    Long created_at = null;
                    Long updated_at = null;
                    if (map.get("id") != null)
                        id = map.get("id").toString();
                    if (map.get("icon_url") != null)
                        icon_url = map.get("icon_url").toString();
                    if (map.get("description") != null)
                        description = map.get("description").toString();
                    if (map.get("name") != null)
                        name = map.get("name").toString();
                    if (map.get("status") != null)
                        status = map.get("status").toString();
                    if (map.get("latest_contribution") != null)
                        latest_contribution = (Long) map.get("latest_contribution");
                    if (map.get("created_at") != null)
                        created_at = (Long) map.get("created_at");
                    if (map.get("updated_at") != null)
                        updated_at = (Long) map.get("updated_at");
                    Project project = new Project(id, icon_url, description, name, status, latest_contribution, created_at, updated_at);
                    mProjects.add(project);
                }
                if (mProjects.size() != 0) {
                    mProjectsListView.setAdapter(new ProjectAdapter(main, mProjects));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                mLogger.error("Failed to read Projects: {}", databaseError.getMessage());
                Toast.makeText(main, "Could not get projects: "+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}