package com.pki.example.controller;

import com.pki.example.dto.CertificateDto;
import com.pki.example.dto.CreateCertificateDto;
import com.pki.example.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;

@RestController
@RequestMapping("/certificate")
public class CertificateController {
    @Autowired
    private CertificateService certificateService;

    @GetMapping("/all")
    public List<CertificateDto> getAllCertificates(){
        return certificateService.getAllCertificates();
    }

    @GetMapping("/allRoot")
    public List<CertificateDto> getAllRootCertificates(){
        return certificateService.getAllRootCertificates();
    }

    @GetMapping("/allCa")
    public List<CertificateDto> getAllCaCertificates(){
        return certificateService.getAllCaCertificates();
    }

    @GetMapping("/allEe")
    public List<CertificateDto> getAllEeCertificates(){
        return certificateService.getAllEeCertificates();
    }

    @PostMapping("/create")
    public CertificateDto createCertificate(@Valid @RequestBody CreateCertificateDto createCertificateDto){
        CertificateDto certificateDto = certificateService.createCertificate(createCertificateDto);
        if(certificateDto == null){
            return null;
        }
        return certificateDto;
    }
    @PostMapping("/checkExpired")
    public boolean checkExpired(@Valid @RequestBody CreateCertificateDto createCertificateDto){
        boolean expired = certificateService.checkCertificateExpired(createCertificateDto);
        return expired;
    }
}
