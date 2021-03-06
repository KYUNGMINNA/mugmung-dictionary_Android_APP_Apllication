package com.android.mungmung_dictionary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.mungmung_dictionary.ui.Description.Dog_Food_Allergy;
import com.android.mungmung_dictionary.ui.Description.Dog_Food_Dry;
import com.android.mungmung_dictionary.ui.Description.Dog_Food;
import com.android.mungmung_dictionary.ui.My_Dog_Information.My_Dog_Information;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public class Regist extends AppCompatActivity {

    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(Regist.this, modelPath));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    public MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength); //READ_ONLY
    }

    private static final String TAG = "RegisterActivity";
    EditText mEmailText, mPasswordText, mPasswordcheckText, mName, dogage, dogweight;
    //?????????, ??????, ?????? ??????, ??????, ???????????????, ??????????????????
    Button mregisterBtn;
    TextView dogtype;
    ImageView dogimage;
    private FirebaseAuth firebaseAuth;
    CheckBox porkck , beefck, chichkenck, sheepck, rabbitck, fishck, eggck, tanck, fruitck, vegeck, beanck;

    private static final int FROM_ALBUM=1;
    int requestCode ;
    int resultCode;
    Intent data;

    StorageReference storageref,imageref,spaceref;

    Uri imageUri;
    boolean isImageAdded = false;


    DatabaseReference Dataref;
    StorageReference StorageRef;

    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_regist);
        firebaseAuth =  FirebaseAuth.getInstance();
        mEmailText = findViewById(R.id.emailEt);
        mPasswordText = findViewById(R.id.passwordEdt);
        mPasswordcheckText = findViewById(R.id.passwordcheckEdt);
        mregisterBtn = findViewById(R.id.register2_btn);
        mName = findViewById(R.id.nameEt);
        dogage = findViewById(R.id.dog_age);
        dogweight = findViewById(R.id.dog_weight);
        dogimage = findViewById(R.id.imageView);
        dogtype = findViewById(R.id.txttype);

        porkck = (CheckBox)findViewById(R.id.porkck);
        beefck = (CheckBox)findViewById(R.id.beefck);
        chichkenck = (CheckBox)findViewById(R.id.chickenck);
        sheepck = (CheckBox)findViewById(R.id.checkBox4);
        rabbitck = (CheckBox)findViewById(R.id.checkBox5);
        fishck = (CheckBox)findViewById(R.id.checkBox6);
        eggck = (CheckBox)findViewById(R.id.checkBox7);
        tanck = (CheckBox)findViewById(R.id.checkBox8);
        fruitck = (CheckBox)findViewById(R.id.checkBox9);
        vegeck =(CheckBox)findViewById(R.id.checkBox10);
        beanck = (CheckBox)findViewById(R.id.checkBox11);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        Dataref  = FirebaseDatabase.getInstance().getReference("Users");
        StorageRef = FirebaseStorage.getInstance().getReference().child("dogImage");

        layout = findViewById(R.id.layout);

        dogimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent();
                intent.setType("image/*");                      // ????????????
                intent.setAction(Intent.ACTION_GET_CONTENT);    // ?????????(ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, FROM_ALBUM);

            }
        });

        //???????????? ???????????????   -->  firebase??? ???????????? ????????????.
        mregisterBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                final String imageName = dogtype.getText ().toString ();

                //?????? ?????? ????????????
                final String email = mEmailText.getText().toString().trim();
                String pwd = mPasswordText.getText().toString().trim();
                String pwdcheck = mPasswordcheckText.getText().toString().trim();


                if(pwd.equals(pwdcheck)) {
                    Log.d(TAG, "?????? ?????? " + email + " , " + pwd);
                    final ProgressDialog mDialog = new ProgressDialog(Regist.this);
                    mDialog.setMessage("??????????????????...");
                    mDialog.show();

                    //????????????????????? ???????????? ????????????
                    firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(Regist.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //?????? ?????????
                            if (task.isSuccessful()) {
                                mDialog.dismiss();

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String email = user.getEmail();
                                String uid = user.getUid();
                                String name = mName.getText().toString().trim();
                                String age =dogage.getText().toString().trim();
                                String weight=dogweight.getText().toString().trim();
                                String type=dogtype.getText().toString().trim();


                                // String check1, check2, check3;
                                String pork = "", beef="", chicken="",rabbit="", fish="", sheep="", egg="", tan="", fruit="", vege="", bean="" ;
                                if(porkck.isChecked()){
                                    pork = porkck.getText().toString();
                                }
                                if(beefck.isChecked()){
                                    beef=beefck.getText().toString();
                                }
                                if(chichkenck.isChecked()){
                                    chicken=chichkenck.getText().toString();
                                }
                                if(sheepck.isChecked()){
                                    sheep=sheepck.getText().toString();
                                }
                                if(rabbitck.isChecked()){
                                    rabbit=rabbitck.getText().toString();
                                }
                                if(fishck.isChecked()){
                                    fish=fishck.getText().toString();
                                }
                                if(eggck.isChecked()){
                                    egg=eggck.getText().toString();
                                }
                                if(tanck.isChecked()){
                                    tan=tanck.getText().toString();
                                }
                                if(fruitck.isChecked()){
                                    fruit=fruitck.getText().toString();
                                }
                                if(vegeck.isChecked()){
                                    vege=vegeck.getText().toString();
                                }
                                if(beanck.isChecked()){
                                    bean=beanck.getText().toString();
                                }

                                //????????? ???????????? ?????????????????? ????????????????????? ??????
                                HashMap<Object,String> hashMap = new HashMap<>();

                                hashMap.put("uid",uid);
                                hashMap.put("email",email);
                                hashMap.put("name",name);
                                hashMap.put("age",age);
                                hashMap.put("weight",weight);
                                hashMap.put("type",type);
                                hashMap.put("pork",pork);
                                hashMap.put("beef",beef);
                                hashMap.put("chicken",chicken);
                                hashMap.put("rabbit",rabbit);
                                hashMap.put("fish",fish);
                                hashMap.put("sheep",sheep);
                                hashMap.put("egg",egg);
                                hashMap.put("tan",tan);
                                hashMap.put("fruit",fruit);
                                hashMap.put("vege",vege);
                                hashMap.put("bean",bean);

                                //fragment??? ????????? ?????????
                                My_Dog_Information fragment = new My_Dog_Information();
                                Dog_Food_Allergy regfragment = new Dog_Food_Allergy();
                                Dog_Food dogFoodMain = new Dog_Food();
                                Dog_Food_Dry dogFoodDry = new Dog_Food_Dry();

                                Bundle bundle = new Bundle();
                                bundle.putString("email",email);
                                bundle.putString("dog_age",age);
                                bundle.putString("dog_weight",weight);
                                bundle.putString("dog_type",type);
                                bundle.putString("uid",uid);
                                bundle.putString("pork",pork);
                                bundle.putString("beef",beef);
                                bundle.putString("chicken",chicken);
                                bundle.putString("rabbit",rabbit);
                                bundle.putString("fish",fish);
                                bundle.putString("sheep",sheep);
                                bundle.putString("egg",egg);
                                bundle.putString("tan",tan);
                                bundle.putString("fruit",fruit);
                                bundle.putString("vege",vege);
                                bundle.putString("bean",bean);
                                fragment.setArguments(bundle);
                                regfragment.setArguments(bundle);
                                dogFoodMain.setArguments(bundle);
                                dogFoodDry.setArguments(bundle);

                                String key = Dataref.push().getKey();
                                StorageRef.child(uid + ".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        StorageRef.child(uid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                //HashMap hashMap = new HashMap();
                                                hashMap.put("dogtype", imageName);
                                                hashMap.put("ImageUrl", uri.toString());

                                                //??? ?????? fragment??? ????????? ?????????
                                                bundle.putString("dog_image",uri.toString());
                                                fragment.setArguments(bundle);

                                                Dataref.child(uid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Regist.this, "Data Successfully Uploaded!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");
                                reference.child(uid).setValue(hashMap);

                                //????????? ?????????????????? ?????? ????????? ????????????.
                                Intent intent = new Intent(Regist.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(Regist.this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();

                            } else {
                                mDialog.dismiss();
                                Toast.makeText(Regist.this, "?????? ???????????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                                return;  //?????? ????????? ????????? ????????? ????????????.

                            }

                        }
                    });

                    //???????????? ?????????
                }else{

                    Toast.makeText(Regist.this, "??????????????? ???????????????. ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // ???????????? ????????? ?????? ????????? ?????? ????????? ????????? ????????? ????????? ???????????? ??????
        super.onActivityResult(requestCode, resultCode, data);
        //if (requestCode != FROM_ALBUM || resultCode != RESULT_OK){
        if(requestCode ==FROM_ALBUM && data!=null){
            imageUri = data.getData();
            isImageAdded = true;
            dogimage.setImageURI(imageUri);
        }


        float[][][][] input = new float[1][64][64][3];
        float[][] output = new float[1][15]; //?????? 3???

        int i;


        try {
            int batchNum = 0;
            InputStream buf = getContentResolver().openInputStream(data.getData());
            Bitmap bitmap = BitmapFactory.decodeStream(buf);
            buf.close();




            // x,y ????????? ?????? ????????? ?????? ????????? (?????? ????????????)
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    int pixel = bitmap.getPixel(x, y);
                    input[batchNum][x][y][0] = Color.red(pixel) / 1.0f;
                    input[batchNum][x][y][1] = Color.green(pixel) / 1.0f;
                    input[batchNum][x][y][2] = Color.blue(pixel) / 1.0f;
                }
            }

            // tflite ??????
            Interpreter lite = getTfliteInterpreter("model10.tflite");
            lite.run(input, output);

            //????????? ?????? ????????? ?????? ?????????
            dogimage.setScaleType(ImageView.ScaleType.FIT_XY);
            dogimage.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }

         for (i = 0; i < 15; i++)
            if (output[0][i] * 100 > 50) {
                if (i == 0) {
                    dogtype.setText(String.format("??????"));
                } else if (i == 1) {
                    dogtype.setText(String.format("??????"));
                } else if (i ==2 ){
                    dogtype.setText(String.format("?????????"));
                } else if (i ==3 ){
                    dogtype.setText(String.format("????????????"));
                   // dogtype.setText(String.format(" %.5f", output[0][0] * 100));
                } else if (i ==4 ){
                    dogtype.setText(String.format("????????????"));
                } else if (i ==5 ){
                    dogtype.setText(String.format("????????????"));
                } else if (i ==6 ){
                    dogtype.setText(String.format("?????? ????????????"));
                } else if (i==7){
                    dogtype.setText(String.format("???????????? ?????????"));
                } else if (i==8){
                    dogtype.setText(String.format("?????????"));
                } else if (i==9){
                    dogtype.setText(String.format("???????????????"));
                } else if (i==10){
                    dogtype.setText(String.format("??????"));
                } else if (i==11){
                    dogtype.setText(String.format("??????"));
                } else if (i==12){
                    dogtype.setText(String.format("????????????"));
                } else if (i==13){
                    dogtype.setText(String.format("?????????"));
                } else {
                    dogtype.setText(String.format("????????????"));
                }
            } else
                continue;


    }

    public boolean onSupportNavigateUp(){
        onBackPressed();; // ???????????? ????????? ????????????
        return super.onSupportNavigateUp(); // ???????????? ??????
    }


}