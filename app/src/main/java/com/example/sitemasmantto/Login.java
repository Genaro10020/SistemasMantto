package com.example.sitemasmantto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    EditText user, pass;
    String usuario, contrasenia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btn_enviar = (Button)findViewById(R.id.btn_iniciar);

        user = (EditText)findViewById(R.id.editUser);
        pass = (EditText)findViewById(R.id.editContrasena);
        btn_enviar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                VerificarUsuario();
            }
        });
    }

    public  void VerificarUsuario(){
        usuario = user.getText().toString();
        contrasenia = pass.getText().toString();

        String url = "https://vvnorth.com/Sistemas/consulta_usuario.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Login.this, "" + response, Toast.LENGTH_LONG).show();
                        if(response.contains("SOY YO") || response.contains("HOLA SOLECITO")){
                            actividadDatosdelEquipo();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this, "ERROR", Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<String,String>();
                params.put("usuario",usuario);
                params.put("contrasena",contrasenia);
                return  params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(Login.this);
        requestQueue.add(stringRequest);
    }

    public void actividadDatosdelEquipo(){
        Intent datosEquipo = new Intent(getApplicationContext(),DatosEquipos.class);
        startActivity(datosEquipo);
    }
}