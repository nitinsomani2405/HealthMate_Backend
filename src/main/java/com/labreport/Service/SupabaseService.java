package com.labreport.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

// THIS IS MAIN SERVICE CLASS WHERE LOGIC OF UPLOADING AND DELETING FILE IN SUPABASE STORAGE LIES
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

    public void deleteFile(String fileUrl) {
        fileUrl = fileUrl.replace(" ", "");
        String publicBucketUrlPrefix = supabaseUrl + "/storage/v1/object/public/" + bucket + "/";

        if (!fileUrl.startsWith(publicBucketUrlPrefix)) {
            throw new IllegalArgumentException("Invalid file URL: " + fileUrl);
        }

        String filePath = fileUrl.substring(publicBucketUrlPrefix.length());
        String deleteUrl = supabaseUrl + "/storage/v1/object/"+bucket+"/" + filePath;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {

            ResponseEntity<Void> response = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, entity, Void.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ File deleted successfully from Supabase: " + filePath);
            } else {
                System.err.println("⚠️ Deletion request returned non-2xx status: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("❌ HTTP error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Unexpected error while deleting from Supabase: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
