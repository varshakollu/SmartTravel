package com.example.varsha.smarttravel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Varsha on 4/23/2017.
 */

public class OfferMain extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

        private static final String TAG = "SmartTravel";
        public static final String JOKES = "jokes";

        private String mUsername;
        private String mPhotoUrl;
        private EditText mJokeEditText;
        private Button mPostButton;

        private RecyclerView mRecyclerView;
        private LinearLayoutManager mLinearLayoutManager;
        private FirebaseRecyclerAdapter<Offer, MyJokeViewHolder> mFirebaseAdapter;
        private ProgressBar mProgressBar;
        private DatabaseReference mFirebaseDatabaseReference;
        private FirebaseAuth mFirebaseAuth;
        private FirebaseUser mFirebaseUser;

        private GoogleApiClient mGoogleApiClient;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.offer_main);

            // Initialize Firebase Auth
            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();

            if (mFirebaseUser == null) {
                // User has  not signed in, launch the Sign In activity
                startActivity(new Intent(this, Chat.class));
                finish();
                return;
            } else {
                mUsername = mFirebaseUser.getDisplayName();
                if (mFirebaseUser.getPhotoUrl() != null)
                    mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API)
                    .build();

            mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
            mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
            mLinearLayoutManager = new LinearLayoutManager(this);
            mLinearLayoutManager.setStackFromEnd(true);
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            //Adapter for Firebase RecyclerView : FirebaseRecyclerAdapter
                    mFirebaseAdapter = new FirebaseRecyclerAdapter<Offer, MyJokeViewHolder>(
                    Offer.class,
                    R.layout.item_joke_layout,
                    MyJokeViewHolder.class,
                    mFirebaseDatabaseReference.child(JOKES)) {

                @Override
                protected void populateViewHolder(MyJokeViewHolder viewHolder, final Offer joke, int position) {

                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    viewHolder.jokeTextView.setText(joke.text);
                    viewHolder.jokeAuthorTextView.setText(joke.name);
                    viewHolder.likeCount.setText(joke.likeCount + "");
                    if (joke.likesGivenBy.get(mFirebaseUser.getUid()) == null) {
                        viewHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                    } else {
                        viewHolder.likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    }
                    if (joke.photoUrl == null) {
                        viewHolder.circleImageView.setImageDrawable(ContextCompat.getDrawable(OfferMain.this,
                                R.drawable.ic_face_black_24dp));
                    } else {
                        //Loads the image in to circleImageView from the given URL
                        Glide.with(OfferMain.this)
                                .load(joke.photoUrl)
                                .into(viewHolder.circleImageView);
                    }

                    viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFirebaseDatabaseReference.child(JOKES).child(joke.offerKey).runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData currentData) {

                                    Offer joke = currentData.getValue(Offer.class);
                                    if (joke == null) {
                                        return Transaction.success(currentData);
                                    }

                                    if (joke.likesGivenBy.containsKey(mFirebaseUser.getUid())) {
                                        Log.i("SmartTravel", "User has already Liked. So it can be considered as Unliked.");
                                        joke.likeCount = joke.likeCount - 1;
                                        joke.likesGivenBy.remove(mFirebaseUser.getUid());
                                    } else {
                                        Log.i("SmartTravel", "User Liked");
                                        joke.likeCount = joke.likeCount + 1;
                                        joke.likesGivenBy.put(mFirebaseUser.getUid(), true);
                                    }

                                    currentData.setValue(joke);
                                    return Transaction.success(currentData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b,
                                                       DataSnapshot dataSnapshot) {
                                    // Transaction completed
                                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                                }
                            });
                        }
                    });
                }
            };

            mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int jokeCount = mFirebaseAdapter.getItemCount();
                    int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                    if (lastVisiblePosition == -1 ||
                            (positionStart >= (jokeCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                        mRecyclerView.scrollToPosition(positionStart);
                    }
                }
            });

            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.setAdapter(mFirebaseAdapter);

            mJokeEditText = (EditText) findViewById(R.id.post_joke_et);
            mJokeEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int j, int k) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int j, int k) {
                    if (charSequence.toString().trim().length() > 0) {
                        mPostButton.setEnabled(true);
                    } else {
                        mPostButton.setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            mPostButton = (Button) findViewById(R.id.button);
            mPostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Map<String, Boolean> likesGivenBy = new HashMap<>();
                    //Key under which a joke will be stored.
                    String key = mFirebaseDatabaseReference.child(JOKES).push().getKey();
                    Offer joke = new Offer(mFirebaseUser.getUid(), mJokeEditText.getText().toString(), mUsername,
                            mPhotoUrl, key, likesGivenBy);
                    Map<String, Object> values = joke.toMap();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/jokes/" + key, values);

                    mFirebaseDatabaseReference.updateChildren(childUpdates);
                    mJokeEditText.setText("");
                }
            });
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {

                case R.id.action_logout:
                    mFirebaseAuth.signOut();
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    mFirebaseUser = null;
                    mUsername = null;
                    mPhotoUrl = null;
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.d(TAG, "onConnectionFailed:" + connectionResult);
        }

        public static class MyJokeViewHolder extends RecyclerView.ViewHolder {
            public TextView jokeTextView;
            public TextView jokeAuthorTextView;
            public CircleImageView circleImageView;
            public ImageButton likeButton;
            public TextView likeCount;

            public MyJokeViewHolder(View v) {
                super(v);
                jokeTextView = (TextView) itemView.findViewById(R.id.jokeTextView);
                jokeAuthorTextView = (TextView) itemView.findViewById(R.id.jokeAuthorTextView);
                circleImageView = (CircleImageView) itemView.findViewById(R.id.circleImageView);
                likeButton = (ImageButton) itemView.findViewById(R.id.like_button);
                likeCount = (TextView) itemView.findViewById(R.id.like_count);
            }
        }
    }