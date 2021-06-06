package com.example.recuperacionuf2_sergiopujol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button btnCamara;
    ImageView imgView;
    Button btnComent;
    public static ArrayList<items> datos = new ArrayList<items>(); //Arraylist que contendra todos los datos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Variables
        btnCamara = findViewById(R.id.btnCamara);
        imgView = findViewById(R.id.imageView_foto);
        btnComent = findViewById(R.id.btnEdit);

        //Mostramos los datos al iniciar la aplicacion
        ListView mostrarDatos = findViewById(R.id.lvFotos);
        ArrayAdapter adapter = new adaptador(this, MainActivity.datos);
        mostrarDatos.setAdapter(adapter);

        //Permisos de camara
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1000);
        }
        leerDatos(MainActivity.this);//Leemos los datos que tenemos guardados

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomarFoto();
            }
        });

        btnComent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

    }
    public void openDialog() {
        ExampleDialog dialog = new ExampleDialog();
        dialog.show(getSupportFragmentManager(), "Sep");

    }


    static final int REQUEST_TAKE_PHOTO = 1;
    private void tomarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = crearArchivoImagen();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.recuperacionuf2_sergiopujol", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    String currentPhotoPath;
    public File crearArchivoImagen() throws IOException {
        String tiempo = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "Foto_"+tiempo+"_";
        File storage = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageName,".jpg",storage);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static void leerDatos(Context context) {
        File archivo = new File(context.getFilesDir(), "datos.txt");
        try {
            if (archivo.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo));
                Object obj = ois.readObject();
                while (obj!=null) {
                    items it = (items) obj;
                    datos.add(it); //Agrego los datos al arraylist
                    obj = ois.readObject();
                }
                ois.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(context,"Imagenes y comentarios bien cargados.",Toast.LENGTH_SHORT).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    String comentario = ""; //Pone comentario vacio en la foto
    static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            items i = new items(comentario, currentPhotoPath);
            datos.add(i); //Agrego al arraylist los datos
            guardarDatos(MainActivity.this); //Guardamos los datos en un fichero
            setContentView(R.layout.activity_main);

            ListView mostrarDatos = findViewById(R.id.lvFotos);
            ArrayAdapter adapter = new adaptador(this, MainActivity.datos);
            mostrarDatos.setAdapter(adapter);

            //Volvemos a crear el boton debido a que volvemos a la misma pantalla inicial y el boton queda inutil.
            Button btnCamara = findViewById(R.id.btnCamara);
            btnCamara.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tomarFoto();
                }
            });
        }
    }


    public static void guardarDatos(Context context) {
        File archivo = new File(context.getFilesDir(), "datos.txt");
        try {
            if (!archivo.exists()) {//Creamos el archivo en caso de que este no exista ya.
                archivo.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(archivo);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            //A continuacion recorremos y serializamos array
            for (int j = 0; j < datos.size(); j++) {
                oos.writeObject(datos.get(j));
            }
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String incomentaro; //Guarda el comentario del usuario
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.comentariovisual, null);

        //Boton del dialog
        builder.setView(view)
                .setPositiveButton("Introducir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText et = view.findViewById(R.id.infoComentario);
                        incomentaro = et.getText().toString(); //GUARDO EL NOMBRE QUE HA INTRODUCIDO EL USUARIO EN ESTA VARIABLE
                        dismiss();
                    }
                });
        return builder.create();
    }

    public void dismiss() {}
}