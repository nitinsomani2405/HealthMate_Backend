package com.labreport.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

//THIS IS MAIN SERVICE CLASS WHERE LOGIC OF UPLODING FILE IN SUPABASE STORAGE LIES
@Service
public class SupabaseService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucket;

    @Value("${supabase.lab-report-bucket-url}")
    private String publicBucketUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String uploadLabReport(UUID doctorId, UUID patientId, MultipartFile file) throws IOException {
        // ✅ Create unique filename
        String filename = String.format("lab-reports/%s/%s/%d.pdf", doctorId, patientId, Instant.now().toEpochMilli());
        // ✅ Upload file via Supabase Storage API
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + filename;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // Sending binary data
        headers.set("Authorization", "Bearer " + supabaseKey);

        HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);

        ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.PUT, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IOException("Failed to upload file to Supabase: " + response.getBody());
        }

        // ✅ Return public file URL
        return publicBucketUrl + "/" + filename;
    }
}
