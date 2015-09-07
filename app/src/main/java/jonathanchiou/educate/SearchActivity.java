package jonathanchiou.educate;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import jonathanchiou.educate.Classes.LessonMapper;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String[] alg1 = {"Getting Answers", "Variables", "PEMDAS", "Equations with Variables", "Balancing Equations",
            "Equations with Zero or Infinite Solutions", "Linear Equations and Word Problems", "System of Equations", "Inequalities"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String query = intent.getStringExtra(SearchManager.QUERY);
        List<String> list = new ArrayList<String>();
        //searching and populating arraylist with results
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            if (query != null) {
                int i = 0;
                for (i = 0; i < alg1.length; ++i)
                    if (alg1[i].toLowerCase().contains(query.toLowerCase()))
                        list.add(alg1[i]);
            }
        }

        if (list.isEmpty()) {
            setContentView(R.layout.activity_search);
            //findViewById searches the current view for the item. if it DNE, then it returns null => CRASHES
            TextView noResults = (TextView) findViewById(R.id.AStextView);
            noResults.setText("No items found containing: " + query + ".");
            return;
        }

        ArrayAdapter<String> ListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        ListView myLV = new ListView(this);
        myLV.setAdapter(ListAdapter);
        myLV.setClickable(true);
        myLV.setOnItemClickListener(this);
        setContentView(myLV);
    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        String lesson = ((TextView) v).getText().toString();
        Class activity_class = LessonMapper.getInstance().getLesson(lesson);
        if (activity_class != null)
            startActivity(new Intent(SearchActivity.this, activity_class));
        else
            Toast.makeText(getApplicationContext(), "Error: Class does not exist.", Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
