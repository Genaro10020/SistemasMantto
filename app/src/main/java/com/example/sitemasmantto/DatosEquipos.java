package com.example.sitemasmantto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DatosEquipos extends AppCompatActivity {
    TextView nombreEquipo,Textserie,TextoCondiciones;
    RequestQueue requestQueue;
    String encontrado="";
    Button btncamaraAntes, btncamaraDespues,btnPDF;
    int tomandoAntes=0, tomandoDespues=0,listoAntes=0,listoDespues=0;
    private ImageView imageAntes, imageDespues;
    private String currentPhotoPath;
    Bitmap bitmapf,bitmapf2;
    String serie = "";
    String condiciones = "";
    String nom_usuario = "";
    String cuenta = "";
    String departamento = "";
    String nombre_del_equipo = "";
    String marca = "";
    String ubicacion = "";
    String tipo = "";
    String modelo = "";
    String tni = "";
    String version = "";


    private static final int REQUEST_PERMISSION_CAMERA=100;
    private static final int REQUEST_IMAGE_CAMERA=101;
    String URL="https://sheets.googleapis.com/v4/spreadsheets/1WJ_XDSCpDGxgAM1Bnbk36MvJVyXT-pa-k8o1aJXXk3Y/values/Equipos?key=AIzaSyBKivL1kfUQJjmsVpjSgAUbVTElApOsxKA"; //FUNCIONA OBJETO ARRAY ARRAY

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_equipos);
        //Buscar
        nombreEquipo = (TextView)findViewById(R.id.mostrar);
        Textserie=(EditText)findViewById(R.id.EditSerie);
        TextoCondiciones=(EditText)findViewById(R.id.EditCondiciones);
        Button btnBuscar = (Button)findViewById(R.id.ButtonSearch);
        btnBuscar.setOnClickListener(v -> {
            nombreEquipo.setText("");
            recibiendoObjeto();
        });

        //ONCLIK evidencia antes
        imageAntes = (ImageView)findViewById(R.id.ViewImagenAntes);
        btncamaraAntes = (Button)findViewById(R.id.btnCamaraAntes);
        btncamaraAntes.setOnClickListener(view -> {
            tomandoAntes=1;
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                if(ActivityCompat.checkSelfPermission(DatosEquipos.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                    abrirCamara();
                }else{
                    ActivityCompat.requestPermissions(DatosEquipos.this, new String[]{Manifest.permission.CAMERA},REQUEST_PERMISSION_CAMERA);
                }
            }else{
                abrirCamara();
            }
        });
        //ONCLIK evidencia despues
        imageDespues = (ImageView)findViewById(R.id.ViewImagenDespues);
        btncamaraDespues = (Button)findViewById(R.id.btnCamaraDespues);
        btncamaraDespues.setOnClickListener(view -> {
            tomandoDespues=1;
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                if(ActivityCompat.checkSelfPermission(DatosEquipos.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                    abrirCamara();
                }else{
                    ActivityCompat.requestPermissions(DatosEquipos.this, new String[]{Manifest.permission.CAMERA},REQUEST_PERMISSION_CAMERA);
                }
            }else{
                abrirCamara();
            }
        });

        //BTN GUARDAR
        Button btnGuardar = (Button)findViewById(R.id.btnguardar);
        btnGuardar.setOnClickListener(view -> {
            GuadarImgesServidor();
        });
        //BTN DESCARGAR PDF
        btnPDF= (Button)findViewById(R.id.btnpdf);
        btnPDF.setOnClickListener(view -> startActivity(
                new Intent(Intent.ACTION_VIEW, Uri.parse("https://vvnorth.com/Sistemas/toPDF.php"))
        ));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==REQUEST_PERMISSION_CAMERA){
            if(permissions.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                abrirCamara();
            }else{
                Toast.makeText(this,"Necesitas los permisos Habilitalos",Toast.LENGTH_SHORT).show();
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode== REQUEST_IMAGE_CAMERA){
            if(resultCode==Activity.RESULT_OK){

                    if(tomandoAntes==1){
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        bitmapf=bitmap;
                        imageAntes.setImageBitmap(bitmap);
                        Log.i("Bitmap",":"+bitmap);
                        tomandoAntes=0;
                        listoAntes=1;
                    }
                    if(tomandoDespues==1){
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        bitmapf2=bitmap;
                        imageDespues.setImageBitmap(bitmap);
                        Log.i("Bitmap",":"+bitmap);
                        tomandoDespues=0;
                        listoDespues=1;
                    }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void recibiendoObjeto(){
        serie =Textserie.getText().toString();

        Log.e("SERIE ES",""+serie);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("values");
                        int size = jsonArray.length();
                            for (int i=1; i < size; i++){
                                //String posicion = jsonArray.getString(i);
                                JSONArray jsonArray2 = jsonArray.getJSONArray(i);
                                String posicion = jsonArray2.getString(1);
                                if (posicion.equals(serie)){

                                    nom_usuario = jsonArray2.getString(2);//index numero de columna
                                    cuenta = jsonArray2.getString(4);//index numero de columna CUENTA
                                    departamento = jsonArray2.getString(5);//index numero de columna DEPARTAMENTO
                                    nombre_del_equipo = jsonArray2.getString(6);//index numero de columna NOMBRE DEL EQUIPO
                                    marca = jsonArray2.getString(7);//index numero de columna NOMBRE DEL EQUIPO
                                    ubicacion = jsonArray2.getString(8);//index numero de columna UBICACION
                                    tipo = jsonArray2.getString(9);//index numero de columna UBICACION
                                    modelo = jsonArray2.getString(10);//index numero de columna UBICACION
                                    tni = jsonArray2.getString(11);//index numero de columna TNI10
                                    version = jsonArray2.getString(12);//index numero de columna TNI10
                                    nombreEquipo.append("Usuario: "+nom_usuario+"\n"+
                                                        "Cuenta: "+cuenta+"\n"+
                                                        "Departamento: "+departamento+"\n"+
                                                        "Nombre del Equipo: "+nombre_del_equipo+"\n"+
                                                        "Marca: "+marca+"\n"+
                                                        "Ubicacion: "+ubicacion+"\n"+
                                                        "Tipo: "+tipo+"\n"+
                                                        "Modelo: "+modelo+"\n"+
                                                        "TNI10: "+tni+"\n"+
                                                        "Version: "+version+"\n");
                                                        encontrado="1";
                                }
                            }

                        Log.e("ENTRe ;-)","."+size);
                    } catch (JSONException e) {
                        e.printStackTrace();
                            if (encontrado.equals("")){
                                Log.e("Vacio",";"+encontrado);
                                Toast toast = Toast.makeText(getApplicationContext(),"No se encontro la serie",Toast.LENGTH_SHORT);
                                toast.show();
                            }
                    }

                },
                error -> {
                    Log.e("ERROR ;-(",".");
                }

        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    @SuppressWarnings("deprecation")
    private void abrirCamara(){
        String fileName="photo";
        File StorageDirectory= getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile=File.createTempFile(fileName,".jpg",StorageDirectory);
            currentPhotoPath=imageFile.getAbsolutePath();

            /*Uri imageUri=  FileProvider.getUriForFile(DatosEquipos.this,
                    "com.example.a5SGonher.fileprovider",imageFile);
            imageFile = File.createTempFile(fileName,".jpg",StorageDirectory);*/

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
            //startActivityForResult(intent, REQUEST_IMAGE_CAMERA);

            if(intent.resolveActivity(getPackageManager())!= null){
                startActivityForResult(intent, REQUEST_IMAGE_CAMERA);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        private void GuadarImgesServidor()
        {
            condiciones=TextoCondiciones.getText().toString();
            StringRequest stringRequest=new  StringRequest(Request.Method.POST, "https://vvnorth.com/Sistemas/subir_informe.php", response -> {
                Toast toast = Toast.makeText(getApplicationContext(),"LOS DATOS SE GUARDARON CON EXITO..",Toast.LENGTH_SHORT);
                toast.show();
                btnPDF.setVisibility(View.VISIBLE);


            }, error -> Toast.makeText(getApplicationContext(), "ALGO SALIO MAL AL GUARDARA", Toast.LENGTH_SHORT).show())
            {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> parametros = new HashMap<>();


                    parametros.put("serie",serie);
                    parametros.put("condiciones",condiciones);
                    parametros.put("nom_usuario",nom_usuario);
                    parametros.put("cuenta",cuenta);
                    parametros.put("departamento",departamento);
                    parametros.put("nombre_del_equipo",nombre_del_equipo);
                    parametros.put("marca",marca);
                    parametros.put("ubicacion",ubicacion);
                    parametros.put("tipo",tipo);
                    parametros.put("modelo",modelo);
                    parametros.put("tni",tni);
                    parametros.put("version",version);

                    if(listoAntes==1)
                    { String imageData= imageToString(bitmapf);
                        parametros.put("image",imageData);
                        Log.e("ENVIANDO","");}

                    if(listoDespues==1)
                    { String imageData2= imageToString(bitmapf2);
                        parametros.put("image2",imageData2);
                        Log.e("ENVIANDO","");}

                    return parametros;
                }
            };
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }

    private String imageToString(Bitmap bitmap) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, outputStream);

        byte[] imageBytes= outputStream.toByteArray();
        String encodeImage= Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return encodeImage;

    }


}