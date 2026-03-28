package com.ceres.hoime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ceres.hoime.entity.Inquiry;
import com.ceres.hoime.entity.User;
import com.ceres.hoime.repository.InquiryRepository;
import com.ceres.hoime.service.InquiryService;

/**
 * 問い合わせ情報のビジネスロジック
 */
@Service
public class InquiryServiceImpl implements InquiryService {

    @Autowired
    private InquiryRepository inquiryRepository;

    @Override
    public Inquiry createInquiry(User user, String inquiryType, String message) {
        Inquiry inquiry = new Inquiry();
        inquiry.setUser(user);
        inquiry.setInquiryType(inquiryType);
        inquiry.setMessage(message);
        return inquiryRepository.save(inquiry);
    }
}
