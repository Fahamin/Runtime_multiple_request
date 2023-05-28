package tv.iptv.livetv.android13takeimage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class MainActivity2 extends AppCompatActivity {


    boolean is_storage_image_permitted = false;
    boolean is_camera_access_permitted = false;
    Button button;
    ImageView imageView;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image_IV);
        if (!is_camera_access_permitted) {
            requestPermissonImage();
        }

        button = findViewById(R.id.takeImage_Btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (is_camera_access_permitted) {
                    startCamera();
                } else {
                    requestPermissonImage();
                }
            }
        });

    }

    public void requestPermissonImage() {
        if (ContextCompat.checkSelfPermission(this, requiredPermission[0]) == PackageManager.PERMISSION_GRANTED) {
            is_storage_image_permitted = true;
            startCamera();
        } else {
            launcherPermission.launch(requiredPermission);
        }
    }

    ActivityResultLauncher<String> permission_luncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {

                    is_camera_access_permitted = true;
                    startCamera();

                } else {
                    is_camera_access_permitted = false;

                }
            });

    ActivityResultLauncher<Intent> luncher_For_Camera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        imageView.setImageURI(imageUri);

                    }
                }
            });

    private void startCamera() {
        /*Intent intent = new Intent(this, CameraPreviewActivity.class);
        startActivity(intent);*/
        String masterName = "dd.jpg";

        //Log.e("mahtab", "startCamera: " + masterName);
        Intent m_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String imagefolderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "";

        File folder = new File(imagefolderPath);
        if (!folder.exists()) {
            File wallpaperDirectory = new File(imagefolderPath);
            wallpaperDirectory.mkdirs();
        }

        //File file = new File(folderPath, file_name + ".jpg");
        File file = new File(imagefolderPath, masterName);
        imageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        m_intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        m_intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        luncher_For_Camera.launch(m_intent);

    }


    String[] requiredPermission = new String[]

            {       Manifest.permission.CALL_PHONE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_VIDEO
            };

    ActivityResultLauncher<String[]> launcherPermission = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), granted -> {
                for (String s : granted.keySet()) {
                    Log.e("MultiplePermission", "key" + s);
                    //    Log.e("MultiplePermission","key"+s);
                }
                for (boolean s : granted.values()) {
                    Log.e("MultiplePermission", "key" + s);

                }
                if (ContextCompat.checkSelfPermission(this, requiredPermission[0]) == PackageManager.PERMISSION_GRANTED) {
                    Log.e("MultiplePermission", "phone_Call True");
                }
                else {
                   requestPermissonImage();

                }
            });

}