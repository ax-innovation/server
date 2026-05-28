package com.finapi.controller;

import com.finapi.dto.RecommendRequest;
import com.finapi.dto.RecommendResponse;
import com.finapi.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final RecommendService recommendService;

    @PostMapping("/recommend")
    public ResponseEntity<RecommendResponse> recommend(@RequestBody RecommendRequest request) {
        return ResponseEntity.ok(recommendService.recommend(request));
    }
}
