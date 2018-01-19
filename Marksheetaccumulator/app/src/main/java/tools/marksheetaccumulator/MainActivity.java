package tools.marksheetaccumulator;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tools.marksheetaccumulator.dao.MarksheetDao;
import tools.marksheetaccumulator.entity.MarksheetEntity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<MarksheetEntity> marksheetList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewMarksheetActivity();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MarksheetDatabaseOpenHelper dbHelper = MarksheetDatabaseOpenHelper.createInstance(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // setup for ListView
        ListView listView = (ListView)findViewById(R.id.marksheetList);
        MarksheetAdapter adapter = new MarksheetAdapter(getApplicationContext(), marksheetList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListViewItemClick(parent, view, position, id);
            }
        });
        registerForContextMenu(listView);

        setTitle(getString(R.string.app_name));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        switch(v.getId()){
            case R.id.marksheetList:
                getMenuInflater().inflate(R.menu.activity_main_marksheet, menu);
                break;
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        MarksheetEntity entity = marksheetList.get(info.position);

        switch(item.getItemId()) {
            case R.id.menu_ansert:
                openMarksheetActivity(entity.id);
                break;

            case R.id.menu_register_answer:
                break;

            case R.id.menu_marksheet_config:
                break;

            case R.id.menu_delete:
                final long marksheetId = entity.id;
                final int listPosition = info.position;

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("削除しますか？\n" + entity.title)
                        .setTitle("削除確認");

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MarksheetDatabaseOpenHelper dbHelper = MarksheetDatabaseOpenHelper.getInstance();
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        try {
                            db.beginTransaction();
                            MarksheetDao dao = new MarksheetDao(db);
                            dao.deleteMarksheet(marksheetId);
                            db.setTransactionSuccessful();

                            marksheetList.remove(listPosition);
                            getMarksheetAdapter().notifyDataSetChanged();
                        } finally {
                            db.endTransaction();
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        loadMarksheet();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMarksheet();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private MarksheetAdapter getMarksheetAdapter() {
        ListView listView = (ListView)findViewById(R.id.marksheetList);
        return (MarksheetAdapter)listView.getAdapter();
    }

    private void loadMarksheet() {
        MarksheetDatabaseOpenHelper dbHelper = MarksheetDatabaseOpenHelper.getInstance();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.beginTransaction();
            MarksheetDao dao = new MarksheetDao(db);
            List<MarksheetEntity> list = dao.getMarksheetList();

            marksheetList.clear();
            marksheetList.addAll(list);
            getMarksheetAdapter().notifyDataSetChanged();

        } catch(Exception e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
        }
    }

    private void openNewMarksheetActivity() {
        openMarksheetActivity(MarksheetActivity.NEW_MARKSHEET_ID);
    }

    private void openMarksheetActivity(Long id) {
        Intent intent = new Intent(MainActivity.this, MarksheetActivity.class);
        intent.putExtra("marksheet_id", id);
        startActivity(intent);
    }

    private void onListViewItemClick(AdapterView<?> parent, View view, int position, long id) {
        MarksheetEntity entity = marksheetList.get(position);
        openMarksheetActivity(entity.id);
    }

    private boolean onListViewItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        MarksheetEntity entity = marksheetList.get(position);
        final long marksheetId = entity.id;
        final int listPosition = position;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("削除しますか？\n"+entity.title)
                .setTitle("削除確認");

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MarksheetDatabaseOpenHelper dbHelper = MarksheetDatabaseOpenHelper.getInstance();
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    db.beginTransaction();
                    MarksheetDao dao = new MarksheetDao(db);
                    dao.deleteMarksheet(marksheetId);
                    db.setTransactionSuccessful();

                    marksheetList.remove(listPosition);
                    getMarksheetAdapter().notifyDataSetChanged();
                } finally {
                    db.endTransaction();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }
}
