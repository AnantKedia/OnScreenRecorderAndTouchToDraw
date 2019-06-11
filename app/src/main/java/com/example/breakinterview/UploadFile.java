package com.example.breakinterview;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class UploadFile extends AppCompatActivity {

    Button btnRead , btnSave;
    EditText txtInput;
    TextView txtContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_upload);

        txtContent = (TextView) findViewById(R.id.txtContent);
        txtInput = (EditText) findViewById(R.id.txtInput);

        btnRead = (Button) findViewById(R.id.btnRead);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtContent.setText(FileHelper.ReadFile(UploadFile.this));
            }
        });

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FileHelper.saveToFile( txtInput.getText().toString())){
                    Toast.makeText(UploadFile.this,"Saved to file",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(UploadFile.this,"File could not be saved. Come back later",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
