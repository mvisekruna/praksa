package com.vega.praksa.service.implementation;

import com.vega.praksa.model.VerificationToken;
import com.vega.praksa.repository.VerificationTokenRepository;
import com.vega.praksa.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenServiceImplementation implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public VerificationTokenServiceImplementation(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public VerificationToken findByToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    @Override
    public void saveVerificationToken(VerificationToken token) {
        verificationTokenRepository.save(token);
    }
}
