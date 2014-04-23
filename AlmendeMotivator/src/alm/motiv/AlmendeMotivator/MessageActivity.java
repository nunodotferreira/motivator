package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.models.Message;
import alm.motiv.AlmendeMotivator.models.User;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.mongodb.*;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageActivity extends Activity{
    Intent home;
    Intent k;
    private String[] mMenuOptions;
    private ListView mDrawerList;

    private ListView listView;
    private ArrayList<String> runningMessages = new ArrayList<String>();

    private ArrayList<String> nameArray = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagesmenu);

        UpdateMessages u = new UpdateMessages();
        u.execute();

        //showMessages();

        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    class GetNameThread extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            Session session = Session.getActiveSession();

            Request request = new Request(session, "me", null, HttpMethod.GET);
            Response response = request.executeAndWait();


            for (int i = 0; i < runningMessages.size(); i++){
                request = new Request(session, runningMessages.get(i), null, HttpMethod.GET);
                response = request.executeAndWait();

                if (response.getError() != null) {
                    System.out.println("NOPE");
                } else {
                    GraphUser graphUser = response.getGraphObjectAs(GraphUser.class);
                    nameArray.add(graphUser.getName());
                    System.out.println(graphUser.getName());
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String string) {
            showMessages();
        }

    }

    public void createMessage(View v){
        k = new Intent(MessageActivity.this, MessageCreateActivity.class);
        finish();
        startActivity(k);
    }

    class UpdateMessages extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection userCollection = db.getCollection("messages");
            userCollection.setObjectClass(Message.class);

            getMessages(userCollection);

            return null;
        }
        @Override
        protected void onPostExecute(String string) {
            showMessages();
        }
    }

    public void getMessages(DBCollection userCollection){

        DBObject query = QueryBuilder.start("Author").is(Cookie.getInstance().userEntryId).get();
        DBCursor myCursor = userCollection.find(query);

        while(myCursor.hasNext()){
            DBObject testObj = myCursor.next();
            runningMessages.add(testObj.get("Receiver").toString());
        }

        query = QueryBuilder.start("Receiver").is(Cookie.getInstance().userEntryId).get();
        myCursor = userCollection.find(query);

        while(myCursor.hasNext()){
            DBObject testObj = myCursor.next();
            runningMessages.add(testObj.get("Author").toString());
        }
        GetNameThread dbT = new GetNameThread();
        dbT.execute();
    }

    public void showMessages(){
        listView = (ListView) findViewById(R.id.messageList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                //android.R.layout.simple_list_item_1, runningMessages);
                android.R.layout.simple_list_item_1, nameArray);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new MessageClickListener());
    }


    private class MessageClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            //GA NAAR DE JUISTE MESSAGE
            System.out.println(runningMessages.get(position));
            String butName = runningMessages.get(position);

            k = new Intent(MessageActivity.this, MessageViewActivity.class);

                k.putExtra("challenger", Cookie.getInstance().userEntryId);
                k.putExtra("challengee", butName);

            finish();
            startActivity(k);
        }
    }

    public void selectItem(int pos){
        switch (pos){
            case 0:
                k = new Intent(MessageActivity.this, ProfileActivity.class);
                break;
            case 1:
                k = new Intent(MessageActivity.this, MessageActivity.class);
                break;
            case 2:
                k = new Intent(MessageActivity.this, ChallengeOverviewActivity.class);
                break;
            case 3:
                k = new Intent(MessageActivity.this, FriendActivity.class);
                break;
            case 4:
                FacebookManager.logout();
                k = new Intent(MessageActivity.this, FacebookMainActivity.class);
                break;
        }
        finish();
        startActivity(k);
    }
    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(MessageActivity.this, ChallengeOverviewActivity.class);
        startActivity(home);
        return;
    }

}
