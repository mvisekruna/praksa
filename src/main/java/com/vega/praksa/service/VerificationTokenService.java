package com.vega.praksa.service;

import com.vega.praksa.model.VerificationToken;

public interface VerificationTokenService {

    VerificationToken findByToken(String token);
    void saveVerificationToken(VerificationToken token);

}
