package com.example.authify.service;

import com.example.authify.entity.UserEntity;
import com.example.authify.io.ProfileRequest;
import com.example.authify.io.ProfileResponse;
import com.example.authify.repository.UserRepostory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileServiceimpl implements ProfileService {

    final UserRepostory userRepostory;

    final PasswordEncoder passwordEncoder;

    final EmailService emailService;
    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        UserEntity newProfile=convertToUserEntity(request);
        if(!userRepostory.existsByEmail(request.getEmail())){
            newProfile = userRepostory.save(newProfile);
            return convertToProfileResponse(newProfile);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already existes");

    }

    @Override
    public ProfileResponse getProfile(String email) {
        UserEntity userNotFound = userRepostory.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return convertToProfileResponse(userNotFound);
    }

    @Override
    public void sendRestOtp(String email) {
        UserEntity userNotFound = userRepostory.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));
        long expire = System.currentTimeMillis() + (15 * 60 * 1000);
        userNotFound.setResetOtp(otp);
        userNotFound.setResetOtpExpireAt(expire);
        userRepostory.save(userNotFound);

        try {

            emailService.sendResetOtp(userNotFound.getEmail(), otp);

        }catch (Exception e){
            throw new RuntimeException("Unable to send send Email");
        }

    }

    @Override
    public void resetPassword(String email, String otp,String newpassword) {
        UserEntity userNotFound = userRepostory.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        if(userNotFound.getResetOtp()==null || userNotFound.getResetOtp().equals(otp)){
             throw new RuntimeException("Invalid");
        }
        if(userNotFound.getResetOtpExpireAt()<System.currentTimeMillis()){
            throw new RuntimeException("OTP EXPIRED");
        }
        userNotFound.setPassword(passwordEncoder.encode(newpassword));
        userNotFound.setResetOtp(null);
        userNotFound.setResetOtpExpireAt(0L);
        userRepostory.save(userNotFound);

    }

    @Override
    public void sendOtp(String email) {
        UserEntity userNotFound = userRepostory.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(userNotFound.getIsAccountVerified()!=null && userNotFound.getIsAccountVerified()){
            return ;
        }
        String s = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        long l = System.currentTimeMillis() + (24*60 * 60 * 1000);

        userNotFound.setVerifyOtp(s);
        userNotFound.setVerifyOtpExpireAt(l);
        userRepostory.save(userNotFound);
        try{
            emailService.sendOtpEmail(userNotFound.getEmail(), s);
        }catch (Exception e){
            throw new RuntimeException("Unable to send email");
        }

    }

    @Override
    public void verifyOtp(String email, String otp) {
        UserEntity userNotFound = userRepostory.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(userNotFound.getVerifyOtp()==null || !userNotFound.getVerifyOtp().equals(otp)){
            throw new RuntimeException("Invalid OTP");
        }
        if(userNotFound.getVerifyOtpExpireAt()<System.currentTimeMillis()){
            throw new RuntimeException("OTP EXPIRED");
        }
        userNotFound.setIsAccountVerified(true);
        userNotFound.setVerifyOtp(null);
        userNotFound.setVerifyOtpExpireAt(0L);
        userRepostory.save(userNotFound);
    }

    @Override
    public String getLoggedInUserId(String email) {
        UserEntity userNotFound = userRepostory.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userNotFound.getUserId();
    }

    private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
         return ProfileResponse.builder()
                .name(newProfile.getName())
                .email(newProfile.getEmail())
                .userId(newProfile.getUserId())
                .isAccountverified(newProfile.getIsAccountVerified())
                .build();
    }

    private UserEntity convertToUserEntity(ProfileRequest request) {
        return UserEntity.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null).build();
    }
}
