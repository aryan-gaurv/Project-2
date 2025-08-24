package com.example.authify.controller;

import com.example.authify.io.AuthRequest;
import com.example.authify.io.AuthResponse;
import com.example.authify.io.ResetPasswordRequest;
import com.example.authify.service.AppUserDetailsService;
import com.example.authify.service.ProfileService;
import com.example.authify.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0")
public class AuthController{

   final AuthenticationManager authenticationManager;

    final AppUserDetailsService appUserDetailsService;

    final ProfileService profileService;

   final JwtUtil jwt;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){
        try {
            authenticate(request.getEmail(),request.getPassword());
            UserDetails userDetails = appUserDetailsService.loadUserByUsername(request.getEmail());
            String jwtToken = jwt.generateToken(userDetails);
            ResponseCookie cookie=ResponseCookie.from("jwt",jwtToken)
                    .httpOnly(true).path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Strict").build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new AuthResponse(request.getEmail(),jwtToken));

        }catch ( BadCredentialsException b){
            Map<String,Object> error=new HashMap<>();
            error.put("error",true);
            error.put("message","Email or Password is incorrect");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }catch ( DisabledException b){
            Map<String,Object> error=new HashMap<>();
            error.put("error",true);
            error.put("message","Account id disabled");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }catch ( Exception b){
            Map<String,Object> error=new HashMap<>();
            error.put("error",true);
            error.put("message","FAILED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        
    }

    private void authenticate(String email, String password) {
        
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
    }
    @GetMapping("/is-authenticated")
    private ResponseEntity<Boolean> isAuthenticated(@CurrentSecurityContext(expression = "authentication?.name") String email){
        return ResponseEntity.ok(email!=null);
    }
    @PostMapping("/send-reset-otp")
    public void sendResetOtp(@RequestParam String email){
        try{
            profileService.sendRestOtp(email);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }
    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        try{
            profileService.resetPassword(request.getEmail(),request.getOtp(),request.getNewPassword());
        }catch (Exception e){
         throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }

    @PostMapping("/send-otp")
    public void sendVerifyOtp(@CurrentSecurityContext(expression = "authentication?.name") String email){
        try{
            profileService.sendOtp(email);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }
    @PostMapping("/verify-otp")
    public void VerifyEmail(@RequestBody Map<String,Object> request,
                            @CurrentSecurityContext(expression = "authentication?.name")String email){
        if(request.get("otp").toString()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"otp is required");
        }
        try{
            profileService.verifyOtp(email,request.get("otp").toString());
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response){
        ResponseCookie cookie =ResponseCookie.from("jwt","")
                .httpOnly(true).secure(false).path("/")
                .maxAge(0).sameSite("Strict")
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString())
                .body("Logout successful");
    }
}
