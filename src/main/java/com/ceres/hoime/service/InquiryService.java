package com.ceres.hoime.service;

import com.ceres.hoime.entity.Inquiry;
import com.ceres.hoime.entity.User;

public interface InquiryService {

    Inquiry createInquiry(User user, String inquiryType, String message);

}
