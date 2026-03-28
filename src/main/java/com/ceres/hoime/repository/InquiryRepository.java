package com.ceres.hoime.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ceres.hoime.entity.Inquiry;

/**
 * 問い合わせ情報リポジトリ
 */
public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {
}
