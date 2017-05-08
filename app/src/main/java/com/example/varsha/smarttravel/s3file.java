package com.example.varsha.smarttravel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import com.amazonaws.services.s3.AmazonS3;
import java.io.File;
import java.io.FileNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferProgress;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Varsha on 4/29/2017.
 */

public class s3file extends AppCompatActivity {

    File fileToUpload;
    File fileToDownload ;
    AmazonS3 s3;
    TransferUtility transferUtility;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recommendation);

        // callback method to call credentialsProvider method.
        credentialsProvider();

        // callback method to call the setTransferUtility method
        setTransferUtility();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    public void credentialsProvider(){

        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:82069ea0-32b2-4dab-8f83-031dca964cef", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        setAmazonS3Client(credentialsProvider);
    }

    /**
     *  Create a AmazonS3Client constructor and pass the credentialsProvider.
     * @param credentialsProvider
     */
    public void setAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider){

        // Create an S3 client
        s3 = new AmazonS3Client(credentialsProvider);

        // Set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.US_WEST_1));
    }

    public void setTransferUtility(){
        transferUtility = new TransferUtility(s3, getApplicationContext());
    }

    /**
     * This method is used to upload the file to S3 by using TransferUtility class
     * @param view
     */

     public void setFileToUpload(View view) throws FileNotFoundException {

        fileToUpload = new File(getExternalFilesDir(null), "coupons.csv");

        TransferObserver transferObserver = transferUtility.upload(
                "rmrketbasket",     /* The bucket to upload to */
                "coupons.csv",    /* The key for the uploaded object */
                fileToUpload       /* The file where the data to upload exists */
        );

        transferObserverListener(transferObserver);
         Toast.makeText(getApplicationContext(), "Pushed Successfully!", Toast.LENGTH_LONG).show();

     }

    /**
     *  This method is used to Download the file to S3 by using transferUtility class
     * @param view
     **/

    public void setFileToDownload(View view) {

        System.out.print("inside download");
        fileToDownload = new File(getExternalFilesDir(null), "Rules_.csv");

        TransferObserver transferObserver = transferUtility.download(
                "rmrketbasket",     /* The bucket to download from */
                "coupons.csv",    /* The key for the object to download */
                fileToDownload        /* The file to download the object to */
        );

        transferObserverListener(transferObserver);
        System.out.println("File path" +fileToDownload.toString());
        Intent intent=new Intent(this, csvmain.class);
        startActivity(intent);
    }

/*    public final List<String[]> readCsv() {
       List<String[]> questionList = new ArrayList<String[]>();
         AssetManager assetManager = mcontext.getAssets();

         try {
              CSVReader csvReader = new CSVReader(new FileReader("path/storage/emulated/0/Android/data/app.s3amazon/files/Demo.csv"));
              InputStream csvStream = assetManager.open("path/storage/emulated/0/Android/data/app.s3amazon/files/Demo.csv");
              InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
              CSVReader csvReader = new CSVReader(csvStreamReader);
              String[] line;

              csvReader.readNext();
              while ((line = csvReader.readNext()) != null) {
                questionList.add(line);
             }
        } catch (IOException e) {
           e.printStackTrace();
         }
         return questionList;
     }*/


    public void transferObserverListener(TransferObserver transferObserver){

        transferObserver.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.e("statechange", state+"");
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent/bytesTotal * 100);
                Log.e("percentage",percentage +"");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("error","error");
            }

        });
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
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

