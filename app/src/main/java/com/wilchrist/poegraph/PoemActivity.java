package com.wilchrist.poegraph;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class PoemActivity extends AppCompatActivity {

    public static final String SELECTED_POEM = "com.wilchrist.poegraph.SELECTED_POEM";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private RequestQueue requestQueue;

    EditText txtTitle;
    EditText txtContent;
    ImageView imageView;
    String randomImgUrl="";
    boolean changeImage =false;
    private Poem poem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poem);
        requestQueue = Volley.newRequestQueue(this);
        getRandomImageUrl();

        FirebaseUtility.poemCollectionName = getResources().getString(R.string.poem_collection_name);
        FirebaseUtility.openFbReference(FirebaseUtility.poemCollectionName, this);
        mFirebaseDatabase = FirebaseUtility.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtility.mDatabaseReference;

        txtTitle = (EditText) findViewById(R.id.txtTitle);
        txtContent = (EditText) findViewById(R.id.txtContent);
        imageView = (ImageView) findViewById(R.id.ivPoemPicture);

        Intent intent = getIntent();
        Poem poem = (Poem) intent.getSerializableExtra(SELECTED_POEM);
        if(poem == null) {
            poem = new Poem();
        }
        this.poem = poem;
        txtTitle.setText(poem.getTitle());
        txtContent.setText(poem.getContent());
        String imgUrl="";
        imgUrl= poem.getImageUrl();
        imgUrl = (imgUrl!=null && imgUrl.isEmpty() == false)? imgUrl : randomImgUrl;
        poem.setImageUrl(imgUrl);
        showPicture(imgUrl);

        if(this.poem.getId()!=null && !FirebaseUtility.isConnectedUserOwnerOfPoem(poem)){
            enableEditTexts(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Poem Saved", Toast.LENGTH_LONG).show();
                cleanPoem();
                backToList();
                return true;
            case R.id.delete_menu:
                deletePoem();
                Toast.makeText(this, "Poem removed", Toast.LENGTH_LONG).show();
                backToList();
                return true;
            case R.id.change_image:
                getRandomImageUrl();
                changeImage =true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void showPicture(String url) {
        if (url != null && url.isEmpty() == false) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get().load(url)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(imageView);
        }
    }
    private void enableEditTexts(boolean isEnabled) {
        txtTitle.setEnabled(isEnabled);
        txtContent.setEnabled(isEnabled);
    }

    private void cleanPoem() {
        txtTitle.setText("");
        txtContent.setText("");

        txtTitle.requestFocus();
    }

    private boolean saveDeal() {
        poem.setTitle(txtTitle.getText().toString());
        poem.setContent(txtContent.getText().toString());
        FirebaseUser firebaseUser = FirebaseUtility.getCurrentlyConnectedUser();
        if(firebaseUser!=null){
            poem.setUserId(firebaseUser.getUid());
        }else {
            Toast.makeText(this, "You must be connected to be able to do that", Toast.LENGTH_LONG).show();
            return false;
        }

        if (poem.getId() == null) {
            mDatabaseReference.push().setValue(poem);
        }
        else
        {
            mDatabaseReference.child(poem.getId()).setValue(poem);
        }
        return true;
    }

    private void backToList() {
        Intent intent = new Intent(this, PoemListActivity.class);
        startActivity(intent);
    }

    private void deletePoem() {
        if (poem == null) {
            Toast.makeText(this, "Please save the menu before deleting", Toast.LENGTH_LONG).show();
            return;
        }
        mDatabaseReference.child(poem.getId()).removeValue();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        if(poem.getId()!=null && !FirebaseUtility.isConnectedUserOwnerOfPoem(poem)){
            MenuItem menuItem = menu.findItem(R.id.save_menu);
            menuItem.setVisible(false);
            //menuItem.getIcon().setAlpha(130);
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.change_image).setVisible(false);
        }
        return true;
    }

    public void getRandomImageUrl(){
        String url="http://www.splashbase.co/api/v1/images/random?images_only=true";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response: ",  response.toString());
                        try {
                            randomImgUrl=response.getString("url");
                            if(((txtTitle.getText()==null || txtTitle.getText().toString().isEmpty()) && (txtContent.getText()==null || txtContent.getText().toString().isEmpty()))){
                                showPicture(randomImgUrl);
                                poem.setImageUrl(randomImgUrl);
                                Toast.makeText(getApplicationContext(),"May you be inspired by this one!", Toast.LENGTH_LONG).show();
                            }else{
                                if(changeImage){
                                    Toast.makeText(getApplicationContext(),"You can't change image once you have started writing", Toast.LENGTH_LONG).show();
                                    changeImage=false;
                                }
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("Error", error.toString());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}
