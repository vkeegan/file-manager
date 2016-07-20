package vivienkeegan.csc4320.filemanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static String ROOT = "/";

    private List<File> mFileList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mCurrentDirView;
    private String currentDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentDirectory = ROOT;
        //Log.i("fff", Environment.getDataDirectory().getAbsolutePath());
        mFileList = createFileList(currentDirectory);

        mCurrentDirView = (TextView) findViewById(R.id.current_dir_view);
        mCurrentDirView.setText(currentDirectory);

        mRecyclerView = (RecyclerView) findViewById(R.id.file_recycler_view);

        // Use this to improve performance if changes in content do not change
        // the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ListAdapter(mFileList);
        mRecyclerView.setAdapter(mAdapter);
    }

    protected List<File> createFileList(String path) {
        List<File> fileList = new ArrayList<>();
        File directory = new File(path);

        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (File f : files) {
                if (f.canRead()) {
                    fileList.add(f);
                }
            }
        } else {
            Log.i("FileManager: ", "Directory does not exist");
        }

        return fileList;
    }

    public void goToParentDirectory(View v) {
        if (!currentDirectory.equals(ROOT)) {
            File current = new File(currentDirectory);
            setCurrentDirectory(current.getParent());
        }
    }

    protected boolean isDirectory(int position) {
        return mFileList.get(position).isDirectory();
    }

    protected void setCurrentDirectory(int position) {
        try {
            setCurrentDirectory(mFileList.get(position).getCanonicalPath());
        } catch (IOException ioe) {
            Log.i("FileManager_Error: ", "getCanonicalPath failed");
        }
    }

    protected void setCurrentDirectory(String canonicalPath) {
        currentDirectory = canonicalPath;
        mFileList = createFileList(currentDirectory);
        ((ListAdapter) mAdapter).setFileList(mFileList);
        mAdapter.notifyDataSetChanged();
        mCurrentDirView.setText(canonicalPath);
    }

    protected boolean deleteFile(int position) {
        if (((ListAdapter) mAdapter).deleteFile(position, this)) {
            mAdapter.notifyDataSetChanged();
           return true;
        }
        return false;
    }

    protected boolean createFolder(View v) {
        ListAdapter adapter = (ListAdapter) mAdapter;

        File newDir = new File("/temp/");

        if (!adapter.createDirectory(newDir)) {
            Toast.makeText(this, "Unable to create", Toast.LENGTH_SHORT).show();
            return false;
        }

        mFileList.add(newDir);
        mAdapter.notifyDataSetChanged();
        return true;
    }
}
