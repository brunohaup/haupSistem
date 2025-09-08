package com.haupsystem.service;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucket;

    private final OkHttpClient client = new OkHttpClient();

    // Upload de arquivo
    public String uploadFile(String filename, byte[] fileBytes) throws IOException {
        String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + filename;

        RequestBody body = RequestBody.create(fileBytes, MediaType.parse("application/octet-stream"));

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + supabaseKey)
                .header("apikey", supabaseKey)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro no upload: " + response);
            }
            // URL pública do arquivo
            return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + filename;
        }
    }

    // Download de arquivo
    public byte[] downloadFile(String filename) throws IOException {
        String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + filename;

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + supabaseKey)
                .header("apikey", supabaseKey)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro no download: " + response);
            }
            return response.body().bytes();
        }
    }
}
