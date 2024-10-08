package com.example.dgpaysproje.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dgpaysproje.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // FirebaseAuth instance'ını başlatıyoruz
        auth = FirebaseAuth.getInstance();

        // Eğer kullanıcı giriş yapmışsa HomeActivity'ye yönlendirme
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Şifre görünürlüğünü değiştiren fonksiyon
    public void togglePasswordVisibility(View view) {
        if (isPasswordVisible) {
            // Şifreyi gizle
            binding.passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            binding.hidePassword.setVisibility(View.VISIBLE);
            binding.showPassword.setVisibility(View.GONE);
        } else {
            // Şifreyi göster
            binding.passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            binding.hidePassword.setVisibility(View.GONE);
            binding.showPassword.setVisibility(View.VISIBLE);
        }
        isPasswordVisible = !isPasswordVisible;
        binding.passwordText.setSelection(binding.passwordText.length());
    }

    // Giriş yap butonuna tıklanıldığında çalışacak fonksiyon
    public void signInClicked(View view) {
        String email = binding.emailText.getText().toString();
        String password = binding.passwordText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email ve Şifre Giriniz !", Toast.LENGTH_SHORT).show();
        } else {
            // Giriş işlemi
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    // Giriş başarılı olduğunda HomeActivity'ye yönlendirme
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Giriş başarısızsa hata mesajını göster
                    Toast.makeText(MainActivity.this, "Giriş Başarısız: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Kayıt ol butonuna tıklanıldığında çalışacak fonksiyon
    public void signUpClicked(View view) {
        String email = binding.emailText.getText().toString();
        String password = binding.passwordText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email ve Şifre Giriniz !", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Şifre en az 6 karakter olmalıdır!", Toast.LENGTH_SHORT).show();
        } else {
            // Kullanıcı kaydı işlemi
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    // Kayıt başarılı olduğunda HomeActivity'ye yönlendirme
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Kayıt başarısızsa hata mesajını göster
                    Toast.makeText(MainActivity.this, "Kayıt Başarısız: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
