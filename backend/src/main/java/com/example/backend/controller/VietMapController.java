package com.example.backend.controller;

import com.example.backend.dto.response.location.VietMapLocationResponse;
import com.example.backend.service.location.VietMapLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vietmap")
public class VietMapController {


        private final VietMapLocationService vietMapLocationService;

        @GetMapping("/reverse")
        public VietMapLocationResponse reverse(@RequestParam BigDecimal lat,
                                               @RequestParam BigDecimal lng) {
            return vietMapLocationService.reverse(lat, lng);
        }

}
