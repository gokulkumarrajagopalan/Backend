package com.tally.service;

import com.tally.entity.User;
import com.tally.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class MobileOtpService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SmsService smsService;

    private final Random random = new Random();
    private static final int MAX_SMS_PER_DAY = 3;

    @Transactional
    public boolean sendMobileOtp(String mobile) {
        User user = userRepository.findByMobile(mobile);
        if (user == null) {
            throw new RuntimeException("Mobile number not registered");
        }

        // Check SMS limit (3 per 24 hours)
        LocalDate today = LocalDate.now();
        if (user.getSmsLastSentDate() != null && user.getSmsLastSentDate().equals(today)) {
            if (user.getSmsCountToday() >= MAX_SMS_PER_DAY) {
                throw new RuntimeException("SMS limit exceeded. Maximum 3 SMS allowed per 24 hours");
            }
        } else {
            // Reset count for new day
            user.setSmsCountToday(0);
            user.setSmsLastSentDate(today);
        }

        String otp = String.format("%06d", random.nextInt(1000000));
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        user.setOtpSentAt(LocalDateTime.now());
        user.setOtpVerified(false);
        user.setSmsCountToday(user.getSmsCountToday() + 1);
        
        userRepository.save(user);

        String message = "Your Tally verification code is: " + otp + ". Valid for 5 minutes.";
        return smsService.sendSms(mobile, message);
    }

    @Transactional
    public boolean verifyMobileOtp(String mobile, String otp) {
        User user = userRepository.findByMobile(mobile);
        if (user == null || user.getOtpCode() == null) {
            return false;
        }

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (user.getOtpCode().equals(otp)) {
            user.setOtpVerified(true);
            user.setMobileVerified(true);
            user.setOtpCode(null);
            user.setOtpExpiry(null);
            userRepository.save(user);
            return true;
        }

        return false;
    }
}