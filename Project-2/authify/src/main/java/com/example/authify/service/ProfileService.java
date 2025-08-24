package com.example.authify.service;

import com.example.authify.io.ProfileRequest;
import com.example.authify.io.ProfileResponse;

public interface ProfileService {
   ProfileResponse createProfile(ProfileRequest request);
    ProfileResponse getProfile(String email);
    void sendRestOtp(String email);
    void resetPassword(String email,String otp,String newpassword);
    void sendOtp(String email);
    void verifyOtp(String email,String otp);
    String getLoggedInUserId(String email);

}
